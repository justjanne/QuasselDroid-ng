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
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.about.AboutSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.app.AppSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.app.AppSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.crash.CrashSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseSettingsActivity
import de.kuschku.quasseldroid.ui.clientsettings.license.LicenseSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsActivity
import de.kuschku.quasseldroid.ui.coresettings.CoreSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistCreateFragmentProvider
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditActivity
import de.kuschku.quasseldroid.ui.coresettings.chatlist.ChatlistEditFragmentProvider
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

  @ContributesAndroidInjector(modules = [AppSettingsFragmentProvider::class])
  abstract fun bindAppSettingsActivity(): AppSettingsActivity

  @ContributesAndroidInjector(modules = [CrashSettingsFragmentProvider::class])
  abstract fun bindCrashSettingsActivity(): CrashSettingsActivity

  @ContributesAndroidInjector(modules = [AboutSettingsFragmentProvider::class])
  abstract fun bindAboutSettingsActivity(): AboutSettingsActivity

  @ContributesAndroidInjector(modules = [LicenseSettingsFragmentProvider::class])
  abstract fun bindLicenseSettingsActivity(): LicenseSettingsActivity

  @ContributesAndroidInjector(modules = [CoreSettingsFragmentProvider::class])
  abstract fun bindCoreSettingsActivity(): CoreSettingsActivity

  @ContributesAndroidInjector(modules = [NetworkCreateFragmentProvider::class])
  abstract fun bindNetworkCreateActivity(): NetworkCreateActivity

  @ContributesAndroidInjector(modules = [NetworkEditFragmentProvider::class])
  abstract fun bindNetworkEditActivity(): NetworkEditActivity

  @ContributesAndroidInjector(modules = [IdentityCreateFragmentProvider::class])
  abstract fun bindIdentityCreateActivity(): IdentityCreateActivity

  @ContributesAndroidInjector(modules = [IdentityEditFragmentProvider::class])
  abstract fun bindIdentityEditActivity(): IdentityEditActivity

  @ContributesAndroidInjector(modules = [ChatlistCreateFragmentProvider::class])
  abstract fun bindChatListCreateActivity(): ChatlistCreateActivity

  @ContributesAndroidInjector(modules = [ChatlistEditFragmentProvider::class])
  abstract fun bindChatListEditActivity(): ChatlistEditActivity

  @ContributesAndroidInjector(modules = [IgnoreListFragmentProvider::class])
  abstract fun bindIgnoreActivity(): IgnoreListActivity

  @ContributesAndroidInjector(modules = [IgnoreItemFragmentProvider::class])
  abstract fun bindIgnoreItemActivity(): IgnoreItemActivity

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
