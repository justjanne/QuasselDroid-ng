/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2020 Janne Mareike Koschinski
 * Copyright (c) 2020 The Quassel Project
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

package de.kuschku.quasseldroid.util.ui.presenter

import android.content.Context
import android.view.ActionMode
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.BufferInfo
import de.kuschku.libquassel.quassel.syncables.BufferSyncer
import de.kuschku.libquassel.quassel.syncables.BufferViewConfig
import de.kuschku.libquassel.quassel.syncables.Network
import de.kuschku.libquassel.quassel.syncables.interfaces.INetwork
import de.kuschku.libquassel.session.ISession
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.network.NetworkEditActivity
import de.kuschku.quasseldroid.ui.info.channellist.ChannelListActivity
import de.kuschku.quasseldroid.viewmodel.data.BufferHiddenState
import de.kuschku.quasseldroid.viewmodel.data.SelectedBufferItem

object BufferContextPresenter {
  fun present(actionMode: ActionMode, buffer: SelectedBufferItem?) {
    if (buffer != null) {
      val menu = actionMode.menu
      if (menu != null) {
        val allActions = setOf(
          R.id.action_channellist,
          R.id.action_configure,
          R.id.action_connect,
          R.id.action_disconnect,
          R.id.action_join,
          R.id.action_part,
          R.id.action_delete,
          R.id.action_rename,
          R.id.action_unhide,
          R.id.action_archive
        )

        val visibilityActions = when (buffer.hiddenState) {
          BufferHiddenState.VISIBLE -> setOf(
            R.id.action_archive
          )
          BufferHiddenState.HIDDEN_TEMPORARY -> setOf(
            R.id.action_unhide
          )
          BufferHiddenState.HIDDEN_PERMANENT -> setOf(
            R.id.action_unhide
          )
        }

        val availableActions = when (buffer.info?.type?.enabledValues()?.firstOrNull()) {
          Buffer_Type.StatusBuffer -> {
            when (buffer.connectionState) {
              INetwork.ConnectionState.Disconnected -> setOf(
                R.id.action_configure, R.id.action_connect
              )
              INetwork.ConnectionState.Initialized -> setOf(
                R.id.action_channellist, R.id.action_configure, R.id.action_disconnect
              )
              else -> setOf(
                R.id.action_configure, R.id.action_connect, R.id.action_disconnect
              )
            }
          }
          Buffer_Type.ChannelBuffer -> {
            if (buffer.joined) {
              setOf(R.id.action_part)
            } else {
              setOf(R.id.action_join, R.id.action_delete)
            } + visibilityActions
          }
          Buffer_Type.QueryBuffer -> {
            setOf(R.id.action_delete, R.id.action_rename) + visibilityActions
          }
          else -> visibilityActions
        }

        val unavailableActions = allActions - availableActions

        for (action in availableActions) {
          menu.findItem(action)?.isVisible = true
        }
        for (action in unavailableActions) {
          menu.findItem(action)?.isVisible = false
        }
      }
    } else {
      actionMode.finish()
    }
  }

  fun handleAction(
    context: Context,
    actionMode: ActionMode,
    item: MenuItem,
    info: BufferInfo?,
    session: ISession?,
    bufferSyncer: BufferSyncer?,
    bufferViewConfig: BufferViewConfig?,
    network: Network?
  ) = when (item.itemId) {
    R.id.action_channellist -> {
      network?.let {
        ChannelListActivity.launch(context, network = it.networkId())
      }
      actionMode.finish()
      true
    }
    R.id.action_configure -> {
      network?.let {
        NetworkEditActivity.launch(context, network = it.networkId())
      }
      actionMode.finish()
      true
    }
    R.id.action_connect -> {
      network?.requestConnect()
      actionMode.finish()
      true
    }
    R.id.action_disconnect -> {
      network?.requestDisconnect()
      actionMode.finish()
      true
    }
    R.id.action_join -> {
      if (info != null) {
        session?.rpcHandler?.sendInput(info, "/join ${info.bufferName}")
        actionMode.finish()
        true
      } else {
        false
      }
    }
    R.id.action_part -> {
      if (info != null) {
        session?.rpcHandler?.sendInput(info, "/part ${info.bufferName}")
        actionMode.finish()
        true
      } else {
        false
      }
    }
    R.id.action_delete -> {
      if (info != null) {
        MaterialDialog.Builder(context)
          .content(R.string.buffer_delete_confirmation)
          .positiveText(R.string.label_yes)
          .negativeText(R.string.label_no)
          .negativeColorAttr(R.attr.colorTextPrimary)
          .backgroundColorAttr(R.attr.colorBackgroundCard)
          .contentColorAttr(R.attr.colorTextPrimary)
          .onPositive { _, _ ->
            session?.bufferSyncer?.requestRemoveBuffer(info.bufferId)
          }
          .onAny { _, _ ->
            actionMode.finish()
          }
          .build()
          .show()
        true
      } else {
        false
      }
    }
    R.id.action_rename -> {
      if (info != null && bufferSyncer != null) {
        MaterialDialog.Builder(context)
          .input(
            context.getString(R.string.label_buffer_name),
            info.bufferName,
            false
          ) { _, input ->
            session?.bufferSyncer?.requestRenameBuffer(info.bufferId, input.toString())
          }
          .positiveText(R.string.label_save)
          .negativeText(R.string.label_cancel)
          .negativeColorAttr(R.attr.colorTextPrimary)
          .backgroundColorAttr(R.attr.colorBackgroundCard)
          .contentColorAttr(R.attr.colorTextPrimary)
          .onAny { _, _ ->
            actionMode.finish()
          }
          .build()
          .show()
        true
      } else {
        false
      }
    }
    R.id.action_unhide -> {
      if (info != null && bufferSyncer != null) {
        bufferViewConfig?.insertBufferSorted(info, bufferSyncer)
        actionMode.finish()
        true
      } else {
        false
      }
    }
    R.id.action_archive -> {
      if (info != null) {
        MaterialDialog.Builder(context)
          .title(R.string.label_archive_chat)
          .content(R.string.buffer_archive_confirmation)
          .checkBoxPromptRes(R.string.buffer_archive_temporarily, true, null)
          .positiveText(R.string.label_archive)
          .negativeText(R.string.label_cancel)
          .negativeColorAttr(R.attr.colorTextPrimary)
          .backgroundColorAttr(R.attr.colorBackgroundCard)
          .contentColorAttr(R.attr.colorTextPrimary)
          .onAny { _, _ ->
            actionMode.finish()
          }
          .onPositive { dialog, _ ->
            if (dialog.isPromptCheckBoxChecked) {
              bufferViewConfig?.requestRemoveBuffer(info.bufferId)
            } else {
              bufferViewConfig?.requestRemoveBufferPermanently(info.bufferId)
            }
          }
          .build()
          .show()
        true
      } else {
        false
      }
    }
    else -> false
  }
}
