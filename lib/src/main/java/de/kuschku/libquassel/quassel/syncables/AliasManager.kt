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

  override fun addAlias(name: String, expansion: String) {
    if (contains(name)) {
      return
    }

    _aliases += Alias(name, expansion)

    super.addAlias(name, expansion)
  }

  fun indexOf(name: String) = _aliases.map(Alias::name).indexOf(name)

  fun contains(name: String) = _aliases.map(Alias::name).contains(name)

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

  fun aliasList() = _aliases

  fun setAliasList(list: List<Alias>) {
    _aliases = list
  }

  fun copy() = AliasManager(proxy).also {
    it.fromVariantMap(toVariantMap())
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
        msg = msg.substring(1)  // "//asdf" is transformed to "/asdf"
      else if (msg.startsWith("/ "))
        msg.substring(2)  // "/ /asdf" is transformed to "/asdf"
      msg = "/SAY $msg" // make sure we only send proper commands to the core
    } else {
      // check for aliases
      val split = msg.split(' ', ignoreCase = true, limit = 2)
      val search: String? = split.firstOrNull()?.substring(1)
      if (search != null) {
        val found = _aliases.firstOrNull { it.name.equals(search, true) }
        if (found != null) {
          expand(found.expansion, info, split.getOrNull(1) ?: "", previousCommands)
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
    val commands = expansion.split("; ?".toRegex()).dropLastWhile { it.isEmpty() }
    val params = msg.split(' ').dropLastWhile({ it.isEmpty() })
    val expandedCommands = LinkedList<String>()

    for (i in commands.indices) {
      var command = commands[i]

      if (params.isNotEmpty()) {
        for (match in paramRange.findAll(command)) {
          val start = match.groups[1]?.value?.toIntOrNull() ?: -1
          val replacement: String
          // $1.. would be "arg1 and all following"
          replacement = if (match.groups[2]?.value.isNullOrEmpty()) {
            params.subList(start, params.size).joinToString(" ")
          } else {
            val end = match.groups[2]?.value?.toIntOrNull() ?: -1
            if (end < start) {
              ""
            } else {
              params.subList(start, end).joinToString(" ")
            }
          }
          command = command.substring(0, match.range.start) + replacement +
            command.substring(match.range.endInclusive + 1)
        }
      }

      for (j in params.size downTo 1) {
        val user = network?.ircUser(params[j - 1])
        command = command.replace("\$$j:hostname", user?.host() ?: "*")
        command = command.replace("\$$j:ident", user?.user() ?: "*")
        command = command.replace("\$$j:account", user?.account() ?: "*")
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

  private var _aliases = listOf<IAliasManager.Alias>()
}
