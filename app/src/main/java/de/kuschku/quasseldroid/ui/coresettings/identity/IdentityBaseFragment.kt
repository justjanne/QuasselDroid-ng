/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Koschinski
 * Copyright (c) 2019 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.coresettings.identity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.libquassel.protocol.IdentityId
import de.kuschku.libquassel.quassel.syncables.Identity
import de.kuschku.libquassel.session.ISession
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.defaults.Defaults
import de.kuschku.quasseldroid.util.helper.setDependent
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.settings.fragment.Changeable
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

abstract class IdentityBaseFragment(private val initDefault: Boolean) :
  ServiceBoundSettingsFragment(), Savable, Changeable {

  @BindView(R.id.identity_name)
  lateinit var identityName: EditText

  @BindView(R.id.real_name)
  lateinit var realName: EditText

  @BindView(R.id.ident)
  lateinit var ident: EditText

  @BindView(R.id.nicks)
  lateinit var nicks: RecyclerView

  @BindView(R.id.new_nick)
  lateinit var newNick: Button

  @BindView(R.id.kick_reason)
  lateinit var kickReason: EditText

  @BindView(R.id.part_reason)
  lateinit var partReason: EditText

  @BindView(R.id.quit_reason)
  lateinit var quitReason: EditText

  @BindView(R.id.away_reason)
  lateinit var awayReason: EditText

  @BindView(R.id.detach_away)
  lateinit var detachAway: SwitchCompat

  @BindView(R.id.detach_away_group)
  lateinit var detachAwayGroup: ViewGroup

  @BindView(R.id.detach_away_reason)
  lateinit var detachAwayReason: EditText

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  protected var identity: Pair<Identity?, Identity>? = null

  private lateinit var adapter: IdentityNicksAdapter
  private lateinit var helper: ItemTouchHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_identity, container, false)
    ButterKnife.bind(this, view)

    val identityId = IdentityId(arguments?.getInt("identity", -1) ?: -1)

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

    if (initDefault) {
      modelHelper.session
        .filter(Optional<ISession>::isPresent)
        .map(Optional<ISession>::get)
        .firstElement()
        .toLiveData().observe(this, Observer {
          it?.let {
            update(Defaults.identity(requireContext(), it.proxy))
          }
        })
    } else {
      modelHelper.identities.map { Optional.ofNullable(it[identityId]) }
        .filter(Optional<Identity>::isPresent)
        .map(Optional<Identity>::get)
        .firstElement()
        .toLiveData().observe(this, Observer {
          it?.let {
            update(it)
          }
        })
    }

    detachAway.setDependent(detachAwayGroup)

    return view
  }

  private fun update(it: Identity) {
    if (this.identity == null) {
      this.identity = Pair(it, it.copy())
      this.identity?.let { (_, data) ->
        identityName.setText(data.identityName())
        realName.setText(data.realName())
        ident.setText(data.ident())
        kickReason.setText(data.kickReason())
        partReason.setText(data.partReason())
        quitReason.setText(data.quitReason())
        awayReason.setText(data.awayReason())
        detachAway.isChecked = data.detachAwayEnabled()
        detachAwayReason.setText(data.detachAwayReason())
        adapter.nicks = data.nicks()
      }
    }
  }

  private fun startDrag(holder: IdentityNicksAdapter.IdentityNickViewHolder) =
    helper.startDrag(holder)

  private fun nickClick(index: Int, nick: String) {
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

  override fun hasChanged() = identity?.let { (it, data) ->
    applyChanges(data)
    it == null || !data.isEqual(it)
  } ?: true

  protected fun applyChanges(data: Identity) {
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
  }
}
