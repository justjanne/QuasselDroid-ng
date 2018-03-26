package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.settings.Settings

@Module
class SettingsModule {
  @Provides
  fun provideAppearanceSettings(context: Context) = Settings.appearance(context)

  @Provides
  fun provideAutoCompleteSettings(context: Context) = Settings.autoComplete(context)

  @Provides
  fun provideBacklogSettings(context: Context) = Settings.backlog(context)

  @Provides
  fun provideConnectionSettings(context: Context) = Settings.connection(context)
}