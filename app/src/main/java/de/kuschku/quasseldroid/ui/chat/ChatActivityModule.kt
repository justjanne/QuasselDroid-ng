package de.kuschku.quasseldroid.ui.chat

import dagger.Binds
import dagger.Module
import de.kuschku.quasseldroid.ui.chat.messages.MessageRenderer
import de.kuschku.quasseldroid.ui.chat.messages.QuasselMessageRenderer

@Module
abstract class ChatActivityModule {
  @Binds
  abstract fun bindMessageRenderer(messageRenderer: QuasselMessageRenderer): MessageRenderer
}
