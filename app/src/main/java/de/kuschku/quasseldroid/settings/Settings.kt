package de.kuschku.quasseldroid.settings

import android.content.Context
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings.InputEnterMode
import de.kuschku.quasseldroid.settings.AppearanceSettings.Theme
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
      inputEnter = InputEnterMode.of(
        getString(
          context.getString(R.string.preference_input_enter_key),
          ""
        )
      ) ?: AppearanceSettings.DEFAULT.inputEnter,
      showLag = getBoolean(
        context.getString(R.string.preference_show_lag_key),
        AppearanceSettings.DEFAULT.showLag
      )
    )
  }

  fun message(context: Context) = context.sharedPreferences {
    MessageSettings(
      useMonospace = getBoolean(
        context.getString(R.string.preference_monospace_key),
        MessageSettings.DEFAULT.useMonospace
      ),
      textSize = getInt(
        context.getString(R.string.preference_textsize_key),
        MessageSettings.DEFAULT.textSize
      ),
      showSeconds = getBoolean(
        context.getString(R.string.preference_show_seconds_key),
        MessageSettings.DEFAULT.showSeconds
      ),
      use24hClock = getBoolean(
        context.getString(R.string.preference_use_24h_clock_key),
        MessageSettings.DEFAULT.use24hClock
      ),
      showPrefix = MessageSettings.ShowPrefixMode.of(
        getString(
          context.getString(R.string.preference_show_prefix_key),
          ""
        )
      ) ?: MessageSettings.DEFAULT.showPrefix,
      colorizeNicknames = MessageSettings.ColorizeNicknamesMode.of(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          ""
        )
      ) ?: MessageSettings.DEFAULT.colorizeNicknames,
      colorizeMirc = getBoolean(
        context.getString(R.string.preference_colorize_mirc_key),
        MessageSettings.DEFAULT.colorizeMirc
      ),
      showHostmaskActions = getBoolean(
        context.getString(R.string.preference_hostmask_actions_key),
        MessageSettings.DEFAULT.showHostmaskActions
      ),
      showHostmaskPlain = getBoolean(
        context.getString(R.string.preference_hostmask_plain_key),
        MessageSettings.DEFAULT.showHostmaskPlain
      ),
      nicksOnNewLine = getBoolean(
        context.getString(R.string.preference_nicks_on_new_line_key),
        MessageSettings.DEFAULT.nicksOnNewLine
      ),
      timeAtEnd = getBoolean(
        context.getString(R.string.preference_time_at_end_key),
        MessageSettings.DEFAULT.timeAtEnd
      ),
      showRealNames = getBoolean(
        context.getString(R.string.preference_show_realnames_key),
        MessageSettings.DEFAULT.showRealNames
      ),
      showAvatars = getBoolean(
        context.getString(R.string.preference_show_avatars_key),
        MessageSettings.DEFAULT.showAvatars
      ),
      showIRCCloudAvatars = getBoolean(
        context.getString(R.string.preference_show_irccloud_avatars_key),
        MessageSettings.DEFAULT.showIRCCloudAvatars
      ),
      showGravatarAvatars = getBoolean(
        context.getString(R.string.preference_show_gravatar_avatars_key),
        MessageSettings.DEFAULT.showGravatarAvatars
      ),
      largerEmoji = getBoolean(
        context.getString(R.string.preference_larger_emoji_key),
        MessageSettings.DEFAULT.largerEmoji
      )
    )
  }

  fun autoComplete(context: Context) = context.sharedPreferences {
    AutoCompleteSettings(
      senderDoubleClick = getBoolean(
        context.getString(R.string.preference_autocomplete_sender_doubleclick_key),
        AutoCompleteSettings.DEFAULT.senderDoubleClick
      ),
      button = getBoolean(
        context.getString(R.string.preference_autocomplete_button_key),
        AutoCompleteSettings.DEFAULT.button
      ),
      doubleTap = getBoolean(
        context.getString(R.string.preference_autocomplete_doubletap_key),
        AutoCompleteSettings.DEFAULT.doubleTap
      ),
      auto = getBoolean(
        context.getString(R.string.preference_autocomplete_auto_key),
        AutoCompleteSettings.DEFAULT.auto
      ),
      prefix = getBoolean(
        context.getString(R.string.preference_autocomplete_prefix_key),
        AutoCompleteSettings.DEFAULT.prefix
      )
    )
  }

  fun backlog(context: Context) = context.sharedPreferences {
    BacklogSettings(
      initialAmount = getString(
        context.getString(R.string.preference_initial_amount_key),
        BacklogSettings.DEFAULT.initialAmount.toString()
      ).toIntOrNull() ?: BacklogSettings.DEFAULT.initialAmount,
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
