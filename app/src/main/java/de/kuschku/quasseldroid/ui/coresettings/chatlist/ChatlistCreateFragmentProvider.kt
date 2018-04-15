package de.kuschku.quasseldroid.ui.coresettings.chatlist

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatlistCreateFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindChatListCreateFragment(): ChatListCreateFragment
}
