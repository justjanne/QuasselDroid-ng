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
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class SlidePagerAdapter(private val fragmentManager: FragmentManager) :
  FragmentStatePagerAdapter(fragmentManager) {
  private val retainedFragments = SparseArray<SlideFragment>()

  val result = Bundle()
    get() {
      (0 until retainedFragments.size()).map(retainedFragments::valueAt).forEach {
        it.getData(field)
      }
      return field
    }

  var lastValidItem = -1
    set(value) {
      field = value
      notifyDataSetChanged()
    }
  private val list = mutableListOf<SlideFragment>()

  override fun getItem(position: Int): SlideFragment {
    return retainedFragments.get(position) ?: list[position]
  }

  override fun getCount() = minOf(list.size, lastValidItem + 2)
  val totalCount get() = list.size
  fun addFragment(fragment: SlideFragment) {
    list.add(fragment)
  }

  override fun instantiateItem(container: ViewGroup, position: Int): Any {
    val fragment = super.instantiateItem(container, position)
    storeNewFragment(position, fragment as SlideFragment)
    return fragment
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    retainedFragments.get(position)?.getData(result)
    retainedFragments.remove(position)
    super.destroyItem(container, position, `object`)
  }

  override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
    super.restoreState(state, loader)
    if (state != null) {
      val bundle = state as Bundle
      val keys = bundle.keySet()
      for (key in keys) {
        if (key.startsWith("f")) {
          val index = Integer.parseInt(key.substring(1))
          val f = fragmentManager.getFragment(bundle, key)
          if (f != null && f is SlideFragment) {
            storeNewFragment(index, f)
          }
        }
      }
    }
  }

  private fun storeNewFragment(index: Int, fragment: SlideFragment) {
    fragment.initData = result
    fragment.setHasChangedListener { hasChanged(index, fragment) }
    retainedFragments.put(index, fragment)
  }

  fun allChanged() {
    for (index in 0 until totalCount) {
      hasChanged(index, getItem(index))
    }
  }

  fun hasChanged(index: Int, fragment: SlideFragment) {
    fragment.getData(result)
    if (index > -1 && (index + 1) < totalCount) {
      getItem(index + 1).setData(result)
    }
  }
}
