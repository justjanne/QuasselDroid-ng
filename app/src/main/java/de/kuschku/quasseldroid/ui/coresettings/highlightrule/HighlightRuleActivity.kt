package de.kuschku.quasseldroid.ui.coresettings.highlightrule

import android.content.Context
import android.content.Intent
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.quasseldroid.util.ui.SettingsActivity

class HighlightRuleActivity : SettingsActivity(HighlightRuleFragment()) {
  companion object {
    fun launch(
      context: Context,
      rule: HighlightRuleManager.HighlightRule? = null,
      ignore: Boolean = false
    ) = context.startActivity(intent(context, rule, ignore))

    fun intent(
      context: Context,
      rule: HighlightRuleManager.HighlightRule? = null,
      ignore: Boolean = false
    ) = Intent(context, HighlightRuleActivity::class.java).apply {
      if (rule != null) {
        putExtra("item", rule)
      }
      putExtra("ignore", ignore)
    }
  }
}
