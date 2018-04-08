package de.kuschku.quasseldroid.ui.chat

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.kuschku.quasseldroid.ui.chat.buffers.BufferViewConfigFragment
import de.kuschku.quasseldroid.ui.chat.input.ChatlineFragment
import de.kuschku.quasseldroid.ui.chat.messages.MessageListFragment
import de.kuschku.quasseldroid.ui.chat.nicks.NickListFragment

@Module
abstract class ChatFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindBufferViewConfigFragment(): BufferViewConfigFragment

  @ContributesAndroidInjector
  abstract fun bindMessageListFragment(): MessageListFragment

  @ContributesAndroidInjector
  abstract fun bindNickListFragment(): NickListFragment

  @ContributesAndroidInjector
  abstract fun bindToolbarFragment(): ToolbarFragment

  @ContributesAndroidInjector
  abstract fun bindChatlineFragment(): ChatlineFragment
}
