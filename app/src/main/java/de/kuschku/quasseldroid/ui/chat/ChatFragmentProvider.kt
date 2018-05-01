/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.chat

import android.support.v4.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.kuschku.quasseldroid.ui.chat.buffers.BufferViewConfigFragment
import de.kuschku.quasseldroid.ui.chat.input.ChatlineFragment
import de.kuschku.quasseldroid.ui.chat.messages.MessageListFragment
import de.kuschku.quasseldroid.ui.chat.nicks.NickListFragment

@Module
abstract class ChatFragmentProvider {
  @Binds
  abstract fun bindFragmentActivity(activity: ChatActivity): FragmentActivity

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
