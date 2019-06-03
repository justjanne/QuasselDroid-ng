/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

package de.kuschku.quasseldroid.ui.chat.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.kuschku.libquassel.protocol.BufferId
import de.kuschku.libquassel.util.helper.invoke
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.input.*
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.util.ui.settings.fragment.Savable
import de.kuschku.quasseldroid.util.ui.settings.fragment.ServiceBoundSettingsFragment
import de.kuschku.quasseldroid.viewmodel.helper.EditorViewModelHelper
import javax.inject.Inject

class TopicFragment : ServiceBoundSettingsFragment(), Savable {
  @BindView(R.id.chatline)
  lateinit var chatline: RichEditText

  @BindView(R.id.formatting_toolbar)
  lateinit var toolbar: RichToolbar

  @BindView(R.id.autocomplete_list)
  lateinit var autoCompleteList: RecyclerView

  @Inject
  lateinit var autoCompleteSettings: AutoCompleteSettings

  @Inject
  lateinit var messageSettings: MessageSettings

  @Inject
  lateinit var appearanceSettings: AppearanceSettings

  @Inject
  lateinit var formatDeserializer: IrcFormatDeserializer

  @Inject
  lateinit var contentFormatter: ContentFormatter

  @Inject
  lateinit var formatSerializer: IrcFormatSerializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  @Inject
  lateinit var modelHelper: EditorViewModelHelper

  private lateinit var editorHelper: EditorHelper

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.info_topic, container, false)
    ButterKnife.bind(this, view)

    val autoCompleteHelper = AutoCompleteHelper(
      requireActivity(),
      autoCompleteSettings,
      messageSettings,
      formatDeserializer,
      contentFormatter,
      modelHelper
    )

    editorHelper = EditorHelper(
      requireActivity(),
      chatline,
      toolbar,
      autoCompleteHelper,
      autoCompleteSettings,
      appearanceSettings
    )

    modelHelper.editor.lastWord.onNext(editorHelper.lastWord)

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      val autoCompleteBottomSheet = BottomSheetBehavior.from(autoCompleteList)
      autoCompleteAdapter.setOnClickListener(chatline::autoComplete)
      autoCompleteList.layoutManager = LinearLayoutManager(activity)
      autoCompleteList.itemAnimator = DefaultItemAnimator()
      autoCompleteList.adapter = autoCompleteAdapter
      autoCompleteHelper.addDataListener {
        autoCompleteBottomSheet.state =
          if (it.isEmpty()) BottomSheetBehavior.STATE_HIDDEN
          else BottomSheetBehavior.STATE_COLLAPSED
        autoCompleteAdapter.submitList(it)
      }
    }

    val bufferId = BufferId(arguments?.getInt("bufferId", -1) ?: -1)
    modelHelper.chat.bufferId.onNext(bufferId)
    modelHelper.bufferData.filter {
      it.info != null
    }.firstElement().toLiveData().observe(this, Observer {
      chatline.setText(formatDeserializer.formatString(it?.description, true))
    })

    return view
  }

  override fun onSave(): Boolean {
    modelHelper.connectedSession { sessionOptional ->
      val session = sessionOptional.orNull()
      modelHelper.chat.bufferId { bufferId ->
        session?.bufferSyncer?.bufferInfo(bufferId)?.also { bufferInfo ->
          val topic = formatSerializer.toEscapeCodes(chatline.safeText)
          session.rpcHandler.sendInput(bufferInfo, "/topic $topic")
          return true
        }
      }
    }
    return false
  }
}
