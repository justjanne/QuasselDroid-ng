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

package de.kuschku.quasseldroid.ui.coresettings.highlightlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.ExtendedFeature
import de.kuschku.libquassel.quassel.syncables.HighlightRuleManager
import de.kuschku.libquassel.quassel.syncables.interfaces.IHighlightRuleManager
import de.kuschku.libquassel.util.Optional
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.ui.coresettings.highlightrule.HighlightRuleActivity
import de.kuschku.quasseldroid.util.helper.toLiveData
import de.kuschku.quasseldroid.util.ui.WarningBarView

class HighlightListFragment : SettingsFragment(), SettingsFragment.Savable,
                              SettingsFragment.Changeable {
  @BindView(R.id.feature_context_coresidehighlights)
  lateinit var featureContextCoreSideHighlights: WarningBarView

  @BindView(R.id.highlight_nick_type)
  lateinit var highlightNickType: Spinner

  @BindView(R.id.is_case_sensitive)
  lateinit var isCaseSensitive: SwitchCompat

  @BindView(R.id.highlight_rules)
  lateinit var rules: RecyclerView

  @BindView(R.id.new_highlight_rule)
  lateinit var newHighlightRule: Button

  @BindView(R.id.highlight_ignore_rules)
  lateinit var ignoreRules: RecyclerView

  @BindView(R.id.new_highlight_ignore_rule)
  lateinit var newHighlightIgnoreRule: Button

  private var ruleManager: Pair<HighlightRuleManager, HighlightRuleManager>? = null

  private lateinit var rulesHelper: ItemTouchHelper

  private lateinit var ignoreRulesHelper: ItemTouchHelper

  private val rulesAdapter = HighlightRuleAdapter(::ruleClick, ::startRuleDrag)

  private val ignoreRulesAdapter = HighlightRuleAdapter(::ignoreRuleClick, ::startIgnoreRuleDrag)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_highlightlist, container, false)
    ButterKnife.bind(this, view)

    rules.adapter = rulesAdapter
    rules.layoutManager = LinearLayoutManager(requireContext())
    rules.itemAnimator = DefaultItemAnimator()

    rulesHelper = ItemTouchHelper(DragSortItemTouchHelperCallback(rulesAdapter))
    rulesHelper.attachToRecyclerView(rules)

    ignoreRules.adapter = ignoreRulesAdapter
    ignoreRules.layoutManager = LinearLayoutManager(requireContext())
    ignoreRules.itemAnimator = DefaultItemAnimator()

    ignoreRulesHelper = ItemTouchHelper(DragSortItemTouchHelperCallback(ignoreRulesAdapter))
    ignoreRulesHelper.attachToRecyclerView(ignoreRules)

    newHighlightRule.setOnClickListener {
      startActivityForResult(
        HighlightRuleActivity.intent(requireContext(), ignore = false),
        REQUEST_CREATE_RULE
      )
    }

    newHighlightIgnoreRule.setOnClickListener {
      startActivityForResult(
        HighlightRuleActivity.intent(requireContext(), ignore = true),
        REQUEST_CREATE_IGNORE_RULE
      )
    }

    val highlightNickTypeAdapter = HighlightNickTypeAdapter(listOf(
      HighlightNickTypeItem(
        value = IHighlightRuleManager.HighlightNickType.AllNicks,
        name = R.string.settings_highlightlist_highlight_nick_all_nicks
      ),
      HighlightNickTypeItem(
        value = IHighlightRuleManager.HighlightNickType.CurrentNick,
        name = R.string.settings_highlightlist_highlight_nick_current_nick
      ),
      HighlightNickTypeItem(
        value = IHighlightRuleManager.HighlightNickType.NoNick,
        name = R.string.settings_highlightlist_highlight_nick_none
      )
    ))
    highlightNickType.adapter = highlightNickTypeAdapter

    viewModel.highlightRuleManager
      .filter(Optional<HighlightRuleManager>::isPresent)
      .map(Optional<HighlightRuleManager>::get)
      .toLiveData().observe(this, Observer {
        if (it != null) {
          if (this.ruleManager == null) {
            this.ruleManager = Pair(it, it.copy())
            this.ruleManager?.let { (_, data) ->
              rulesAdapter.list = data.highlightRuleList().filter { !it.isInverse }
              ignoreRulesAdapter.list = data.highlightRuleList().filter { it.isInverse }
              highlightNickType.setSelection(highlightNickTypeAdapter.indexOf(data.highlightNick())
                                             ?: 0)
              isCaseSensitive.isChecked = data.nicksCaseSensitive()
            }
          }
        }
      })

    viewModel.negotiatedFeatures.toLiveData().observe(this, Observer { (connected, features) ->
      featureContextCoreSideHighlights.setMode(
        if (!connected || features.hasFeature(ExtendedFeature.CoreSideHighlights)) WarningBarView.MODE_NONE
        else WarningBarView.MODE_ICON
      )
    })

    return view
  }

  private fun ruleClick(rule: HighlightRuleManager.HighlightRule) {
    startActivityForResult(
      HighlightRuleActivity.intent(requireContext(), rule),
      REQUEST_UPDATE_RULE
    )
  }

  private fun startRuleDrag(holder: HighlightRuleAdapter.HighlightRuleViewHolder) =
    rulesHelper.startDrag(holder)

  private fun ignoreRuleClick(rule: HighlightRuleManager.HighlightRule) {
    startActivityForResult(
      HighlightRuleActivity.intent(requireContext(), rule),
      REQUEST_UPDATE_IGNORE_RULE
    )
  }

  private fun startIgnoreRuleDrag(holder: HighlightRuleAdapter.HighlightRuleViewHolder) =
    ignoreRulesHelper.startDrag(holder)

  private fun nextId(): Int {
    val maxRuleId = rulesAdapter.list.maxBy { it.id }?.id
    val maxIgnoreRuleId = ignoreRulesAdapter.list.maxBy { it.id }?.id

    return when {
      maxRuleId != null && maxIgnoreRuleId != null -> maxOf(maxRuleId, maxIgnoreRuleId) + 1
      maxIgnoreRuleId != null                      -> maxIgnoreRuleId + 1
      maxRuleId != null                            -> maxRuleId + 1
      else                                         -> 0
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null) {
      when (requestCode) {
        REQUEST_UPDATE_RULE        -> {
          val oldRule = data.getSerializableExtra("old") as? HighlightRuleManager.HighlightRule
          val newRule = data.getSerializableExtra("new") as? HighlightRuleManager.HighlightRule

          if (oldRule != null && newRule != null) {
            rulesAdapter.replace(rulesAdapter.indexOf(oldRule.name), newRule)
          }
        }
        REQUEST_CREATE_RULE        -> {
          val newRule = data.getSerializableExtra("new") as? HighlightRuleManager.HighlightRule

          if (newRule != null) {
            rulesAdapter.add(newRule.copy(id = nextId()))
          }
        }
        REQUEST_UPDATE_IGNORE_RULE -> {
          val oldRule = data.getSerializableExtra("old") as? HighlightRuleManager.HighlightRule
          val newRule = data.getSerializableExtra("new") as? HighlightRuleManager.HighlightRule

          if (oldRule != null && newRule != null) {
            ignoreRulesAdapter.replace(ignoreRulesAdapter.indexOf(oldRule.name), newRule)
          }
        }
        REQUEST_CREATE_IGNORE_RULE -> {
          val newRule = data.getSerializableExtra("new") as? HighlightRuleManager.HighlightRule

          if (newRule != null) {
            ignoreRulesAdapter.add(newRule.copy(id = nextId()))
          }
        }
      }
    }
  }

  override fun onSave() = ruleManager?.let { (it, data) ->
    applyChanges(data)
    it.requestUpdate(data.toVariantMap())
    true
  } ?: false

  override fun hasChanged() = ruleManager?.let { (it, data) ->
    applyChanges(data)
    !data.isEqual(it)
  } == true

  private fun applyChanges(data: HighlightRuleManager) {
    data.setHighlightNick(highlightNickType.selectedItemId.toInt())
    data.setNicksCaseSensitive(isCaseSensitive.isChecked)
    data.setHighlightRuleList(rulesAdapter.list + ignoreRulesAdapter.list)
  }

  companion object {
    private const val REQUEST_UPDATE_RULE = 1
    private const val REQUEST_CREATE_RULE = 2
    private const val REQUEST_UPDATE_IGNORE_RULE = 3
    private const val REQUEST_CREATE_IGNORE_RULE = 4
  }
}
