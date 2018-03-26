package de.kuschku.quasseldroid.settings

import android.content.Context
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings.*
import de.kuschku.quasseldroid.util.helper.sharedPreferences

object Settings {
  fun appearance(context: Context) = context.sharedPreferences {
    AppearanceSettings(
      theme = Theme.of(
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
      showPrefix = ShowPrefixMode.of(
        getString(
          context.getString(R.string.preference_show_prefix_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.showPrefix,
      colorizeNicknames = ColorizeNicknamesMode.of(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.colorizeNicknames,
      inputEnter = InputEnterMode.of(
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

  fun backlog(context: Context) = context.sharedPreferences {
    BacklogSettings(
      pageSize = getString(
        context.getString(R.string.preference_page_size_key),
        BacklogSettings.DEFAULT.pageSize.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.pageSize
    )
  }

  fun connection(context: Context) = context.sharedPreferences {
    ConnectionSettings(
      showNotification = getBoolean(
        context.getString(R.string.preference_show_notification_key),
        ConnectionSettings.DEFAULT.showNotification
      )
    )
  }
}