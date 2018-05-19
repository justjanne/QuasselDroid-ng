package de.kuschku.quasseldroid.util.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class LocaleHelperTest {
  @Test
  fun testParseLanguageCode() {
    LocaleHelper.parseLanguageCode("fr-CA").let {
      assertEquals("CA", it.country)
      assertEquals("", it.variant)
      assertEquals("fr", it.language)
    }
    LocaleHelper.parseLanguageCode("zh-Hant-TW").let {
      assertEquals("TW", it.country)
      assertEquals("Hant", it.variant)
      assertEquals("zh", it.language)
    }
    LocaleHelper.parseLanguageCode("zh").let {
      assertEquals("", it.country)
      assertEquals("", it.variant)
      assertEquals("zh", it.language)
    }
  }
}
