/*
 * QuasselDroid - Quassel client for Android
 *
 * Copyright (c) 2018 Janne Koschinski
 * Copyright (c) 2018 Ken Børge Viktil
 * Copyright (c) 2018 Magnus Fjell
 * Copyright (c) 2018 Martin Sandsmark
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
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
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.settings.AppearanceSettings
import de.kuschku.quasseldroid.settings.AutoCompleteSettings
import de.kuschku.quasseldroid.settings.MessageSettings
import de.kuschku.quasseldroid.ui.chat.input.*
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.helper.styledAttributes
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

  private lateinit var editorHelper: EditorHelper

  private lateinit var mircColorMap: Map<Int, Int>

  private var colorForegroundMirc: Int = 0

  private var rule: IAliasManager.Alias? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    val view = inflater.inflate(R.layout.settings_aliasitem, container, false)
    ButterKnife.bind(this, view)

    (arguments?.getSerializable("item") as? IAliasManager.Alias)?.let {
      rule = it
    }

    val mircColors = requireContext().theme.styledAttributes(
      R.attr.mircColor00, R.attr.mircColor01, R.attr.mircColor02, R.attr.mircColor03,
      R.attr.mircColor04, R.attr.mircColor05, R.attr.mircColor06, R.attr.mircColor07,
      R.attr.mircColor08, R.attr.mircColor09, R.attr.mircColor10, R.attr.mircColor11,
      R.attr.mircColor12, R.attr.mircColor13, R.attr.mircColor14, R.attr.mircColor15,
      R.attr.mircColor16, R.attr.mircColor17, R.attr.mircColor18, R.attr.mircColor19,
      R.attr.mircColor20, R.attr.mircColor21, R.attr.mircColor22, R.attr.mircColor23,
      R.attr.mircColor24, R.attr.mircColor25, R.attr.mircColor26, R.attr.mircColor27,
      R.attr.mircColor28, R.attr.mircColor29, R.attr.mircColor30, R.attr.mircColor31,
      R.attr.mircColor32, R.attr.mircColor33, R.attr.mircColor34, R.attr.mircColor35,
      R.attr.mircColor36, R.attr.mircColor37, R.attr.mircColor38, R.attr.mircColor39,
      R.attr.mircColor40, R.attr.mircColor41, R.attr.mircColor42, R.attr.mircColor43,
      R.attr.mircColor44, R.attr.mircColor45, R.attr.mircColor46, R.attr.mircColor47,
      R.attr.mircColor48, R.attr.mircColor49, R.attr.mircColor50, R.attr.mircColor51,
      R.attr.mircColor52, R.attr.mircColor53, R.attr.mircColor54, R.attr.mircColor55,
      R.attr.mircColor56, R.attr.mircColor57, R.attr.mircColor58, R.attr.mircColor59,
      R.attr.mircColor60, R.attr.mircColor61, R.attr.mircColor62, R.attr.mircColor63,
      R.attr.mircColor64, R.attr.mircColor65, R.attr.mircColor66, R.attr.mircColor67,
      R.attr.mircColor68, R.attr.mircColor69, R.attr.mircColor70, R.attr.mircColor71,
      R.attr.mircColor72, R.attr.mircColor73, R.attr.mircColor74, R.attr.mircColor75,
      R.attr.mircColor76, R.attr.mircColor77, R.attr.mircColor78, R.attr.mircColor79,
      R.attr.mircColor80, R.attr.mircColor81, R.attr.mircColor82, R.attr.mircColor83,
      R.attr.mircColor84, R.attr.mircColor85, R.attr.mircColor86, R.attr.mircColor87,
      R.attr.mircColor88, R.attr.mircColor89, R.attr.mircColor90, R.attr.mircColor91,
      R.attr.mircColor92, R.attr.mircColor93, R.attr.mircColor94, R.attr.mircColor95,
      R.attr.mircColor96, R.attr.mircColor97, R.attr.mircColor98
    ) {
      IntArray(99) {
        getColor(it, 0)
      }
    }

    mircColorMap = mircColors.take(16).mapIndexed { index: Int, color: Int ->
      color to index
    }.toMap()

    colorForegroundMirc = requireContext().theme.styledAttributes(R.attr.colorForegroundMirc) {
      getColor(0, 0)
    }

    val editorViewModel = ViewModelProviders.of(this).get(EditorViewModel::class.java)
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
      autoCompleteAdapter.setOnClickListener(expansion::autoComplete)
      autoCompleteList.layoutManager = LinearLayoutManager(activity)
      autoCompleteList.itemAnimator = DefaultItemAnimator()
      autoCompleteList.adapter = autoCompleteAdapter
      autoCompleteHelper.setDataListener {
        autoCompleteAdapter.submitList(it)
      }
    }

    rule?.let { data ->
      name.setText(data.name)
      expansion.setText(formatDeserializer.formatString(mircColors, data.expansion, true))
    }

    return view
  }

  private fun applyChanges() = IAliasManager.Alias(
    name = name.text.toString(),
    expansion = formatSerializer.toEscapeCodes(colorForegroundMirc, mircColorMap, expansion.text)
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
