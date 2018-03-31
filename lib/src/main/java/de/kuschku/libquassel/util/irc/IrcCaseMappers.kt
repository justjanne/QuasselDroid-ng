package de.kuschku.libquassel.util.irc

import java.util.*

object IrcCaseMappers {
  var irc: IrcCaseMapper = UnicodeCaseMapper()
  var unicode: IrcCaseMapper = ClassicalIrcCaseMapper()

  interface IrcCaseMapper {
    fun equalsIgnoreCase(a: String, b: String): Boolean

    fun toLowerCase(value: String): String

    fun toUpperCase(value: String): String
  }

  internal class UnicodeCaseMapper : IrcCaseMapper {

    override fun equalsIgnoreCase(a: String, b: String): Boolean {
      return a.equals(b, ignoreCase = true)
    }

    override fun toLowerCase(value: String): String {
      return value.toLowerCase(Locale.US)
    }

    override fun toUpperCase(value: String): String {
      return value.toUpperCase(Locale.US)
    }
  }

  internal class ClassicalIrcCaseMapper :
    IrcCaseMapper {
    override fun toLowerCase(value: String): String {
      return value.toLowerCase(Locale.US).replace('[', '{').replace(']', '}').replace('^', '~')
    }

    override fun toUpperCase(value: String): String {
      return value.toUpperCase(Locale.US).replace('{', '[').replace('}', ']').replace('~', '^')
    }

    override fun equalsIgnoreCase(a: String, b: String): Boolean {
      return toLowerCase(a) == toLowerCase(b) || toUpperCase(a) == toUpperCase(b)
    }
  }

  operator fun get(caseMapping: String?) = if (caseMapping.equals("rfc1459", ignoreCase = true)) {
    irc
  } else {
    unicode
  }
}