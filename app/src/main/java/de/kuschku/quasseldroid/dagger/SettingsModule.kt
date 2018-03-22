package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.BacklogSettings
import de.kuschku.quasseldroid.settings.ConnectionSettings
import de.kuschku.quasseldroid.util.helper.sharedPreferences

@Module
class SettingsModule {
  @Provides
  fun provideAppearanceSettings(context: Context) = context.sharedPreferences {
    AppearanceSettings(
      theme = AppearanceSettings.Theme.of(
        getString(
          context.getString(R.string.preference_theme_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.theme,
      useMonospace = getBoolean(
        context.getString(R.string.preference_monospace_key),
        AppearanceSettings.DEFAULT.useMonospace
      ),
      textSize = getInt(
        context.getString(R.string.preference_textsize_key),
        AppearanceSettings.DEFAULT.textSize
      ),
      showSeconds = getBoolean(
        context.getString(R.string.preference_show_seconds_key),
        AppearanceSettings.DEFAULT.showSeconds
      ),
      use24hClock = getBoolean(
        context.getString(R.string.preference_use_24h_clock_key),
        AppearanceSettings.DEFAULT.use24hClock
      ),
      showPrefix = AppearanceSettings.ShowPrefixMode.of(
        getString(
          context.getString(R.string.preference_show_prefix_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.showPrefix,
      colorizeNicknames = AppearanceSettings.ColorizeNicknamesMode.of(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.colorizeNicknames,
      inputEnter = AppearanceSettings.InputEnterMode.of(
        getString(
          context.getString(R.string.preference_input_enter_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.inputEnter,
      colorizeMirc = getBoolean(
        context.getString(R.string.preference_colorize_mirc_key),
        AppearanceSettings.DEFAULT.colorizeMirc
      ),
      showAutocomplete = getBoolean(
        context.getString(R.string.preference_autocomplete_key),
        AppearanceSettings.DEFAULT.showAutocomplete
      ),
      showHostmask = getBoolean(
        context.getString(R.string.preference_hostmask_key),
        AppearanceSettings.DEFAULT.showHostmask
      ),
      showLag = getBoolean(
        context.getString(R.string.preference_show_lag_key),
        AppearanceSettings.DEFAULT.showLag
      )
    )
  }

  @Provides
  fun provideBacklogSettings(context: Context) = context.sharedPreferences {
    BacklogSettings(
      dynamicAmount = getString(
        context.getString(R.string.preference_page_size_key),
        BacklogSettings.DEFAULT.dynamicAmount.toString()
      ).toIntOrNull()
                      ?: BacklogSettings.DEFAULT.dynamicAmount
    )
  }

  @Provides
  fun provideConnectionSettings(context: Context) = context.sharedPreferences {
    ConnectionSettings(
      showNotification = getBoolean(
        context.getString(R.string.preference_show_notification_key),
        ConnectionSettings.DEFAULT.showNotification
      )
    )
  }
}