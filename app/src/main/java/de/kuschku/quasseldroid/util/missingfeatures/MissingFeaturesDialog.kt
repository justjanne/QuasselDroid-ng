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

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.ui.BetterLinkMovementMethod
import java.io.Serializable

class MissingFeaturesDialog : DialogFragment() {
  val builder: Builder?
    get() = arguments?.getSerializable("builder") as? Builder

  @BindView(R.id.list)
  lateinit var list: RecyclerView

  @BindView(R.id.message)
  lateinit var message: TextView

  @SuppressLint("StringFormatInvalid")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = MaterialDialog.Builder(requireContext())
      .customView(R.layout.dialog_missing_features, true)
      .title(R.string.label_missing_features)
      .also {
        it.positiveText(if (builder?.readOnly == true) R.string.label_accept
                        else R.string.label_close)
        builder?.positiveListener?.let(it::onPositive)
      }
      .build()
    ButterKnife.bind(this, dialog.customView!!)
    val version = builder?.missingFeatures?.maxBy(MissingFeature::minimumVersion)?.minimumVersion
                  ?: QuasselVersion.VERSION_0_13
    message.text = Html.fromHtml(getString(R.string.info_missing_features, version.humanName))
    message.movementMethod = BetterLinkMovementMethod.newInstance()
    list.layoutManager = LinearLayoutManager(list.context)
    val adapter = MissingFeaturesAdapter()
    list.adapter = adapter
    list.itemAnimator = null
    adapter.submitList(builder?.missingFeatures.orEmpty())
    return dialog
  }

  override fun onDismiss(dialog: DialogInterface?) {
    super.onDismiss(dialog)
    builder?.dismissListener?.onDismiss(this.dialog)
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

  class Builder(private val fragmentManager: FragmentManager) : Serializable {
    constructor(context: FragmentActivity) : this(context.supportFragmentManager)

    var dismissListener: DialogInterface.OnDismissListener? = null
    var positiveListener: MaterialDialog.SingleButtonCallback? = null
    var missingFeatures: List<MissingFeature>? = null
    var readOnly: Boolean = false

    fun missingFeatures(missingFeatures: List<MissingFeature>): Builder {
      this.missingFeatures = missingFeatures
      return this
    }

    fun readOnly(readOnly: Boolean): Builder {
      this.readOnly = readOnly
      return this
    }

    fun dismissListener(dismissListener: DialogInterface.OnDismissListener): Builder {
      this.dismissListener = dismissListener
      return this
    }

    fun positiveListener(positiveListener: MaterialDialog.SingleButtonCallback): Builder {
      this.positiveListener = positiveListener
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
