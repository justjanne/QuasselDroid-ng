package de.kuschku.quasseldroid_ng.quassel.syncables

import de.kuschku.quasseldroid_ng.protocol.QVariantMap
import de.kuschku.quasseldroid_ng.protocol.QVariant_
import de.kuschku.quasseldroid_ng.protocol.Type
import de.kuschku.quasseldroid_ng.protocol.valueOr
import de.kuschku.quasseldroid_ng.quassel.BufferInfo
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.IAliasManager.Alias
import de.kuschku.quasseldroid_ng.quassel.syncables.interfaces.ISyncableObject
import de.kuschku.quasseldroid_ng.session.SignalProxy
import java.util.*
import java.util.regex.Pattern

class AliasManager constructor(
  proxy: SignalProxy
) : SyncableObject(proxy, "AliasManager"), IAliasManager, ISyncableObject {
  override fun toVariantMap(): QVariantMap = mapOf(
    "Aliases" to QVariant_(initAliases(), Type.QVariantMap)
  )

  override fun fromVariantMap(properties: QVariantMap) {
    initSetAliases(properties["Aliases"].valueOr(::emptyMap))
  }

  override fun initAliases(): QVariantMap = mapOf(
    "names" to QVariant_(_aliases.map(Alias::name), Type.QStringList),
    "expansions" to QVariant_(_aliases.map(Alias::expansion), Type.QStringList)
  )

  override fun initSetAliases(aliases: QVariantMap) {
    val names = aliases["names"].valueOr<List<String>>(::emptyList)
    val expansions = aliases["expansions"].valueOr<List<String>>(::emptyList)

    if (names.size != expansions.size)
      throw IllegalArgumentException(
        "Sizes do not match: names=${names.size}, expansions=${expansions.size}")

    _aliases.clear()
    _aliases.addAll(names.zip(expansions, ::Alias))
  }

  override fun addAlias(name: String, expansion: String) {
    if (contains(name)) {
      return
    }

    _aliases.add(Alias(name, expansion))

    super.addAlias(name, expansion)
  }

  fun indexOf(name: String) = _aliases.map(Alias::name).indexOf(name)

  fun contains(name: String) = _aliases.map(Alias::name).contains(name)

  fun defaults() = listOf<Alias>(
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
      val search: String = split.firstOrNull()
        ?: return
      val found = _aliases.firstOrNull { it.name.equals(search, true) }
        ?: return
      expand(found.expansion, info, split.getOrNull(1) ?: "", previousCommands)
    }

    previousCommands.add(IAliasManager.Command(info, msg))
  }

  fun expand(expansion: String, bufferInfo: BufferInfo, msg: String,
             previousCommands: MutableList<IAliasManager.Command>) {
    val network = proxy.network(bufferInfo.networkId)

    val paramRange = Pattern.compile("""\$(\d+)\.\.(\d*)""")
    val commands = Arrays.asList(
      *expansion.split("; ?").dropLastWhile { it.isEmpty() }.toTypedArray())
    val params = Arrays.asList<String>(
      *msg.split(' ').dropLastWhile({ it.isEmpty() }).toTypedArray())
    val expandedCommands = LinkedList<String>()

    for (i in commands.indices) {
      var command = commands[i]

      if (params.size != 0) {
        val m = paramRange.matcher(command)
        while (m.find()) {
          val start = m.group(1).toIntOrNull() ?: -1
          val replacement: String
          // $1.. would be "arg1 and all following"
          replacement = if (m.group(2).isEmpty()) {
            params.subList(start, params.size).joinToString(" ")
          } else {
            val end = m.group(2).toIntOrNull() ?: -1
            if (end < start) {
              ""
            } else {
              params.subList(start, end).joinToString(" ")
            }
          }
          command = command.substring(0, m.start()) + replacement + command.substring(m.end())
        }
      }

      for (j in params.size downTo 1) {
        val user = network?.ircUser(params[j - 1])
        val host = user?.host() ?: "*"
        command = command.replace(String.format(Locale.US, "$%d:hostname", j).toRegex(), host)
        command = command.replace(String.format(Locale.US, "$%d", j).toRegex(), params[j - 1])
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
      if (expandedCommands[0].trim { it <= ' ' }.toLowerCase(Locale.US).startsWith("/wait ")) {
        command = expandedCommands.joinToString("; ")
        expandedCommands.clear()
      } else {
        command = expandedCommands[0]
      }
      previousCommands.add(IAliasManager.Command(bufferInfo, command))
    }
  }

  private val _aliases = mutableListOf<IAliasManager.Alias>()
}
