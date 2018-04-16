package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import de.kuschku.quasseldroid.R

class AutoCompleteRecyclerView : RecyclerView {
  constructor(context: Context?) : super(context)
  constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
  constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
    context, attrs, defStyle
  )

  override fun onMeasure(widthSpec: Int, heightSpec: Int) {
    super.onMeasure(
      widthSpec,
      MeasureSpec.makeMeasureSpec(
        resources.getDimensionPixelSize(R.dimen.autocomplete_max_height),
        MeasureSpec.AT_MOST
      )
    )
  }
}
