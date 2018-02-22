package de.kuschku.quasseldroid_ng.ui.settings.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings.*

object Settings {
  private fun <T> settings(context: Context,
                           f: SharedPreferences.() -> T) = PreferenceManager.getDefaultSharedPreferences(
    context
  ).f()

  fun appearance(context: Context) = settings(
    context
  ) {
    AppearanceSettings(
      theme = Theme.valueOf(
        getString(
          context.getString(R.string.preference_theme_key),
          AppearanceSettings.DEFAULT.theme.name
        )
      ),
      showPrefix = ShowPrefixMode.valueOf(
        getString(
          context.getString(R.string.preference_show_prefix_key),
          AppearanceSettings.DEFAULT.showPrefix.name
        )
      ),
      colorizeNicknames = ColorizeNicknamesMode.valueOf(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          AppearanceSettings.DEFAULT.colorizeNicknames.name
        )
      ),
      colorizeMirc = getBoolean(
        context.getString(R.string.preference_colorize_mirc_key),
        AppearanceSettings.DEFAULT.colorizeMirc
      ),
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
      showLag = getBoolean(
        context.getString(R.string.preference_show_lag_key),
        AppearanceSettings.DEFAULT.showLag
      )
    )
  }

  fun backlog(context: Context) = settings(
    context
  ) {
    BacklogSettings(
      dynamicAmount = getString(
        context.getString(R.string.preference_dynamic_fetch_key),
        BacklogSettings.DEFAULT.dynamicAmount.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.dynamicAmount
    )
  }
}