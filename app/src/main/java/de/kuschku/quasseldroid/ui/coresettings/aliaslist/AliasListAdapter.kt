package de.kuschku.quasseldroid.ui.coresettings.aliaslist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.quassel.syncables.interfaces.IAliasManager
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.styledAttributes
import de.kuschku.quasseldroid.util.irc.format.IrcFormatDeserializer
import java.util.*
import javax.inject.Inject

class AliasListAdapter @Inject constructor(
  private val formatDeserializer: IrcFormatDeserializer
) : RecyclerView.Adapter<AliasListAdapter.AliasItemViewHolder>() {
  private var clickListener: ((IAliasManager.Alias) -> Unit)? = null
  private var dragListener: ((AliasItemViewHolder) -> Unit)? = null

  fun setOnClickListener(listener: ((IAliasManager.Alias) -> Unit)?) {
    clickListener = listener
  }

  fun setOnDragListener(listener: ((AliasItemViewHolder) -> Unit)?) {
    dragListener = listener
  }

  private val data = mutableListOf<IAliasManager.Alias>()
  var list: List<IAliasManager.Alias>
    get() = data
    set(value) {
      val length = data.size
      data.clear()
      notifyItemRangeRemoved(0, length)
      data.addAll(value)
      notifyItemRangeInserted(0, list.size)
    }

  fun add(item: IAliasManager.Alias) {
    val index = data.size
    data.add(item)
    notifyItemInserted(index)
  }

  fun replace(index: Int, item: IAliasManager.Alias) {
    data[index] = item
    notifyItemChanged(index)
  }

  fun indexOf(name: String) = data.map(IAliasManager.Alias::name).indexOf(name)

  fun remove(index: Int) {
    data.removeAt(index)
    notifyItemRemoved(index)
  }

  fun move(from: Int, to: Int) {
    Collections.swap(data, from, to)
    notifyItemMoved(from, to)
  }

  private lateinit var mircColors: IntArray

  fun updateColors(context: Context) {
    mircColors = context.theme.styledAttributes(
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
  }

  override fun getItemCount() = data.size

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AliasItemViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.settings_aliaslist_item, parent, false),
    formatDeserializer,
    mircColors,
    clickListener,
    dragListener
  )

  override fun onBindViewHolder(holder: AliasItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  class AliasItemViewHolder(
    itemView: View,
    private val formatDeserializer: IrcFormatDeserializer,
    private val mircColors: IntArray,
    clickListener: ((IAliasManager.Alias) -> Unit)?,
    dragListener: ((AliasItemViewHolder) -> Unit)?
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.name)
    lateinit var name: TextView

    @BindView(R.id.expansion)
    lateinit var expansion: TextView

    @BindView(R.id.handle)
    lateinit var handle: View

    private var item: IAliasManager.Alias? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        item?.let {
          clickListener?.invoke(it)
        }
      }
      handle.setOnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
          dragListener?.invoke(this)
        }
        false
      }
    }

    fun bind(item: IAliasManager.Alias) {
      this.item = item
      name.text = item.name
      expansion.text = formatDeserializer.formatString(mircColors, item.expansion, true)
    }
  }
}
