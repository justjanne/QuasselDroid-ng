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

package de.kuschku.quasseldroid.ui.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.appbar.CollapsingToolbarLayout
import dagger.android.support.DaggerFragment
import de.kuschku.quasseldroid.R

abstract class SlideFragment : DaggerFragment() {
  @get:StringRes
  protected abstract val title: Int
  @get:StringRes
  protected abstract val description: Int

  protected abstract fun isValid(): Boolean

  val valid = object : MutableLiveData<Boolean>() {
    override fun observe(owner: LifecycleOwner, observer: Observer<in Boolean>) {
      super.observe(owner, observer)
      observer.onChanged(value)
    }

    override fun observeForever(observer: Observer<in Boolean>) {
      super.observeForever(observer)
      observer.onChanged(value)
    }
  }

  protected fun updateValidity() {
    valid.value = isValid()
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_slide, container, false)
    val viewGroup = view.findViewById<View>(R.id.content_host) as ViewGroup
    viewGroup.addView(onCreateContent(inflater, viewGroup, savedInstanceState))

    view.findViewById<TextView>(R.id.title)?.setText(title)
    view.findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)?.title =
      resources.getString(title)
    view.findViewById<TextView>(R.id.description).setText(description)

    initData?.let(this::setData)
    savedInstanceState?.let(this::setData)
    updateValidity()

    return view
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    getData(outState)
  }

  override fun onViewStateRestored(savedInstanceState: Bundle?) {
    super.onViewStateRestored(savedInstanceState)
    updateValidity()
  }

  abstract fun setData(data: Bundle)
  abstract fun getData(data: Bundle)
  var initData: Bundle? = null

  fun requestFocus() {
    this.view?.requestFocus()
  }

  private var hasChangedListener: ((SlideFragment) -> Unit)? = null
  fun setHasChangedListener(listener: ((SlideFragment) -> Unit)?) {
    hasChangedListener = listener
  }

  protected fun hasChanged() {
    hasChangedListener?.invoke(this)
  }

  protected abstract fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                                         savedInstanceState: Bundle?): View
}
