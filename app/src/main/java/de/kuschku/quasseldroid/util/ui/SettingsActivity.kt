package de.kuschku.quasseldroid.util.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NavUtils
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity

abstract class SettingsActivity(private val fragment: Fragment? = null) : ServiceBoundActivity() {
  protected open fun fragment(): Fragment? = null

  private var changeable: SettingsFragment.Changeable? = null

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  override fun onCreate(savedInstanceState: Bundle?) {
    val arguments = intent.extras
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val fragment = this.fragment ?: this.fragment()

    if (fragment != null) {
      val transaction = supportFragmentManager.beginTransaction()
      fragment.arguments = arguments
      transaction.replace(R.id.fragment_container, fragment)
      transaction.commit()
    }

    this.changeable = fragment as? SettingsFragment.Changeable
  }

  private fun shouldNavigateAway(callback: () -> Unit) {
    val changeable = this.changeable
    if (changeable?.hasChanged() == true) {
      MaterialDialog.Builder(this)
        .content(R.string.cancel_confirmation)
        .positiveText(R.string.label_yes)
        .negativeText(R.string.label_no)
        .negativeColorAttr(R.attr.colorTextPrimary)
        .backgroundColorAttr(R.attr.colorBackgroundCard)
        .contentColorAttr(R.attr.colorTextPrimary)
        .onPositive { _, _ ->
          callback()
        }
        .build()
        .show()
    } else callback()
  }

  override fun onBackPressed() = shouldNavigateAway {
    super.onBackPressed()
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    android.R.id.home -> {
      shouldNavigateAway {
        if (supportParentActivityIntent == null) {
          super.onBackPressed()
        } else {
          NavUtils.navigateUpFromSameTask(this)
        }
      }
      true
    }
    else              -> super.onOptionsItemSelected(item)
  }
}
