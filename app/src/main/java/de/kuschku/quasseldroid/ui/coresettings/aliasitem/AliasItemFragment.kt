/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 The Quassel Project
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 3 as published
 * by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid.ui.coresettings.aliasitem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.input.*
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.compatibility.AndroidCrashFixer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import de.kuschku.quasseldroid.util.irc.format.IrcFormatSerializer
import de.kuschku.quasseldroid.viewmodel.EditorViewModel
import javax.inject.Inject

class AliasItemFragment : SettingsFragment(), SettingsFragment.Savable,
                          SettingsFragment.Changeable {

  @BindView(R.id.name)
  lateinit var name: EditText

  @BindView(R.id.expansion)
  lateinit var expansion: RichEditText

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
  lateinit var formatSerializer: IrcFormatSerializer

  @Inject
  lateinit var autoCompleteAdapter: AutoCompleteAdapter

  @Inject
  lateinit var editorViewModel: EditorViewModel

  private lateinit var editorHelper: EditorHelper

  private var rule: IAliasManager.Alias? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.settings_aliasitem, container, false)
    ButterKnife.bind(this, view)

    (arguments?.getSerializable("item") as? IAliasManager.Alias)?.let {
      rule = it
    }

    editorViewModel.quasselViewModel.onNext(viewModel)

    val autoCompleteHelper = AutoCompleteHelper(
      requireActivity(),
      autoCompleteSettings,
      messageSettings,
      formatDeserializer,
      editorViewModel
    )

    editorHelper = EditorHelper(
      requireActivity(),
      expansion,
      toolbar,
      autoCompleteHelper,
      autoCompleteSettings,
      appearanceSettings
    )

    editorViewModel.lastWord.onNext(editorHelper.lastWord)

    if (autoCompleteSettings.prefix || autoCompleteSettings.auto) {
      val autoCompleteBottomSheet = BottomSheetBehavior.from(autoCompleteList)
      autoCompleteAdapter.setOnClickListener(expansion::autoComplete)
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

    rule?.let { data ->
      name.setText(AndroidCrashFixer.removeCrashableCharacters(data.name))
      expansion.setText(formatDeserializer.formatString(data.expansion, true))
    }

    return view
  }

  private fun applyChanges() = IAliasManager.Alias(
    name = name.text.toString(),
    expansion = formatSerializer.toEscapeCodes(expansion.safeText)
  )

  override fun onSave() = rule.let { data ->
    requireActivity().setResult(
      Activity.RESULT_OK,
      Intent().also {
        it.putExtra("old", data)
        it.putExtra("new", applyChanges())
      }
    )
    true
  }

  override fun hasChanged() = rule != applyChanges()
}
