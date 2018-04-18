package de.kuschku.quasseldroid.ui.coresettings.aliasitem

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.util.ui.ServiceBoundSettingsActivity

class AliasItemActivity : ServiceBoundSettingsActivity(AliasItemFragment()) {
  companion object {
    fun launch(
      context: Context,
      item: IAliasManager.Alias? = null
    ) = context.startActivity(intent(context, item))

    fun intent(
      context: Context,
      item: IAliasManager.Alias? = null
    ) = Intent(context, AliasItemActivity::class.java).apply {
      if (item != null) {
        putExtra("item", item)
      }
    }
  }
}
