/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
        ) ?: ""
      ) ?: AppearanceSettings.DEFAULT.theme,
      inputEnter = InputEnterMode.of(
        getString(
          context.getString(R.string.preference_input_enter_key),
          ""
        ) ?: ""
      ) ?: AppearanceSettings.DEFAULT.inputEnter,
      showLag = getBoolean(
        context.getString(R.string.preference_show_lag_key),
        AppearanceSettings.DEFAULT.showLag
      ),
      keepScreenOn = getBoolean(
        context.getString(R.string.preference_keep_screen_on_key),
        AppearanceSettings.DEFAULT.keepScreenOn
      ),
      language = getString(
        context.getString(R.string.preference_language_key),
        AppearanceSettings.DEFAULT.language
      ) ?: ""
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
        ) ?: ""
      ) ?: MessageSettings.DEFAULT.showPrefix,
      colorizeNicknames = MessageSettings.ColorizeNicknamesMode.of(
        getString(
          context.getString(R.string.preference_colorize_nicknames_key),
          ""
        ) ?: ""
      ) ?: MessageSettings.DEFAULT.colorizeNicknames,
      colorizeMirc = getBoolean(
        context.getString(R.string.preference_colorize_mirc_key),
        MessageSettings.DEFAULT.colorizeMirc
      ),
      showHostmaskActions = getBoolean(
        context.getString(R.string.preference_hostmask_actions_key),
        MessageSettings.DEFAULT.showHostmaskActions
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
      squareAvatars = getBoolean(
        context.getString(R.string.preference_square_avatars_key),
        MessageSettings.DEFAULT.squareAvatars
      ),
      showIRCCloudAvatars = getBoolean(
        context.getString(R.string.preference_show_irccloud_avatars_key),
        MessageSettings.DEFAULT.showIRCCloudAvatars
      ),
      showGravatarAvatars = getBoolean(
        context.getString(R.string.preference_show_gravatar_avatars_key),
        MessageSettings.DEFAULT.showGravatarAvatars
      ),
      showMatrixAvatars = getBoolean(
        context.getString(R.string.preference_show_matrix_avatars_key),
        MessageSettings.DEFAULT.showMatrixAvatars
      ),
      largerEmoji = getBoolean(
        context.getString(R.string.preference_larger_emoji_key),
        MessageSettings.DEFAULT.largerEmoji
      ),
      highlightOwnMessages = getBoolean(
        context.getString(R.string.preference_highlight_own_messages_key),
        MessageSettings.DEFAULT.highlightOwnMessages
      )
    )
  }

  fun notification(context: Context) = context.sharedPreferences {
    NotificationSettings(
      query = NotificationSettings.Level.of(
        getString(
          context.getString(R.string.preference_notification_query_key),
          ""
        ) ?: ""
      ) ?: NotificationSettings.DEFAULT.query,
      channel = NotificationSettings.Level.of(
        getString(
          context.getString(R.string.preference_notification_channel_key),
          ""
        ) ?: ""
      ) ?: NotificationSettings.DEFAULT.channel,
      other = NotificationSettings.Level.of(
        getString(
          context.getString(R.string.preference_notification_other_key),
          ""
        ) ?: ""
      ) ?: NotificationSettings.DEFAULT.other,
      sound = getString(
        context.getString(R.string.preference_notification_sound_key),
        NotificationSettings.DEFAULT.sound
      ) ?: "",
      vibrate = getBoolean(
        context.getString(R.string.preference_notification_vibration_key),
        NotificationSettings.DEFAULT.vibrate
      ),
      light = getBoolean(
        context.getString(R.string.preference_notification_light_key),
        NotificationSettings.DEFAULT.light
      ),
      markReadOnSwipe = getBoolean(
        context.getString(R.string.preference_notification_mark_read_on_swipe_key),
        NotificationSettings.DEFAULT.markReadOnSwipe
      ),
      networkNameInNotificationTitle = getBoolean(
        context.getString(R.string.preference_notification_network_name_in_notification_title_key),
        NotificationSettings.DEFAULT.networkNameInNotificationTitle
      ),
      showAllActivitiesInToolbar = getBoolean(
        context.getString(R.string.preference_notification_show_all_activities_in_toolbar_key),
        NotificationSettings.DEFAULT.showAllActivitiesInToolbar
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
      ),
      nicks = getBoolean(
        context.getString(R.string.preference_autocomplete_nicks_key),
        AutoCompleteSettings.DEFAULT.nicks
      ),
      buffers = getBoolean(
        context.getString(R.string.preference_autocomplete_buffers_key),
        AutoCompleteSettings.DEFAULT.buffers
      ),
      aliases = getBoolean(
        context.getString(R.string.preference_autocomplete_aliases_key),
        AutoCompleteSettings.DEFAULT.aliases
      )
    )
  }

  fun backlog(context: Context) = context.sharedPreferences {
    BacklogSettings(
      initialAmount = getString(
        context.getString(R.string.preference_initial_amount_key),
        BacklogSettings.DEFAULT.initialAmount.toString()
      )?.toIntOrNull() ?: BacklogSettings.DEFAULT.initialAmount,
      pageSize = getString(
        context.getString(R.string.preference_page_size_key),
        BacklogSettings.DEFAULT.pageSize.toString()
      )?.toIntOrNull() ?: BacklogSettings.DEFAULT.pageSize
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
