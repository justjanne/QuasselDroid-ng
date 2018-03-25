package de.kuschku.quasseldroid.ui.settings.about

data class Library(
  val name: String,
  val version: String,
  val license: License,
  val url: String? = null
)