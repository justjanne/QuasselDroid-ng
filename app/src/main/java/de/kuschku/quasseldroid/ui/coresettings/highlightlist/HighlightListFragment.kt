package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment

class HighlightListFragment : SettingsFragment(), SettingsFragment.Savable,
                              SettingsFragment.Changeable {
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return super.onCreateView(inflater, container, savedInstanceState)
  }

  override fun hasChanged(): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun onSave(): Boolean {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}
