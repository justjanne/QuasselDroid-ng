package de.kuschku.quasseldroid.ui.chat.info.user

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class UserInfoFragmentProvider {
  @ContributesAndroidInjector
  abstract fun bindUserInfoFragment(): UserInfoFragment
}
