package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
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
  fun provideAutoCompleteSettings(context: Context) = context.sharedPreferences {
    AutoCompleteSettings(
      button = getBoolean(
        context.getString(R.string.preference_autocomplete_button_key),
        AutoCompleteSettings.DEFAULT.button
      ),
      doubleTap = getBoolean(
        context.getString(R.string.preference_autocomplete_doubletap_key),
        AutoCompleteSettings.DEFAULT.button
      ),
      auto = getBoolean(
        context.getString(R.string.preference_autocomplete_auto_key),
        AutoCompleteSettings.DEFAULT.button
      ),
      prefix = getBoolean(
        context.getString(R.string.preference_autocomplete_prefix_key),
        AutoCompleteSettings.DEFAULT.button
      )
    )
  }

  @Provides
  fun provideBacklogSettings(context: Context) = context.sharedPreferences {
    BacklogSettings(
      pageSize = getString(
        context.getString(R.string.preference_page_size_key),
        BacklogSettings.DEFAULT.pageSize.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.pageSize,
      initialAmount = getString(
        context.getString(R.string.preference_initial_amount_key),
        BacklogSettings.DEFAULT.initialAmount.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.initialAmount
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