package de.kuschku.quasseldroid.util.ui

import android.content.res.Resources
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ThemedSpinnerAdapter
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class RecyclerSpinnerAdapter<VH : RecyclerView.ViewHolder> : BaseAdapter(),
                                                                      ThemedSpinnerAdapter {
  private var dropDownViewTheme: Resources.Theme? = null
  override fun getDropDownViewTheme() = dropDownViewTheme
  override fun setDropDownViewTheme(theme: Resources.Theme?) {
    dropDownViewTheme = theme
  }

  override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
    val tag = convertView?.tag
    val holder: VH = tag as? VH ?: onCreateViewHolder(parent, true)
    holder.itemView.tag = holder
    onBindViewHolder(holder, position)
    return holder.itemView
  }

  override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
    val tag = convertView?.tag
    val holder = tag as? VH ?: onCreateViewHolder(parent, false)
    holder.itemView.tag = holder
    onBindViewHolder(holder, position)
    return holder.itemView
  }

  protected abstract fun onBindViewHolder(holder: VH, position: Int)
  protected abstract fun onCreateViewHolder(parent: ViewGroup, dropDown: Boolean): VH
}
