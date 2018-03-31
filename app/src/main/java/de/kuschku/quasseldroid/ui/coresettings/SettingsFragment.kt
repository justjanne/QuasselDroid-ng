package de.kuschku.quasseldroid.ui.coresettings

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment

abstract class SettingsFragment : ServiceBoundFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
    inflater?.inflate(R.menu.context_setting, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
    R.id.action_save -> {
      if (onSave()) activity?.finish()
      true
    }
    else             -> super.onOptionsItemSelected(item)
  }

  abstract fun onSave(): Boolean
}