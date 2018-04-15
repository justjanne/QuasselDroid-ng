package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class IgnoreItemActivity : SettingsActivity(IgnoreItemFragment()) {
  companion object {
    fun launch(
      context: Context,
      item: IgnoreListManager.IgnoreListItem? = null
    ) = context.startActivity(intent(context, item))

    fun intent(
      context: Context,
      item: IgnoreListManager.IgnoreListItem? = null
    ) = Intent(context, IgnoreItemActivity::class.java).apply {
      if (item != null) {
        putExtra("item", item)
      }
    }
  }
}
