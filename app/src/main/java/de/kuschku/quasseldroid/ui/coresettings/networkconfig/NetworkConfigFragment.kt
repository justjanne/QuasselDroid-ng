package de.kuschku.quasseldroid.ui.coresettings.networkconfig

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.NetworkConfig
import de.kuschku.libquassel.session.ISession
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData

class NetworkConfigFragment : SettingsFragment() {
  private var networkConfig: NetworkConfig? = null

  @BindView(R.id.ping_timeout_enabled)
  lateinit var pingTimeoutEnabled: SwitchCompat

  @BindView(R.id.ping_timeout_group)
  lateinit var pingTimeoutGroup: ViewGroup

  @BindView(R.id.ping_interval)
  lateinit var pingInterval: EditText

  @BindView(R.id.max_ping_count)
  lateinit var maxPingCount: EditText

  @BindView(R.id.auto_who_enabled)
  lateinit var autoWhoEnabled: SwitchCompat

  @BindView(R.id.auto_who_group)
  lateinit var autoWhoGroup: ViewGroup

  @BindView(R.id.auto_who_interval)
  lateinit var autoWhoInterval: EditText

  @BindView(R.id.auto_who_nick_limit)
  lateinit var autoWhoNickLimit: EditText

  @BindView(R.id.auto_who_delay)
  lateinit var autoWhoDelay: EditText

  @BindView(R.id.standard_ctcp)
  lateinit var standardCtcp: SwitchCompat

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_networksettings, container, false)
    ButterKnife.bind(this, view)

    setHasOptionsMenu(true)

    viewModel.session.map { it.map(ISession::networkConfig) }.toLiveData().observe(this, Observer {
      it?.orNull()?.let {
        this.networkConfig = it

        pingTimeoutEnabled.isChecked = it.pingTimeoutEnabled()
        pingInterval.setText(it.pingInterval().toString())
        maxPingCount.setText(it.maxPingCount().toString())

        autoWhoEnabled.isChecked = it.autoWhoEnabled()
        autoWhoInterval.setText(it.autoWhoInterval().toString())
        autoWhoNickLimit.setText(it.autoWhoNickLimit().toString())
        autoWhoDelay.setText(it.autoWhoDelay().toString())

        standardCtcp.isChecked = it.standardCtcp()
      }
    })

    pingTimeoutEnabled.setDependent(pingTimeoutGroup)
    autoWhoEnabled.setDependent(autoWhoGroup)

    return view
  }


  override fun onSave() = networkConfig?.let {
    val config = it.copy()

    config.setPingTimeoutEnabled(pingTimeoutEnabled.isChecked)
    pingInterval.text.toString().toIntOrNull()?.let(config::setPingInterval)
    maxPingCount.text.toString().toIntOrNull()?.let(config::setMaxPingCount)

    config.setAutoWhoEnabled(autoWhoEnabled.isChecked)
    autoWhoInterval.text.toString().toIntOrNull()?.let(config::setAutoWhoInterval)
    autoWhoNickLimit.text.toString().toIntOrNull()?.let(config::setAutoWhoNickLimit)
    autoWhoDelay.text.toString().toIntOrNull()?.let(config::setAutoWhoDelay)
    config.setStandardCtcp(standardCtcp.isChecked)

    val properties = config.toVariantMap()
    it.requestUpdate(properties)

    true
  } ?: false
}