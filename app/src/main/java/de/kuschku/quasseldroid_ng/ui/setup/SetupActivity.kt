package de.kuschku.quasseldroid_ng.ui.setup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.util.SparseArray
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.ui.setup.slides.SlideFragment
import de.kuschku.quasseldroid_ng.util.helper.stickySwitchMapNotNull

abstract class SetupActivity : AppCompatActivity() {
  @BindView(R.id.view_pager)
  lateinit var viewPager: ViewPager

  @BindView(R.id.next_button)
  lateinit var button: FloatingActionButton

  private lateinit var adapter: SlidePagerAdapter

  protected abstract val fragments: List<SlideFragment>

  private val currentPage = MutableLiveData<SlideFragment?>()
  private val isValid = currentPage.stickySwitchMapNotNull(false, SlideFragment::valid)

  private val pageChangeListener = object : ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(state: Int) {
      when (state) {
        ViewPager.SCROLL_STATE_SETTLING -> pageChanged()
      }
    }

    override fun onPageScrolled(position: Int, positionOffset: Float,
                                positionOffsetPixels: Int) = Unit

    override fun onPageSelected(position: Int) = Unit
  }

  private fun pageChanged() {
    currentPage.value = adapter.getItem(viewPager.currentItem)
    val drawable = if (viewPager.currentItem == adapter.totalCount - 1)
      R.drawable.ic_check
    else
      R.drawable.ic_arrow_right
    button.setImageResource(drawable)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.SetupTheme)
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_setup)
    ButterKnife.bind(this)

    adapter = SlidePagerAdapter(supportFragmentManager)
    fragments.forEach(adapter::addFragment)
    viewPager.adapter = adapter

    button.setOnClickListener {
      if (viewPager.currentItem == adapter.totalCount - 1)
        onDoneInternal()
      else
        viewPager.setCurrentItem(viewPager.currentItem + 1, true)
    }
    isValid.observe(this, Observer {
      if (it == true) {
        button.show()
        adapter.lastValidItem = viewPager.currentItem
      } else {
        button.hide()
        adapter.lastValidItem = viewPager.currentItem - 1
      }
    })
    viewPager.addOnPageChangeListener(pageChangeListener)
    pageChanged()
  }

  private fun onDoneInternal() {
    onDone(adapter.result)
  }

  abstract fun onDone(data: Bundle)

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putInt("currentItem", viewPager.currentItem)
    outState.putInt("lastValidItem", adapter.lastValidItem)
    outState.putBundle("result", adapter.result)
    super.onSaveInstanceState(outState)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
    super.onRestoreInstanceState(savedInstanceState)
    if (savedInstanceState != null) {
      if (savedInstanceState.containsKey("result"))
        adapter.result.putAll(savedInstanceState.getBundle("result"))
      if (savedInstanceState.containsKey("lastValidItem"))
        adapter.lastValidItem = savedInstanceState.getInt("lastValidItem")
      if (savedInstanceState.containsKey("currentItem"))
        viewPager.currentItem = savedInstanceState.getInt("currentItem")
      currentPage.value = adapter.getItem(viewPager.currentItem)
    }
    pageChanged()
  }

  companion object {
    private class SlidePagerAdapter(private val fragmentManager: FragmentManager) :
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

      override fun getCount() = Math.min(list.size, lastValidItem + 2)
      val totalCount get() = list.size
      fun addFragment(fragment: SlideFragment) {
        list.add(fragment)
      }

      override fun instantiateItem(container: ViewGroup?, position: Int): Any {
        val fragment = super.instantiateItem(container, position)
        retainedFragments.put(position, fragment as SlideFragment)
        return fragment
      }

      override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any?) {
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
                retainedFragments.put(index, f)
              }
            }
          }
        }
      }
    }
  }
}
