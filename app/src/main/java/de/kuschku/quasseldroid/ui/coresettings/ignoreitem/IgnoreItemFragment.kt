package de.kuschku.quasseldroid.ui.coresettings.ignoreitem

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.IgnoreListManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.coresettings.SettingsFragment
import de.kuschku.quasseldroid.util.ui.AnimationHelper

class IgnoreItemFragment : SettingsFragment(), SettingsFragment.Savable,
                           SettingsFragment.Changeable {
  @BindView(R.id.enabled)
  lateinit var enabled: SwitchCompat

  @BindView(R.id.ignorerule)
  lateinit var ignoreRule: EditText

  @BindView(R.id.isregex)
  lateinit var isRegEx: SwitchCompat

  @BindView(R.id.type)
  lateinit var type: Spinner

  @BindView(R.id.strictness)
  lateinit var strictness: Spinner

  @BindView(R.id.scope)
  lateinit var scope: Spinner

  @BindView(R.id.scoperule)
  lateinit var scopeRule: EditText

  @BindView(R.id.scopegroup)
  lateinit var scopegroup: ViewGroup

  private var item: IgnoreListManager.IgnoreListItem? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.settings_ignoreitem, container, false)
    ButterKnife.bind(this, view)

    (arguments?.getSerializable("item") as? IgnoreListManager.IgnoreListItem)?.let {
      item = it
    }

    val typeAdapter = IgnoreTypeAdapter(listOf(
      IgnoreTypeItem(
        value = IgnoreListManager.IgnoreType.SenderIgnore,
        name = R.string.settings_ignoreitem_type_sender
      ),
      IgnoreTypeItem(
        value = IgnoreListManager.IgnoreType.MessageIgnore,
        name = R.string.settings_ignoreitem_type_message
      ),
      IgnoreTypeItem(
        value = IgnoreListManager.IgnoreType.CtcpIgnore,
        name = R.string.settings_ignoreitem_type_ctcp
      )
    ))
    type.adapter = typeAdapter

    val strictnessAdapter = StrictnessTypeAdapter(listOf(
      StrictnessTypeItem(
        value = IgnoreListManager.StrictnessType.SoftStrictness,
        name = R.string.settings_ignoreitem_strictness_soft
      ),
      StrictnessTypeItem(
        value = IgnoreListManager.StrictnessType.HardStrictness,
        name = R.string.settings_ignoreitem_strictness_hard
      )
    ))
    strictness.adapter = strictnessAdapter

    val scopeAdapter = ScopeTypeAdapter(listOf(
      ScopeTypeItem(
        value = IgnoreListManager.ScopeType.GlobalScope,
        name = R.string.settings_ignoreitem_scope_global
      ),
      ScopeTypeItem(
        value = IgnoreListManager.ScopeType.NetworkScope,
        name = R.string.settings_ignoreitem_scope_network
      ),
      ScopeTypeItem(
        value = IgnoreListManager.ScopeType.ChannelScope,
        name = R.string.settings_ignoreitem_scope_channel
      )
    ))
    scope.adapter = scopeAdapter

    item?.let { data ->
      enabled.isChecked = data.isActive
      ignoreRule.setText(data.ignoreRule)
      isRegEx.isChecked = data.isRegEx
      type.setSelection(typeAdapter.indexOf(data.type) ?: 0)
      strictness.setSelection(strictnessAdapter.indexOf(data.strictness) ?: 0)
      scope.setSelection(scopeAdapter.indexOf(data.scope) ?: 0)
      scopeRule.setText(data.scopeRule)
    }

    scope.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {
        AnimationHelper.collapse(scopegroup)
      }

      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (id.toInt() == IgnoreListManager.ScopeType.GlobalScope.value) {
          AnimationHelper.collapse(scopegroup)
        } else {
          AnimationHelper.expand(scopegroup)
        }
      }
    }

    return view
  }

  override fun onSave() = item.let { data ->
    val intent = Intent()
    intent.putExtra("old", data)
    val new = IgnoreListManager.IgnoreListItem(
      isActive = enabled.isChecked,
      ignoreRule = ignoreRule.text.toString(),
      isRegEx = isRegEx.isChecked,
      type = type.selectedItemId.toInt(),
      strictness = strictness.selectedItemId.toInt(),
      scope = scope.selectedItemId.toInt(),
      scopeRule = scopeRule.text.toString()
    )
    intent.putExtra("new", new)
    requireActivity().setResult(Activity.RESULT_OK, intent)
    true
  }

  override fun hasChanged() = item != IgnoreListManager.IgnoreListItem(
    isActive = enabled.isChecked,
    ignoreRule = ignoreRule.text.toString(),
    isRegEx = isRegEx.isChecked,
    type = type.selectedItemId.toInt(),
    strictness = strictness.selectedItemId.toInt(),
    scope = scope.selectedItemId.toInt(),
    scopeRule = scopeRule.text.toString()
  )
}