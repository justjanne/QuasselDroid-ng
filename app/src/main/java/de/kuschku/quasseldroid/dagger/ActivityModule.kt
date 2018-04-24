/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.kuschku.quasseldroid.service.QuasselService
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.ChatActivityModule
import de.kuschku.quasseldroid.ui.chat.ChatFragmentProvider
import de.kuschku.quasseldroid.ui.chat.info.channel.ChannelInfoActivity
import de.kuschku.quasseldroid.ui.chat.info.channel.ChannelInfoFragmentProvider
import de.kuschku.quasseldroid.ui.chat.info.user.UserInfoActivity
import de.kuschku.quasseldroid.ui.chat.info.user.UserInfoFragmentProvider
import de.kuschku.quasseldroid.ui.chat.topic.TopicActivity
import de.kuschku.quasseldroid.ui.chat.topic.TopicFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutActivity
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.client.ClientSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseActivity
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.whitelist.WhitelistActivity
import de.kuschku.quasseldroid.ui.clientsettings.whitelist.WhitelistFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsActivity
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.aliasitem.AliasItemActivity
import de.kuschku.quasseldroid.ui.coresettings.aliasitem.AliasItemFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.aliaslist.AliasListActivity
import de.kuschku.quasseldroid.ui.coresettings.aliaslist.AliasListFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.highlightlist.HighlightListActivity
import de.kuschku.quasseldroid.ui.coresettings.highlightlist.HighlightListFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.highlightrule.HighlightRuleActivity
import de.kuschku.quasseldroid.ui.coresettings.highlightrule.HighlightRuleFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityCreateFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityEditActivity
import de.kuschku.quasseldroid.ui.coresettings.identity.IdentityEditFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.ignoreitem.IgnoreItemActivity
import de.kuschku.quasseldroid.ui.coresettings.ignoreitem.IgnoreItemFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.ignorelist.IgnoreListActivity
import de.kuschku.quasseldroid.ui.coresettings.ignorelist.IgnoreListFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkCreateFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigActivity
import de.kuschku.quasseldroid.ui.coresettings.networkconfig.NetworkConfigFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.networkserver.NetworkServerActivity
import de.kuschku.quasseldroid.ui.coresettings.networkserver.NetworkServerFragmentProvider
import de.kuschku.quasseldroid.ui.setup.accounts.edit.AccountEditActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionFragmentProvider
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupActivity
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupFragmentProvider

@Module
abstract class ActivityModule {
  @ContributesAndroidInjector(modules = [ChatActivityModule::class, ChatFragmentProvider::class])
  abstract fun bindChatActivity(): ChatActivity

  @ContributesAndroidInjector(modules = [UserInfoFragmentProvider::class])
  abstract fun bindUserInfoActivity(): UserInfoActivity

  @ContributesAndroidInjector(modules = [ChannelInfoFragmentProvider::class])
  abstract fun bindChannelInfoActivity(): ChannelInfoActivity

  @ContributesAndroidInjector(modules = [TopicFragmentProvider::class])
  abstract fun bindTopicActivity(): TopicActivity

  @ContributesAndroidInjector(modules = [ClientSettingsFragmentProvider::class])
  abstract fun bindClientSettingsActivity(): ClientSettingsActivity

  @ContributesAndroidInjector(modules = [WhitelistFragmentProvider::class])
  abstract fun bindWhitelistActivity(): WhitelistActivity

  @ContributesAndroidInjector(modules = [CrashFragmentProvider::class])
  abstract fun bindCrashActivity(): CrashActivity

  @ContributesAndroidInjector(modules = [AboutFragmentProvider::class])
  abstract fun bindAboutActivity(): AboutActivity

  @ContributesAndroidInjector(modules = [LicenseFragmentProvider::class])
  abstract fun bindLicenseActivity(): LicenseActivity

  @ContributesAndroidInjector(modules = [CoreSettingsFragmentProvider::class])
  abstract fun bindCoreSettingsActivity(): CoreSettingsActivity

  @ContributesAndroidInjector(modules = [NetworkCreateFragmentProvider::class])
  abstract fun bindNetworkCreateActivity(): NetworkCreateActivity

  @ContributesAndroidInjector(modules = [NetworkEditFragmentProvider::class])
  abstract fun bindNetworkEditActivity(): NetworkEditActivity

  @ContributesAndroidInjector(modules = [NetworkServerFragmentProvider::class])
  abstract fun bindNetworkServerActivity(): NetworkServerActivity

  @ContributesAndroidInjector(modules = [IdentityCreateFragmentProvider::class])
  abstract fun bindIdentityCreateActivity(): IdentityCreateActivity

  @ContributesAndroidInjector(modules = [IdentityEditFragmentProvider::class])
  abstract fun bindIdentityEditActivity(): IdentityEditActivity

  @ContributesAndroidInjector(modules = [ChatlistCreateFragmentProvider::class])
  abstract fun bindChatListCreateActivity(): ChatlistCreateActivity

  @ContributesAndroidInjector(modules = [ChatlistEditFragmentProvider::class])
  abstract fun bindChatListEditActivity(): ChatlistEditActivity

  @ContributesAndroidInjector(modules = [IgnoreListFragmentProvider::class])
  abstract fun bindIgnoreListActivity(): IgnoreListActivity

  @ContributesAndroidInjector(modules = [IgnoreItemFragmentProvider::class])
  abstract fun bindIgnoreItemActivity(): IgnoreItemActivity

  @ContributesAndroidInjector(modules = [HighlightListFragmentProvider::class])
  abstract fun bindHighlightListActivity(): HighlightListActivity

  @ContributesAndroidInjector(modules = [HighlightRuleFragmentProvider::class])
  abstract fun bindHighlightRuleActivity(): HighlightRuleActivity

  @ContributesAndroidInjector(modules = [AliasListFragmentProvider::class])
  abstract fun bindAliasListActivity(): AliasListActivity

  @ContributesAndroidInjector(modules = [AliasItemFragmentProvider::class])
  abstract fun bindAliasItemActivity(): AliasItemActivity

  @ContributesAndroidInjector(modules = [NetworkConfigFragmentProvider::class])
  abstract fun bindNetworkConfigActivity(): NetworkConfigActivity

  @ContributesAndroidInjector(modules = [AccountSetupFragmentProvider::class])
  abstract fun bindAccountSetupActivity(): AccountSetupActivity

  @ContributesAndroidInjector(modules = [AccountSelectionFragmentProvider::class])
  abstract fun bindAccountSelectionActivity(): AccountSelectionActivity

  @ContributesAndroidInjector
  abstract fun bindAccountEditActivity(): AccountEditActivity

  @ContributesAndroidInjector
  abstract fun bindQuasselService(): QuasselService
}
