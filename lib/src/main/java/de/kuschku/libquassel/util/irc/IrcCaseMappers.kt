package de.kuschku.libquassel.util.irc

import java.util.*

object IrcCaseMappers {
  var irc: IrcCaseMapper = ClassicalIrcCaseMapper()
  var unicode: IrcCaseMapper = UnicodeCaseMapper()

  interface IrcCaseMapper {
    fun equalsIgnoreCase(a: String, b: String): Boolean
    fun equalsIgnoreCaseNullable(a: String?, b: String?) = when {
      a === null && b === null -> true
      a === null               -> false
      b === null               -> false
      else                     -> this.equalsIgnoreCase(a, b)
    }

    fun toLowerCase(value: String): String
    fun toLowerCaseNullable(value: String?): String? = value?.let(this@IrcCaseMapper::toLowerCase)

    fun toUpperCase(value: String): String
    fun toUpperCaseNullable(value: String?): String? = value?.let(this@IrcCaseMapper::toUpperCase)
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
      return value.toLowerCase(Locale.US)
        .replace('[', '{')
        .replace(']', '}')
        .replace('^', '~')
    }

    override fun toUpperCase(value: String): String {
      return value.toUpperCase(Locale.US)
        .replace('{', '[')
        .replace('}', ']')
        .replace('~', '^')
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
