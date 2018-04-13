package de.kuschku.quasseldroid.ui.coresettings.identity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import io.reactivex.Observable


class IdentityFragment : SettingsFragment() {
  private var identity: Pair<Identity, Identity>? = null

  @BindView(R.id.identity_name)
  lateinit var identityName: TextView

  @BindView(R.id.real_name)
  lateinit var realName: TextView

  @BindView(R.id.ident)
  lateinit var ident: TextView

  @BindView(R.id.nicks)
  lateinit var nicks: RecyclerView

  @BindView(R.id.new_nick)
  lateinit var newNick: Button

  @BindView(R.id.kick_reason)
  lateinit var kickReason: TextView

  @BindView(R.id.part_reason)
  lateinit var partReason: TextView

  @BindView(R.id.quit_reason)
  lateinit var quitReason: TextView

  @BindView(R.id.away_reason)
  lateinit var awayReason: TextView

  @BindView(R.id.detach_away)
  lateinit var detachAway: SwitchCompat

  @BindView(R.id.detach_away_group)
  lateinit var detachAwayGroup: ViewGroup

  @BindView(R.id.detach_away_reason)
  lateinit var detachAwayReason: TextView

  private lateinit var adapter: IdentityNicksAdapter
  private lateinit var helper: ItemTouchHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_identity, container, false)
    ButterKnife.bind(this, view)

    val identityId = arguments?.getInt("identity", -1) ?: -1

    adapter = IdentityNicksAdapter(::nickClick, ::startDrag)
    nicks.layoutManager = LinearLayoutManager(requireContext())
    nicks.adapter = adapter
    ViewCompat.setNestedScrollingEnabled(nicks, false)

    val callback = DragSortItemTouchHelperCallback(adapter)
    helper = ItemTouchHelper(callback)
    helper.attachToRecyclerView(nicks)

    newNick.setOnClickListener {
      MaterialDialog.Builder(requireContext())
        .input(null, null, false) { _, _ -> }
        .title(R.string.label_new_nick)
        .negativeText(R.string.label_cancel)
        .positiveText(R.string.label_save)
        .onPositive { dialog, _ ->
          dialog.inputEditText?.text?.toString()?.let {
            if (it.isNotBlank()) {
              adapter.addNick(it)
            }
          }
        }.build().show()
    }

    viewModel.identities.switchMap {
      it[identityId]?.liveUpdates() ?: Observable.empty()
    }.firstElement()
      .toLiveData().observe(this, Observer {
        if (it != null) {
          this.identity = Pair(it, it.copy())
          this.identity?.let { (_, data) ->
            identityName.text = data.identityName()
            realName.text = data.realName()
            ident.text = data.ident()
            kickReason.text = data.kickReason()
            partReason.text = data.partReason()
            quitReason.text = data.quitReason()
            awayReason.text = data.awayReason()
            detachAway.isChecked = data.detachAwayEnabled()
            detachAwayReason.text = data.detachAwayReason()
            adapter.nicks = data.nicks()
          }
        }
      })

    detachAway.setDependent(detachAwayGroup)

    return view
  }

  fun startDrag(holder: IdentityNicksAdapter.IdentityNickViewHolder) = helper.startDrag(holder)

  fun nickClick(index: Int, nick: String) {
    MaterialDialog.Builder(requireContext())
      .input(null, nick, false) { _, _ -> }
      .title(R.string.label_edit_nick)
      .negativeText(R.string.label_cancel)
      .positiveText(R.string.label_save)
      .onPositive { dialog, _ ->
        dialog.inputEditText?.text?.toString()?.let {
          if (it.isNotBlank()) {
            adapter.replaceNick(index, it)
          }
        }
      }.build().show()
  }

  override fun onSave() = identity?.let { (it, data) ->
    data.setIdentityName(identityName.text.toString())
    data.setRealName(realName.text.toString())
    data.setIdent(ident.text.toString())
    data.setKickReason(kickReason.text.toString())
    data.setPartReason(partReason.text.toString())
    data.setQuitReason(quitReason.text.toString())
    data.setAwayReason(awayReason.text.toString())
    data.setDetachAwayEnabled(detachAway.isChecked)
    data.setDetachAwayReason(detachAwayReason.text.toString())
    data.setNicks(adapter.nicks)

    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

}
