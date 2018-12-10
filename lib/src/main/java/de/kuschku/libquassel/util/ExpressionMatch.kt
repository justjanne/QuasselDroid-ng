package de.kuschku.libquassel.util

import de.kuschku.libquassel.util.compatibility.LoggingHandler.Companion.log
import de.kuschku.libquassel.util.compatibility.LoggingHandler.LogLevel
import java.io.Serializable

class ExpressionMatch : Serializable {
  enum class MatchMode {
    /** Match phrase as specified, no special handling */
    MatchPhrase,
    /** Match phrase as specified, split on \n only */
    MatchMultiPhrase,
    /** Match wildcards, "!" at start inverts, "\" escapes */
    MatchWildcard,
    /** Match wildcards, split ; or \n, "!" at start inverts, "\" escapes */
    MatchMultiWildcard,
    /** Match as regular expression, "!..." invert regex, "\" escapes */
    MatchRegEx
  }

  /**
   * Construct an Expression match with the given parameters
   *
   * @param expression    A phrase, wildcard expression, or regular expression
   * @param mode          Expression matching mode @see ExpressionMatch.MatchMode
   * @param caseSensitive If true, match case-sensitively, otherwise ignore case when matching
   */
  constructor(expression: String, mode: MatchMode, caseSensitive: Boolean) {
    // Store the original parameters for later reference
    _sourceExpression = expression
    _sourceMode = mode
    _sourceCaseSensitive = caseSensitive

    // Calculate the internal regex
    //
    // Do this now instead of on-demand to provide immediate feedback on errors when editing
    // highlight and ignore rules.
    cacheRegEx()
  }

  /**
   * Check if the given string matches the stored expression
   *
   * @param string      String to check
   * @param matchEmpty  If true, always match when the expression is empty, otherwise never match
   * @return            True if match found, otherwise false
   */
  fun match(string: String, matchEmpty: Boolean = false): Boolean {
    // Handle empty expression strings
    if (_sourceExpressionEmpty) {
      // Match found if matching empty is allowed, otherwise no match found
      return matchEmpty
    }

    if (!isValid()) {
      // Can't match on an invalid rule
      return false
    }

    // We have "_matchRegEx", "_matchInvertRegEx", or both due to isValid() check above

    // If specified, first check inverted rules
    val _matchInvertRegEx = _matchInvertRegEx
    if (_matchInvertRegExActive && _matchInvertRegEx != null) {
      // Check inverted match rule
      if (_matchInvertRegEx.containsMatchIn(string)) {
        // Inverted rule matched, the rest of the rule cannot match
        return false
      }
    }

    val _matchRegEx = _matchRegEx
    if (_matchRegExActive && _matchRegEx != null) {
      // Check regular match rule
      return _matchRegEx.containsMatchIn(string)
    } else {
      // If no valid regular rules exist, due to the isValid() check there must be valid inverted
      // rules that did not match.  Count this as properly matching (implicit wildcard).
      return true
    }
  }

  /**
   * Gets if the source expression is empty
   *
   * @return True if source expression is empty, otherwise false
   */
  fun isEmpty() = _sourceExpressionEmpty

  /**
   * Gets if the source expression and parameters resulted in a valid expression matcher
   *
   * @return True if given expression is valid, otherwise false
   */
  fun isValid(): Boolean {
    return _sourceExpressionEmpty ||
           ((!_matchRegExActive || _matchRegEx != null) &&
            (!_matchInvertRegExActive || _matchInvertRegEx != null))
  }

  var sourceExpression
    /**
     * Gets the original expression match string
     *
     * @return String of the source expression match string
     */
    get() = _sourceExpression
    /**
     * Sets the expression match string
     *
     * @param expression A phrase, wildcard expression, or regular expression
     */
    set(expression) {
      if (_sourceExpression != expression) {
        _sourceExpression = expression
        cacheRegEx()
      }
    }

  var sourceMode
    /**
     * Gets the original expression match mode
     *
     * @return MatchMode of the source expression
     */
    get() = _sourceMode
    /**
     * Sets the expression match mode
     *
     * @param mode Expression matching mode (see ExpressionMatch.MatchMode)
     */
    set(mode) {
      if (_sourceMode != mode) {
        _sourceMode = mode
        cacheRegEx()
      }
    }

  var sourceCaseSensitive
    /**
     * Gets the original expression case-sensitivity
     *
     * @return True if case-sensitive, otherwise false
     */
    get() = _sourceCaseSensitive
    /**
     * Sets the expression match as case sensitive or not
     *
     * @param caseSensitive If true, match case-sensitively, otherwise ignore case when matching
     */
    set(caseSensitive) {
      if (_sourceCaseSensitive != caseSensitive) {
        _sourceCaseSensitive = caseSensitive
        cacheRegEx()
      }
    }

  override fun equals(other: Any?): Boolean {
    return other is ExpressionMatch &&
           _sourceExpression == other._sourceExpression &&
           _sourceMode == other._sourceMode &&
           _sourceCaseSensitive == other._sourceCaseSensitive
  }

  private fun cacheRegEx() {
    _matchRegExActive = false
    _matchInvertRegExActive = false

    _sourceExpressionEmpty = _sourceExpression.isEmpty()
    if (_sourceExpressionEmpty) {
      // No need to calculate anything for empty strings
      return
    }

    // Convert the given expression to a regular expression based on the mode
    when (_sourceMode) {
      MatchMode.MatchPhrase        -> {
        // Match entire phrase, noninverted
        // Don't trim whitespace for phrase matching as someone might want to match on " word ", a
        // more-specific request than "word".
        _matchRegEx = regExFactory("(?:^|\\W)" + regExEscape(_sourceExpression) + "(?:\\W|$)",
                                   _sourceCaseSensitive)
        _matchRegExActive = true
      }
      MatchMode.MatchMultiPhrase   -> {
        // Match multiple entire phrases, noninverted
        // Convert from multiple-phrase rules
        _matchRegEx = regExFactory(convertFromMultiPhrase(_sourceExpression), _sourceCaseSensitive)
        _matchRegExActive = true
      }
      MatchMode.MatchWildcard      -> {
        // Match as wildcard expression
        // Convert from wildcard rules for a single wildcard
        if (_sourceExpression.startsWith("!")) {
          // Inverted rule: take the remainder of the string
          // "^" + invertComponents.at(0) + "$"
          _matchInvertRegEx = regExFactory("^" + wildcardToRegEx(_sourceExpression.substring(1)) + "$",
                                           _sourceCaseSensitive)
          _matchInvertRegExActive = true
        } else {
          // Normal rule: take the whole string
          // Account for any escaped "!" (i.e. "\!") by skipping past the "\", but don't skip past
          // escaped "\" (i.e. "\\!")
          val expression =
            if (_sourceExpression.startsWith("\\!")) _sourceExpression.substring(1)
            else _sourceExpression
          _matchRegEx = regExFactory("^" + wildcardToRegEx(expression) + "$", _sourceCaseSensitive)
          _matchRegExActive = true
        }
      }
      MatchMode.MatchMultiWildcard -> {
        // Match as multiple wildcard expressions
        // Convert from wildcard rules for multiple wildcards
        // (The generator function handles setting matchRegEx/matchInvertRegEx)
        generateFromMultiWildcard(_sourceExpression, _sourceCaseSensitive)
      }
      MatchMode.MatchRegEx         -> {
        // Match as regular expression
        if (_sourceExpression.startsWith("!")) {
          // Inverted rule: take the remainder of the string
          _matchInvertRegEx = regExFactory(_sourceExpression.substring(1), _sourceCaseSensitive)
          _matchInvertRegExActive = true
        } else {
          // Normal rule: take the whole string
          // Account for any escaped "!" (i.e. "\!") by skipping past the "\", but don't skip past
          // escaped "\" (i.e. "\\!")
          val expression =
            if (_sourceExpression.startsWith("\\!")) _sourceExpression.substring(1)
            else _sourceExpression
          _matchRegEx = regExFactory(expression, _sourceCaseSensitive)
          _matchRegExActive = true
        }
      }
    }

    if (!isValid()) {
      // This can happen with invalid regex, so make it a bit more user-friendly.  Set it to Info
      // level as ideally someone's not just going to leave a broken match rule around.  For
      // MatchRegEx, they probably need to fix their regex rule.  For the other modes, there's
      // probably a bug in the parsing routines (which should also be fixed).

      log(LogLevel.INFO,
          "ExpressionMatch",
          "Could not parse expression match rule $_sourceExpression (match mode: $_sourceMode), this rule will be ignored")
    }
  }

  /**
   * Internally converts a wildcard rule into regular expressions
   *
   * Splits wildcards on ";" and "\n", "!..." inverts section, "\" escapes
   *
   * @param originalRule   MultiWildcard rule list, ";"-separated
   * @param caseSensitive  If true, match case-sensitively, otherwise ignore case when matching
   */
  private fun generateFromMultiWildcard(originalRule: String, caseSensitive: Boolean) {
    // Convert the wildcard rule into regular expression format
    // First, reset the existing match expressions
    _matchRegEx = null
    _matchInvertRegEx = null
    _matchRegExActive = false
    _matchInvertRegExActive = false

    // This gets handled in three steps:
    //
    // 1.  Break apart ";"-separated list into components
    // 2.  Convert components from wildcard format into regular expression format
    // 3.  Combine normal/invert components into normal/invert regular expressions
    //
    // Let's start by making the list...

    // Convert a ";"-separated list into an actual list, splitting on newlines and unescaping
    // escaped characters

    // Escaped list rules (where "[\n]" represents newline):
    // ---------------
    // Token  | Outcome
    // -------|--------
    // ;      | Split
    // \;     | Replace with ";"
    // \\;    | Split (keep as "\\")
    // !      | At start: mark as inverted
    // \!     | At start: replace with "!"
    // \\!    | At start: keep as "\\!" (replaced with "\!" in wildcard conversion)
    // !      | Elsewhere: keep as "!"
    // \!     | Elsewhere: keep as "\!"
    // \\!    | Elsewhere: keep as "\\!"
    // \\\    | Keep as "\\" + "\", set consecutive slashes to 1
    // [\n]   | Split
    // \[\n]  | Split (keep as "\")
    // \\[\n] | Split (keep as "\\")
    // ...    | Keep as "..."
    // \...   | Keep as "\..."
    // \\...  | Keep as "\\..."
    //
    // Strings are forced to end with "\n", always applying "\..." and "\\..." rules
    // "..." also includes another "\" character
    //
    // All whitespace is trimmed from each component

    // "\\" and "\" are not downconverted to allow for other escape codes to be detected in
    // ExpressionMatch::wildcardToRegex

    // Example:
    //
    // > Wildcard rule
    // norm;!invert; norm-space ; !invert-space ;;!;\!norm-escaped;\\!slash-invert;\\\\double;
    // escape\;sep;slash-end-split\\;quad\\\\!noninvert;newline-split[\n]newline-split-slash\\[\n]
    // slash-at-end\\               [line does not continue]
    //
    // (Newlines are encoded as "[\n]".  Ignore linebreaks for the sake of comment wrapping.)
    //
    //
    // > Normal components without wildcard conversion
    //   norm
    //   norm-space
    //   !norm-escaped
    //   \\!slash-invert
    //   \\\\double
    //   escape;sep
    //   slash-end-split\\          [line does not continue]
    //   quad\\\\!noninvert
    //   newline-split
    //   newline-split-slash\\      [line does not continue]
    //   slash-at-end\\             [line does not continue]
    //
    // > Inverted components without wildcard conversion
    //   invert
    //   invert-space
    //
    //
    // > Normal components with wildcard conversion
    //   norm
    //   norm\-space
    //   \!norm\-escaped
    //   \\\!slash\-invert
    //   \\\\double
    //   escape\;sep
    //   slash\-end\-split\\        [line does not continue]
    //   quad\\\\\!noninvert
    //   newline\-split
    //   newline\-split\-slash\\    [line does not continue]
    //   slash\-at\-end\\           [line does not continue]
    //
    // > Inverted components with wildcard conversion
    //   invert
    //   invert\-space
    //
    //
    // > Normal wildcard-converted regex
    // ^(?:norm|norm\-space|\!norm\-escaped|\\\!slash\-invert|\\\\double|escape\;sep|
    // slash\-end\-split\\|quad\\\\\!noninvert|newline\-split|newline\-split\-slash\\|
    // slash\-at\-end\\)$
    //
    // > Inverted wildcard-converted regex
    // ^(?:invert|invert\-space)$

    // Prepare to loop!

    var rule = originalRule

    // Force a termination at the end of the string to trigger a split
    // Don't check for ";" splits as they may be escaped
    if (!rule.endsWith("\n")) {
      rule += "\n"
    }

    // Result, sorted into normal and inverted rules
    val normalComponents = mutableSetOf<String>()
    val invertComponents = mutableSetOf<String>()

    // Current string
    var curString = ""
    // Consecutive "\" characters
    var consecutiveSlashes = 0
    // Whether or not this marks an inverted rule
    var isInverted = false
    // Whether or not we're at the beginning of the rule (for detecting "!" and "\!")
    var isRuleStart = true

    for (curChar in rule) {
      // Check if it's on the list of special list characters
      when (curChar) {
        ';'  -> {
          // Separator found
          when (consecutiveSlashes) {
            0, 2 -> {
              // ";"   -> Split
              // ...or...
              // "\\;" -> Split (keep as "\\")
              // Not escaped separator, split into a new item

              // Apply the additional "\\" if needed
              if (consecutiveSlashes == 2) {
                // "\\;" -> Split (keep as "\\")
                curString += """\\"""
              }

              // Remove any whitespace, e.g. "item1; item2" -> " item2" -> "item2"
              curString = curString.trim()

              // Skip empty items
              if (curString.isNotEmpty()) {
                // Add to inverted/normal list
                if (isInverted) {
                  invertComponents.add(wildcardToRegEx(curString))
                } else {
                  normalComponents.add(wildcardToRegEx(curString))
                }
              }
              // Reset the current list item
              curString = ""
              isInverted = false
              isRuleStart = true
            }
            1    -> {
              // "\;" -> Replace with ";"
              curString += ";"
              isRuleStart = false
            }
            else -> {
              // This shouldn't ever happen (even with invalid wildcard rules), log a warning
              log(LogLevel.WARN,
                  "ExpressionMatch",
                  "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              isRuleStart = false
            }
          }
          consecutiveSlashes = 0
        }
        '!'  -> {
          // Rule inverter found
          if (isRuleStart) {
            // Apply the inverting logic
            when (consecutiveSlashes) {
              0    -> {
                // "!"   -> At start: mark as inverted
                isInverted = true
                // Don't include the "!" character
              }
              1    -> {
                // "\!"  -> At start: replace with "!"
                curString += "!"
              }
              2    -> {
                // "\\!" -> At start: keep as "\\!" (replaced with "\!" in wildcard conversion)
                curString += """\\!"""
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              }
            }
          } else {
            // Preserve the characters as they are now
            when (consecutiveSlashes) {
              0    -> {
                // "!"    -> Elsewhere: keep as "!"
                curString += "!"
              }
              1, 2 -> {
                // "\!"  -> Elsewhere: keep as "\!"
                // "\\!" -> Elsewhere: keep as "\\!"
                curString += """\""".repeat(consecutiveSlashes) + "!"
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              }
            }
          }
          isRuleStart = false
          consecutiveSlashes = 0
        }
        '\\' -> {
          // Split escape
          // Increase consecutive slash count
          consecutiveSlashes++
          // Check if we've reached "\\\"...
          if (consecutiveSlashes == 3) {
            // "\\\" -> Keep as "\\" + "\"
            curString += """\\"""
            // No longer at the rule start
            isRuleStart = false
            // Set consecutive slashes to 1, recognizing the trailing "\"
            consecutiveSlashes = 1
          } else if (consecutiveSlashes > 3) {
            // This shouldn't ever happen (even with invalid wildcard rules), log a warning
            log(LogLevel.WARN,
                "ExpressionMatch",
                "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
          }
          // Don't set "isRuleStart" here as "\" is used in escape sequences
        }
        '\n' -> {
          // Newline found
          // Preserve the characters as they are now

          // "[\n]"   -> Split
          // "\[\n]"  -> Split (keep as "\")
          // "\\[\n]" -> Split (keep as "\\")

          when (consecutiveSlashes) {
            0    -> {
              // Keep string as is
            }
            1, 2 -> {
              // Apply the additional "\" or "\\"
              curString += """\""".repeat(consecutiveSlashes)
            }
            else -> {
              // This shouldn't ever happen (even with invalid wildcard rules), log a warning
              log(LogLevel.WARN,
                  "ExpressionMatch",
                  "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), applying newline split anyways!")
            }
          }

          // Remove any whitespace, e.g. "item1; item2" -> " item2" -> "item2"
          curString = curString.trim()

          // Skip empty items
          if (curString.isNotEmpty()) {
            // Add to inverted/normal list
            if (isInverted) {
              invertComponents.add(wildcardToRegEx(curString))
            } else {
              normalComponents.add(wildcardToRegEx(curString))
            }
          }
          // Reset the current list item
          curString = ""
          isInverted = false
          isRuleStart = true
          consecutiveSlashes = 0
        }
        else -> {
          // Preserve the characters as they are now
          when (consecutiveSlashes) {
            0    -> {
              // "..."   -> Keep as "..."
              curString += curChar
            }
            1, 2 -> {
              // "\..."  -> Keep as "\..."
              // "\\..." -> Keep as "\\..."
              curString += """\""".repeat(consecutiveSlashes) + curChar
            }
            else -> {
              log(LogLevel.WARN,
                  "ExpressionMatch",
                  "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar char escape!")
            }
          }
          // Don't mark as past rule start for whitespace (whitespace gets trimmed)
          if (!curChar.isWhitespace()) {
            isRuleStart = false
          }
          consecutiveSlashes = 0
        }
      }
    }

    // Create full regular expressions by...
    // > Anchoring to start and end of string to mimic QRegExp's .exactMatch() handling, "^...$"
    // > Enclosing within a non-capturing group to avoid overhead of text extraction, "(?:...)"
    // > Flattening normal and inverted rules using the regex OR character "...|..."
    //
    // Before: [foo, bar, baz]
    // After:  ^(?:foo|bar|baz)$
    //
    // See https://doc.qt.io/qt-5/qregularexpression.html#porting-from-qregexp-exactmatch
    // And https://regex101.com/

    // Any empty/invalid regex are handled within ExpressionMatch::match()
    if (!normalComponents.isEmpty()) {
      // Create normal match regex
      if (normalComponents.count() == 1) {
        // Single item, skip the noncapturing group
        _matchRegEx = regExFactory("^${normalComponents.toList().first()}$", caseSensitive)
      } else {
        val buffer = StringBuilder()
        buffer.append("^(?:")
        normalComponents.joinTo(buffer, "|")
        buffer.append(")$")
        _matchRegEx = regExFactory(buffer.toString(), caseSensitive)
      }
      _matchRegExActive = true
    }
    if (!invertComponents.isEmpty()) {
      // Create invert match regex
      if (invertComponents.count() == 1) {
        // Single item, skip the noncapturing group
        _matchInvertRegEx = regExFactory("^${invertComponents.toList().first()}$", caseSensitive)
      } else {
        val buffer = StringBuilder()
        buffer.append("^(?:")
        invertComponents.joinTo(buffer, "|")
        buffer.append(")$")
        _matchInvertRegEx = regExFactory(buffer.toString(), caseSensitive)
      }
      _matchInvertRegExActive = true
    }
  }

  // Original/source components
  /** Expression match string given on creation */
  private var _sourceExpression: String = ""
  /** Expression match mode given on creation */
  private var _sourceMode: MatchMode = MatchMode.MatchPhrase
  /** Expression case sensitive on creation */
  private var _sourceCaseSensitive: Boolean = false

  // Derived components
  /** Cached expression match string is empty */
  private var _sourceExpressionEmpty: Boolean = false

  /** Underlying regular expression matching instance for normal (noninverted) rules */
  private var _matchRegEx: Regex? = null
  /** If true, use normal expression in matching */
  private var _matchRegExActive: Boolean = false

  /** Underlying regular expression matching instance for inverted rules */
  private var _matchInvertRegEx: Regex? = null
  /** If true, use invert expression in matching */
  private var _matchInvertRegExActive: Boolean = false

  companion object {

    /**
     * Creates a regular expression object of appropriate type and case-sensitivity
     *
     * @param regExString    Regular expression string
     * @param caseSensitive  If true, match case-sensitively, otherwise ignore case when matching
     * @return Configured QRegularExpression
     */
    private fun regExFactory(regExString: String, caseSensitive: Boolean) =
      if (caseSensitive) Regex(regExString)
      else Regex(regExString, RegexOption.IGNORE_CASE)

    /**
     * Escapes any regular expression characters in a string so they have no special meaning
     *
     * @param phrase String containing potential regular expression special characters
     * @return QString with all regular expression characters escaped
     */
    private fun regExEscape(phrase: String): String {
      val size = phrase.length
      val result = StringBuilder(size)
      var i = 0
      while (i < size) {
        val current = phrase[i]
        when (current) {
          0.toChar()                                                                                -> {
            result.append('\\')
            result.append('0')
          }
          '\\', '.', '[', ']', '{', '}', '(', ')', '<', '>', '*', '+', '-', '=', '?', '^', '$', '|' -> {
            result.append('\\')
            result.append(current)
          }
          else                                                                                      -> {
            result.append(current)
          }
        }
        i++
      }
      return result.toString()
    }

    /**
     * Converts a multiple-phrase rule into a regular expression
     *
     * Unconditionally splits phrases on "\n", whitespace is preserved
     *
     * @param originalRule MultiPhrase rule list, "\n"-separated
     * @return A regular expression matching the given phrases
     */
    private fun convertFromMultiPhrase(originalRule: String): String {
      // Convert the multi-phrase rule into regular expression format
      // Split apart the original rule into components
      val components = mutableListOf<String>()
      // Split on "\n"
      for (component in originalRule.splitToSequence('\n')) {
        if (component.isNotEmpty()) {
          components.add(regExEscape(component))
        }
      }

      // Create full regular expression by...
      // > Enclosing within a non-capturing group to avoid overhead of text extraction, "(?:...)"
      // > Flattening normal and inverted rules using the regex OR character "...|..."
      //
      // Before: [foo, bar, baz]
      // After:  (?:^|\W)(?:foo|bar|baz)(?:\W|$)

      if (components.count() == 1) {
        // Single item, skip the noncapturing group
        return "(?:^|\\W)${components[0]}(?:\\W|$)"
      } else {
        val buffer = java.lang.StringBuilder()
        buffer.append("(?:^|\\W)(?:")
        components.joinTo(buffer, "|")
        buffer.append(")(?:\\W|$)")
        return buffer.toString()
      }
    }

    /**
     * Converts a wildcard expression into a regular expression
     *
     * NOTE:  Does not handle Quassel's extended scope matching and splitting.
     *
     * @see ExpressionMatch::convertFromWildcard()
     * @return QString with all regular expression characters escaped
     */
    private fun wildcardToRegEx(expression: String): String {
      // Convert the wildcard expression into regular expression format

      // We're taking a little bit different of a route...
      //
      // Original QRegExp::Wildcard rules:
      // --------------------------
      // Wildcard | Regex | Outcome
      // ---------|-------|--------
      // *        | .*    | zero or more of any character
      // ?        | .     | any single character
      //
      // NOTE 1: This is QRegExp::Wildcard, not QRegExp::WildcardUnix
      //
      // NOTE 2: We are ignoring the "[...]" character-class matching functionality of
      // QRegExp::Wildcard as that feature's a bit more complex and can be handled with full-featured
      // regexes.
      //
      // See https://doc.qt.io/qt-5/qregexp.html#wildcard-matching
      //
      // Quassel originally did not use QRegExp::WildcardUnix, which prevented escaping "*" and "?" in
      // messages.  Unfortunately, spam messages might decide to use both, so offering a way to escape
      // makes sense.
      //
      // On the flip-side, that means to match "\" requires escaping as "\\", breaking backwards
      // compatibility.
      //
      // Quassel's Wildcard rules
      // ------------------------------------------
      // Wildcard | Regex escaped | Regex | Outcome
      // ---------|---------------|-------|--------
      // *        | \*            | .*    | zero or more of any character
      // ?        | \?            | .     | any single character
      // \*       | \\\*          | \*    | literal "*"
      // \?       | \\\?          | \?    | literal "?"
      // \[...]   | \\[...]       | [...] | invalid escape, ignore it
      // \\       | \\\\          | \\    | literal "\"
      //
      // In essence, "*" and "?" need changed only if not escaped, "\\" collapses into "\", "\" gets
      // ignored; other characters escape normally.
      //
      // Example:
      //
      // > Wildcard rule
      // never?gonna*give\*you\?up\\test|y\yeah\\1\\\\2\\\1inval
      //
      // ("\\\\" represents "\\", "\\" represents "\", and "\\\" is valid+invalid, "\")
      //
      // > Regex escaped wildcard rule
      // never\?gonna\*give\\\*you\\\?up\\\\test\|y\\yeah\\\\1\\\\\\\\2\\\\\\1inval
      //
      // > Expected correct regex
      // never.gonna.*give\*you\?up\\test\|yyeah\\1\\\\2\\1inval
      //
      // > Undoing regex escaping of "\" as "\\" (i.e. simple replace, with special escapes intact)
      // never.gonna.*give\*you\?up\test\|yyeah\1\\2\1inval

      // Escape string according to regex
      val regExEscaped = regExEscape(expression)

      // Fix up the result
      //
      // NOTE: In theory, regular expression lookbehind could solve this.  Unfortunately, QRegExp does
      // not support lookbehind, and it's theoretically inefficient, anyways.  Just use an approach
      // similar to that taken by QRegExp's official wildcard mode.
      //
      // Lookbehind example (that we can't use):
      // (?<!abc)test    Negative lookbehind - don't match if "test" is proceeded by "abc"    //
      // See https://code.qt.io/cgit/qt/qtbase.git/tree/src/corelib/tools/qregexp.cpp
      //
      // NOTE: We don't copy QRegExp's mode as QRegularExpression has more special characters.  We
      // can't use the same escaping code, hence calling the appropriate QReg[...]::escape() above.

      // Prepare to loop!

      // Result
      val result = StringBuilder()
      // Consecutive "\" characters
      var consecutiveSlashes = 0

      for (curChar in regExEscaped) {
        // Check if it's on the list of special wildcard characters
        when (curChar) {
          '?'  -> {
            // Wildcard "?"
            when (consecutiveSlashes) {
              1    -> {
                // "?" -> "\?" -> "."
                // Convert from regex escaped "?" to regular expression
                result.append(".")
              }
              3    -> {
                // "\?" -> "\\\?" -> "\?"
                // Convert from regex escaped "\?" to literal string
                result.append("""\?""")
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $expression resulted in escaped regular expression string $regExEscaped  with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              }
            }
            consecutiveSlashes = 0
          }
          '*'  -> {
            // Wildcard "*"
            when (consecutiveSlashes) {
              1    -> {
                // "*" -> "\*" -> ".*"
                // Convert from regex escaped "*" to regular expression
                result.append(".*")
              }
              3    -> {
                // "\*" -> "\\\*" -> "\*"
                // Convert from regex escaped "\*" to literal string
                result.append("""\*""")
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $expression resulted in escaped regular expression string $regExEscaped with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              }
            }
            consecutiveSlashes = 0
          }
          '\\' -> {
            // Wildcard escape
            // Increase consecutive slash count
            consecutiveSlashes++
            // Check if we've hit an escape sequence
            if (consecutiveSlashes == 4) {
              // "\\" -> "\\\\" -> "\\"
              // Convert from regex escaped "\\" to literal string
              result.append("""\\""")
              // Reset slash count
              consecutiveSlashes = 0
            }
          }
          else -> {
            // Any other character
            when (consecutiveSlashes) {
              0, 2 -> {
                // "[...]"  -> "[...]"   -> "[...]"
                // ...or...
                // "\[...]" -> "\\[...]" -> "[...]"
                // Either just print the character itself, or convert from regex-escaped invalid
                // wildcard escape sequence to the character itself
                //
                // Both mean doing nothing, the actual character [...] gets appended below
              }
              1    -> {
                // "[...]" -> "\[...]" -> "\"
                // Keep regex-escaped special character "[...]" as literal string
                // (Where "[...]" represents any non-wildcard regex special character)
                result.append("""\""")
                // The actual character [...] gets appended below
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $expression resulted in escaped regular expression string $regExEscaped with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar char escape!")
              }
            }
            consecutiveSlashes = 0
            // Add the character itself
            result.append(curChar)
          }
        }
      }

      // Anchoring to simulate QRegExp::exactMatch() is handled in
      // ExpressionMatch::convertFromWildcard()
      return result.toString()
    }

    /**
     * Trim extraneous whitespace from individual rules within a given MultiWildcard expression
     *
     * This respects the ";" escaping rules with "\".  It is safe to call this multiple times; a
     * trimmed string should remain unchanged.
     *
     * @see ExpressionMatch.MatchMode.MatchMultiWildcard
     *
     * @param originalRule MultiWildcard rule list, ";"-separated
     * @return Trimmed MultiWildcard rule list
     */
    fun trimMultiWildcardWhitespace(originalRule: String): String {
      // This gets handled in two steps:
      //
      // 1.  Break apart ";"-separated list into components
      // 2.  Combine whitespace-trimmed components into wildcard expression
      //
      // Let's start by making the list...

      // Convert a ";"-separated list into an actual list, splitting on newlines and unescaping
      // escaped characters

      // Escaped list rules (where "[\n]" represents newline):
      // ---------------
      // Token  | Outcome
      // -------|--------
      // ;      | Split
      // \;     | Keep as "\;"
      // \\;    | Split (keep as "\\")
      // \\\    | Keep as "\\" + "\", set consecutive slashes to 1
      // [\n]   | Split
      // \[\n]  | Split (keep as "\")
      // \\[\n] | Split (keep as "\\")
      // ...    | Keep as "..."
      // \...   | Keep as "\..."
      // \\...  | Keep as "\\..."
      //
      // Strings are forced to end with "\n", always applying "\..." and "\\..." rules
      // "..." also includes another "\" character
      //
      // All whitespace is trimmed from each component

      // "\\" and "\" are not downconverted to allow for other escape codes to be detected in
      // ExpressionMatch::wildcardToRegex

      // Example:
      //
      // > Wildcard rule
      // norm; norm-space ; newline-space [\n] ;escape \; sep ; slash-end-split\\; quad\\\\norm;
      // newline-split-slash\\[\n] slash-at-end\\                       [line does not continue]
      //
      // > Components
      //   norm
      //   norm-space
      //   newline-space
      //   escape \; sep
      //   slash-end-split\\          [line does not continue]
      //   quad\\\\norm
      //   newline-split-slash\\      [line does not continue]
      //   slash-at-end\\             [line does not continue]
      //
      // > Trimmed wildcard rule
      // norm; norm-space; newline-space[\n]escape \; sep; slash-end-split\\; quad\\\\norm;
      // newline-split-slash\\[\n]slash-at-end\\                        [line does not continue]
      //
      // (Newlines are encoded as "[\n]".  Ignore linebreaks for the sake of comment wrapping.)

      // Prepare to loop!

      var rule: String = originalRule

      // Force a termination at the end of the string to trigger a split
      // Don't check for ";" splits as they may be escaped
      if (!rule.endsWith("\n")) {
        rule += "\n"
      }

      // Result
      val result = StringBuilder()
      // Current string
      var curString = ""
      // Max length
      // Consecutive "\" characters
      var consecutiveSlashes = 0

      for (curChar in rule) {
        // Check if it's on the list of special list characters
        when (curChar) {
          ';'  -> {
            // Separator found
            when (consecutiveSlashes) {
              0, 2 -> {
                // ";"   -> Split
                // ...or...
                // "\\;" -> Split (keep as "\\")
                // Not escaped separator, split into a new item

                // Apply the additional "\\" if needed
                if (consecutiveSlashes == 2) {
                  // "\\;" -> Split (keep as "\\")
                  curString += """\\"""
                }

                curString = curString.trim()

                // Skip empty items
                if (curString.isNotEmpty()) {
                  // Add to list with the same separator used
                  result.append(curString)
                  result.append("; ")
                }

                // Reset the current list item
                curString = ""
              }
              1    -> {
                // "\;" -> Keep as "\;"
                curString += """\;"""
              }
              else -> {
                // This shouldn't ever happen (even with invalid wildcard rules), log a warning
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
              }
            }
            consecutiveSlashes = 0
          }
          '\\' -> {
            // Split escape
            // Increase consecutive slash count
            consecutiveSlashes++
            // Check if we’ve reached "\\\"...
            if (consecutiveSlashes == 3) {
              // "\\\" -> Keep as "\\" + "\"
              curString += """\\"""
              // Set consecutive slashes to 1, recognizing the trailing "\"
              consecutiveSlashes = 1
            } else if (consecutiveSlashes > 3) {
              // This shouldn't ever happen (even with invalid wildcard rules), log a warning
              log(LogLevel.WARN,
                  "ExpressionMatch",
                  "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar character!")
            }
          }
          '\n' -> {
            // Newline found
            // Preserve the characters as they are now

            // "[\n]"   -> Split
            // "\[\n]"  -> Split (keep as "\")
            // "\\[\n]" -> Split (keep as "\\")

            when (consecutiveSlashes) {
              0    -> {
                // Keep string as is
              }
              1, 2 -> {
                // Apply the additional "\" or "\\"
                curString += """\""".repeat(consecutiveSlashes)
              }
              else -> {
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), applying newline split anyways!")
              }
            }

            // Remove any whitespace, e.g. "item1; item2" -> " item2" -> "item2"
            curString = curString.trim()

            // Skip empty items
            if (curString.isNotEmpty()) {
              // Add to list with the same separator used
              result.append(curString + "\n")
            }

            // Reset the current list item
            curString = ""
            consecutiveSlashes = 0
          }
          else -> {
            when (consecutiveSlashes) {
              0    -> {
                // "..."   -> Keep as "..."
                curString += curChar
              }
              1, 2 -> {
                // "\..."  -> Keep as "\..."
                // "\\..." -> Keep as "\\..."
                curString += """\""".repeat(consecutiveSlashes)
                curString += curChar
              }
              else -> {
                log(LogLevel.WARN,
                    "ExpressionMatch",
                    "Wildcard rule $rule resulted in rule component $curString with unexpected count of consecutive '\\' ($consecutiveSlashes), ignoring $curChar char escape!")
              }
            }
            consecutiveSlashes = 0
          }
        }
      }

      // Remove any trailing separators
      if (result.endsWith("; ")) {
        result.setLength(maxOf(result.length - 2, 0))
      }

      // Remove any trailing whitespace
      return result.trim().toString()
    }
  }
}
