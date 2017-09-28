package de.kuschku.quasseldroid_ng.util

import java.util.regex.Pattern

@SuppressWarnings("Access")
object Patterns {
  const val IPv4
    = "(?:(?:[0-1]?[0-9]?[0-9]|2[0-5][0-5])\\.){3}(?:[0-1]?[0-9]?[0-9]|2[0-5][0-5])"

  const val IPv6
    = "(?:(?:(?:[0-9a-fA-F]{1,4}:){7}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,7}|:):(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,6}|:):(?:[0-9a-fA-F]{1,4}:){0,1}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,5}|:):(?:[0-9a-fA-F]{1,4}:){0,2}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,4}|:):(?:[0-9a-fA-F]{1,4}:){0,3}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,3}|:):(?:[0-9a-fA-F]{1,4}:){1,4}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:){1,2}|:):(?:[0-9a-fA-F]{1,4}:){0,5}(?:[0-9a-fA-F]{1,4}))|(?:(?:(?:[0-9a-fA-F]{1,4}:)|:):(?:[0-9a-fA-F]{1,4}:){0,6}(?:[0-9a-fA-F]{1,4})))"

  const val IP_ADDRESS_STRING = "(?:" + IPv4 + "|" + IPv6 + ")"

  const val UCS_CHAR = "[" +
    "\u00A0-\uD7FF" +
    "\uF900-\uFDCF" +
    "\uFDF0-\uFFEF" +
    "\uD800\uDC00-\uD83F\uDFFD" +
    "\uD840\uDC00-\uD87F\uDFFD" +
    "\uD880\uDC00-\uD8BF\uDFFD" +
    "\uD8C0\uDC00-\uD8FF\uDFFD" +
    "\uD900\uDC00-\uD93F\uDFFD" +
    "\uD940\uDC00-\uD97F\uDFFD" +
    "\uD980\uDC00-\uD9BF\uDFFD" +
    "\uD9C0\uDC00-\uD9FF\uDFFD" +
    "\uDA00\uDC00-\uDA3F\uDFFD" +
    "\uDA40\uDC00-\uDA7F\uDFFD" +
    "\uDA80\uDC00-\uDABF\uDFFD" +
    "\uDAC0\uDC00-\uDAFF\uDFFD" +
    "\uDB00\uDC00-\uDB3F\uDFFD" +
    "\uDB44\uDC00-\uDB7F\uDFFD" +
    "&&[^\u00A0[\u2000-\u200A]\u2028\u2029\u202F\u3000]" +
    "]"
  /**
   * Valid characters for IRI label defined in RFC 3987.
   */
  const val LABEL_CHAR
    = "a-zA-Z0-9" + UCS_CHAR
  /**
   * Valid characters for IRI TLD defined in RFC 3987.
   */
  const val TLD_CHAR
    = "a-zA-Z" + UCS_CHAR
  /**
   * RFC 1035 Section 2.3.4 limits the labels to a maximum 63 octets.
   */
  const val IRI_LABEL
    = "[" + LABEL_CHAR + "](?:[" + LABEL_CHAR + "_\\-]{0,61}[" + LABEL_CHAR + "]){0,1}"
  /**
   * RFC 3492 references RFC 1034 and limits Punycode algorithm output to 63 characters.
   */
  const val PUNYCODE_TLD
    = "xn\\-\\-[\\w\\-]{0,58}\\w"
  const val TLD
    = "(?:" + PUNYCODE_TLD + "|" + "[" + TLD_CHAR + "]{2,63}" + ")"
  const val HOST_NAME
    = "(?:" + IRI_LABEL + "\\.)+" + TLD + ".?"
  const val LOCAL_HOST_NAME
    = "(?:" + IRI_LABEL + "\\.)*" + IRI_LABEL
  const val DOMAIN_NAME_STR
    = "(?:" + LOCAL_HOST_NAME + "|" + HOST_NAME + "|" + IP_ADDRESS_STRING + ")"
  val DOMAIN_NAME = Pattern.compile(DOMAIN_NAME_STR)
}
