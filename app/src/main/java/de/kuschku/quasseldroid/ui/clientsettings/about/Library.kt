package de.kuschku.quasseldroid.ui.clientsettings.about

data class Library(
  val name: String,
  val version: String,
  val license: License,
  val url: String? = null
)