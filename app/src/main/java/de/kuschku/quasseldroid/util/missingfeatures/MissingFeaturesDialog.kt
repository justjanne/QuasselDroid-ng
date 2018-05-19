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

package de.kuschku.quasseldroid.util.missingfeatures

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R
import java.io.Serializable

class MissingFeaturesDialog : DialogFragment() {
  val builder: Builder?
    get() = arguments?.getSerializable("builder") as? Builder

  @BindView(R.id.list)
  lateinit var list: RecyclerView

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = MaterialDialog.Builder(requireContext())
      .customView(R.layout.dialog_missing_features, true)
      .title(R.string.label_missing_features)
      .positiveText(R.string.label_accept)
      .build()
    ButterKnife.bind(this, dialog.customView!!)
    list.layoutManager = LinearLayoutManager(list.context)
    val adapter = MissingFeaturesAdapter()
    list.adapter = adapter
    list.itemAnimator = null
    adapter.submitList(builder?.missingFeatures.orEmpty())
    return dialog
  }

  override fun onDismiss(dialog: DialogInterface?) {
    super.onDismiss(dialog)
    builder?.dismissListener?.onDismiss(this)
  }

  fun show(context: FragmentActivity) = show(context.supportFragmentManager)
  fun show(context: FragmentManager) {
    dismissIfNecessary(context)
    show(context, TAG)
  }

  private fun dismissIfNecessary(fragmentManager: FragmentManager) {
    fragmentManager.findFragmentByTag(tag)?.let { frag ->
      (frag as? DialogFragment)?.dismiss()
      fragmentManager.beginTransaction().remove(frag).commit()
    }
  }

  @FunctionalInterface
  interface OnDismissListener {
    fun onDismiss(dialog: MissingFeaturesDialog)
  }

  class Builder(private val fragmentManager: FragmentManager) : Serializable {
    constructor(context: FragmentActivity) : this(context.supportFragmentManager)

    var dismissListener: OnDismissListener? = null
    var missingFeatures: List<MissingFeature>? = null

    fun missingFeatures(missingFeatures: List<MissingFeature>): Builder {
      this.missingFeatures = missingFeatures
      return this
    }

    fun dismissListener(dismissListener: OnDismissListener): Builder {
      this.dismissListener = dismissListener
      return this
    }

    fun build() = MissingFeaturesDialog().apply {
      arguments = Bundle().apply {
        putSerializable("builder", this@Builder)
      }
    }

    fun show() = build().show(fragmentManager)
  }

  companion object {
    const val TAG = "[MISSING_FEATURES]"
  }
}
