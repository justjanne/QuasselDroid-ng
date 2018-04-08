package de.kuschku.quasseldroid.ui.chat.input

import de.kuschku.quasseldroid.viewmodel.data.AutoCompleteItem

data class AutoCompletionState(
  val originalWord: String,
  val range: IntRange,
  val lastCompletion: AutoCompleteItem? = null,
  val completion: AutoCompleteItem
)
