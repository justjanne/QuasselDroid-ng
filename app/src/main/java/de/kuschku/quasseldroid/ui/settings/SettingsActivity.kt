package de.kuschku.quasseldroid.ui.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.service.ServiceBoundActivity

abstract class SettingsActivity(private val fragment: Fragment? = null) : ServiceBoundActivity() {
  protected open fun fragment(): Fragment? = null

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
  }
}