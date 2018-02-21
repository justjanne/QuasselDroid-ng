package de.kuschku.quasseldroid_ng.ui.settings

import android.content.Context
import de.kuschku.quasseldroid_ng.ui.settings.data.AppearanceSettings
import de.kuschku.quasseldroid_ng.ui.settings.data.BacklogSettings

object Settings {
  fun appearance(context: Context) = AppearanceSettings()
  fun backlog(context: Context) = BacklogSettings()
}