package de.kuschku.quasseldroid.ui.clientsettings.about

data class Library(
  val name: String,
  val version: String? = null,
  val license: License,
  val url: String? = null
)
