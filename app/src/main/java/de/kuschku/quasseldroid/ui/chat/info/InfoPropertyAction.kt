package de.kuschku.quasseldroid.ui.chat.info

data class InfoPropertyAction(
  val name: CharSequence,
  val featured: Boolean = false,
  val onClick: () -> Unit
)