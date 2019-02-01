/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.libquassel.quassel.syncables

import de.kuschku.libquassel.protocol.QVariant
import de.kuschku.libquassel.protocol.QVariantMap
import de.kuschku.libquassel.protocol.Type
import de.kuschku.libquassel.protocol.valueOr
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager.Alias
import de.kuschku.libquassel.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.libquassel.session.SignalProxy
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class AliasManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "AliasManager"), IAliasManager, ISyncableObject {
  override fun toVariantMap(): QVariantMap = mapOf(
    "Aliases" to QVariant.of(initAliases(), Type.QVariantMap)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetAliases(properties["Aliases"].valueOr(::emptyMap))
  }

  override fun initAliases(): QVariantMap = mapOf(
    "names" to QVariant.of(_aliases.map(Alias::name), Type.QStringList),
    "expansions" to QVariant.of(_aliases.map(Alias::expansion), Type.QStringList)
  )

  override fun initSetAliases(aliases: QVariantMap) {
    val names = aliases["names"].valueOr<List<String>>(::emptyList)
    val expansions = aliases["expansions"].valueOr<List<String>>(::emptyList)

    if (names.size != expansions.size)
      throw IllegalArgumentException(
        "Sizes do not match: names=${names.size}, expansions=${expansions.size}"
      )

    _aliases = names.zip(expansions, ::Alias).toList()
  }

  override fun addAlias(name: String?, expansion: String?) {
    if (contains(name)) {
      return
    }

    _aliases += Alias(name, expansion)

    super.addAlias(name, expansion)
  }

  fun indexOf(name: String?) = _aliases.map(Alias::name).indexOf(name)

  fun contains(name: String?) = _aliases.map(Alias::name).contains(name)

  fun aliasList() = _aliases

  fun setAliasList(list: List<Alias>) {
    _aliases = list
  }

  fun updates(): Observable<AliasManager> = live_updates.map { this }

  fun copy() = AliasManager(proxy).also {
    it.fromVariantMap(toVariantMap())
  }

  fun processInput(info: BufferInfo, message: String): List<IAliasManager.Command> {
    val result = mutableListOf<IAliasManager.Command>()
    processInput(info, message, result)
    return result
  }

  fun processInput(info: BufferInfo, message: String,
                   previousCommands: MutableList<IAliasManager.Command>) {
    var msg = message

    // leading slashes indicate there's a command to call unless there is another one in the first section (like a path /proc/cpuinfo)
    // For those habitally tied to irssi, "/ " also makes the rest of the line a literal message
    val secondSlashPos = msg.indexOf('/', 1)
    val firstSpacePos = msg.indexOf(' ')
    if (!msg.startsWith('/') || firstSpacePos == 1 ||
        (secondSlashPos != -1 && (secondSlashPos < firstSpacePos || firstSpacePos == -1))) {
      if (msg.startsWith("//"))
        msg = msg.substring(1) // "//asdf" is transformed to "/asdf"
      else if (msg.startsWith("/ "))
        msg = msg.substring(2) // "/ /asdf" is transformed to "/asdf"
      msg = "/SAY $msg" // make sure we only send proper commands to the core
    } else {
      // check for aliases
      val split = msg.split(' ', ignoreCase = true, limit = 2)
      val search: String? = split.firstOrNull()?.substring(1)
      if (search != null) {
        val found = _aliases.firstOrNull { it.name.equals(search, true) }
        if (found != null) {
          expand(found.expansion ?: "", info, split.getOrNull(1) ?: "", previousCommands)
          return
        }
      }
    }

    previousCommands.add(IAliasManager.Command(info, msg))
  }

  fun expand(expansion: String, bufferInfo: BufferInfo, msg: String,
             previousCommands: MutableList<IAliasManager.Command>) {
    val network = proxy.network(bufferInfo.networkId)

    val paramRange = Regex("""\$(\d+)\.\.(\d*)""")
    val commands = expansion.split("; ?".toRegex()).dropLastWhile(String::isEmpty)
    val params = msg.split(' ').dropLastWhile(String::isEmpty)
    val expandedCommands = LinkedList<String>()

    for (i in commands.indices) {
      var command = commands[i]

      if (params.isNotEmpty()) {
        val commandBuffer = StringBuilder()
        var index = 0
        for (match in paramRange.findAll(command)) {
          val start = match.groups[1]?.value?.toIntOrNull() ?: 0
          val replacement: String
          val end = match.groups[2]?.value?.toIntOrNull() ?: params.size
          // $1.. would be "arg1 and all following"
          replacement = if (end < start) {
            ""
          } else {
            params.subList(start - 1, end).joinToString(" ")
          }

          // Append text between last match and this match
          commandBuffer.append(command.substring(index, match.range.start))

          // Append new replacement text
          commandBuffer.append(replacement)

          index = match.range.endInclusive + 1
        }
        // Append remaining text
        commandBuffer.append(command.substring(index, command.length))
        command = commandBuffer.toString()
      }

      for (j in params.size downTo 1) {
        val user = network?.ircUser(params[j - 1])
        // Hostname, or "*" if blank/nonexistent
        command = command.replace("\$$j:hostname", user?.host() ?: "*")
        // Identd
        // Ident if verified, or "*" if blank/unknown/unverified (prefixed with "~")
        //
        // Most IRC daemons have the option to prefix an ident with "~" if it could not be
        // verified via an identity daemon such as oidentd.  In these cases, it can be handy to
        // have a way to ban via ident if verified, or all idents if not verified.  If the
        // server does not verify idents, it usually won't add "~".
        //
        // Identd must be replaced before ident to avoid being treated as "$i:ident" + "d"
        command = command.replace("\$$j:identd", (user?.user()).let {
          when {
            it == null ||
            it.startsWith(prefix = "~") -> "*"
            else                        -> it
          }
        })
        // Ident, or "*" if blank/nonexistent
        command = command.replace("\$$j:ident", user?.user() ?: "*")
        // Account, or "*" if blank/nonexistent/logged out
        command = command.replace("\$$j:account", user?.account() ?: "*")
        // Nickname
        // Must be replaced last to avoid interferring with more specific aliases
        command = command.replace("\$$j", params[j - 1])
      }
      command = command.replace("$0", msg)
      command = command.replace("\$channelname", bufferInfo.bufferName ?: "")
      command = command.replace("\$channel", bufferInfo.bufferName ?: "")
      command = command.replace("\$currentnick", network?.myNick() ?: "")
      command = command.replace("\$nick", network?.myNick() ?: "")
      command = command.replace("\$network", network?.networkName() ?: "")
      expandedCommands.add(command)
    }
    while (!expandedCommands.isEmpty()) {
      val command: String
      if (expandedCommands[0].trim().toLowerCase(Locale.US).startsWith("/wait ")) {
        command = expandedCommands.joinToString("; ")
        expandedCommands.clear()
      } else {
        command = expandedCommands.removeAt(0)
      }
      previousCommands.add(IAliasManager.Command(bufferInfo, command))
    }
  }

  private val live_updates = BehaviorSubject.createDefault(Unit)
  private var _aliases = listOf<IAliasManager.Alias>()
    set(value) {
      field = value
      live_updates.onNext(Unit)
    }

  fun isEqual(other: AliasManager): Boolean =
    this.aliasList() == other.aliasList()

  override fun toString(): String {
    return "AliasManager(_aliases=$_aliases)"
  }

  companion object {
    fun defaults() = listOf(
      Alias("j", "/join $0"),
      Alias("ns", "/msg nickserv $0"),
      Alias("nickserv", "/msg nickserv $0"),
      Alias("cs", "/msg chanserv $0"),
      Alias("chanserv", "/msg chanserv $0"),
      Alias("hs", "/msg hostserv $0"),
      Alias("hostserv", "/msg hostserv $0"),
      Alias("wii", "/whois $0 $0"),
      Alias("back", "/quote away"),

      // let's add aliases for scripts that only run on linux
      Alias("inxi", "/exec inxi $0"),
      Alias("sysinfo", "/exec inxi -d")
    )
  }
}
