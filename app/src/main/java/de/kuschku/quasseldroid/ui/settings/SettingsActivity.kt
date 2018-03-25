package de.kuschku.quasseldroid.ui.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity

abstract class SettingsActivity : ServiceBoundActivity() {
  protected abstract val fragment: Fragment

  @BindView(R.id.toolbar)
  lateinit var toolbar: Toolbar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)
    ButterKnife.bind(this)

    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)

    val transaction = supportFragmentManager.beginTransaction()
    transaction.add(R.id.fragment_container, fragment)
    transaction.commit()
  }
}