package de.kuschku.quasseldroid_ng.ui.settings.data

import android.content.Context
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings.*
import de.kuschku.quasseldroid_ng.util.helper.sharedPreferences

object Settings {
  fun appearance(context: Context) = context.sharedPreferences {
    AppearanceSettings(
      theme = Theme.of(
        getString(
          context.getString(R.string.preference_theme_key),
          null
        )
      ) ?: AppearanceSettings.DEFAULT.theme,
      useMonospace = getBoolean(
        context.getString(R.string.preference_monospace_key),
        AppearanceSettings.DEFAULT.useMonospace
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
          null
        )
      ) ?: AppearanceSettings.DEFAULT.showPrefix,
      colorizeNicknames = ColorizeNicknamesMode.of(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          null
        )
      ) ?: AppearanceSettings.DEFAULT.colorizeNicknames,
      inputEnter = InputEnterMode.of(
        getString(
          context.getString(R.string.preference_input_enter_key),
          null
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
      dynamicAmount = getString(
        context.getString(R.string.preference_dynamic_fetch_key),
        BacklogSettings.DEFAULT.dynamicAmount.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.dynamicAmount
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