package de.kuschku.quasseldroid.dagger

import dagger.Module
import dagger.android.ContributesAndroidInjector
import de.kuschku.quasseldroid.service.QuasselService
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.ChatActivityModule
import de.kuschku.quasseldroid.ui.chat.ChatFragmentProvider
import de.kuschku.quasseldroid.ui.settings.app.AppSettingsActivity
import de.kuschku.quasseldroid.ui.settings.app.AppSettingsFragmentProvider
import de.kuschku.quasseldroid.ui.setup.accounts.edit.AccountEditActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionActivity
import de.kuschku.quasseldroid.ui.setup.accounts.selection.AccountSelectionFragmentProvider
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupActivity
import de.kuschku.quasseldroid.ui.setup.accounts.setup.AccountSetupFragmentProvider

@Module
abstract class ActivityModule {
  @ContributesAndroidInjector(modules = [ChatActivityModule::class, ChatFragmentProvider::class])
  abstract fun bindChatActivity(): ChatActivity

  @ContributesAndroidInjector(modules = [AppSettingsFragmentProvider::class])
  abstract fun bindAppSettingsActivity(): AppSettingsActivity

  @ContributesAndroidInjector(modules = [AccountSetupFragmentProvider::class])
  abstract fun bindAccountSetupActivity(): AccountSetupActivity

  @ContributesAndroidInjector(modules = [AccountSelectionFragmentProvider::class])
  abstract fun bindAccountSelectionActivity(): AccountSelectionActivity

  @ContributesAndroidInjector
  abstract fun bindAccountEditActivity(): AccountEditActivity

  @ContributesAndroidInjector
  abstract fun bindQuasselService(): QuasselService
}