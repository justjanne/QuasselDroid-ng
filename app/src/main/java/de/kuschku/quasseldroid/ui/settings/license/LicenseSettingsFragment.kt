package de.kuschku.quasseldroid.ui.settings.license

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.R

class LicenseSettingsFragment : DaggerFragment() {
  @BindView(R.id.name)
  lateinit var name: TextView

  @BindView(R.id.text)
  lateinit var text: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_license, container, false)
    ButterKnife.bind(this, view)

    name.text = arguments?.getString("license_name")

    val textResource = arguments?.getInt("license_text", 0) ?: 0
    if (textResource != 0) {
      text.text = Html.fromHtml(getString(textResource))
    }

    return view
  }
}