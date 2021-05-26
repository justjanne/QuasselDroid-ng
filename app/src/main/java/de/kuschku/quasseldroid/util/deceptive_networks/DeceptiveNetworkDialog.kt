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

package de.kuschku.quasseldroid.util.deceptive_networks

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import butterknife.BindView
import butterknife.ButterKnife
import com.afollestad.materialdialogs.MaterialDialog
import de.kuschku.quasseldroid.R

class DeceptiveNetworkDialog : DialogFragment() {
  private var builder: Builder? = null

  @BindView(R.id.message)
  lateinit var message: TextView

  @SuppressLint("StringFormatInvalid")
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val dialog = MaterialDialog.Builder(requireContext())
      .customView(R.layout.dialog_deceptive_network, true)
      .title(R.string.deceptive_network)
      .build()
    ButterKnife.bind(this, dialog.customView!!)
    builder?.message?.let {
      message.text = Html.fromHtml(
        resources.openRawResource(it).bufferedReader(Charsets.UTF_8).readText()
      )
    }
    return dialog
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

  class Builder(private val fragmentManager: FragmentManager) {
    constructor(context: FragmentActivity) : this(context.supportFragmentManager)

    @RawRes
    var message: Int? = null

    fun message(@RawRes message: Int?): Builder {
      this.message = message
      return this
    }

    fun build() = DeceptiveNetworkDialog().apply {
      builder = this@Builder
    }

    fun show() = build().show(fragmentManager)
  }

  companion object {
    const val TAG = "[DECEPTIVE_NETWORK]"
  }
}
