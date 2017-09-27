package de.kuschku.quasseldroid_ng.ui.setup.slides

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import de.kuschku.quasseldroid_ng.R

abstract class SlideFragment : Fragment() {
  @get:StringRes
  protected abstract val title: Int
  @get:StringRes
  protected abstract val descripion: Int

  protected abstract fun isValid(): Boolean

  val valid = object : MutableLiveData<Boolean>() {
    override fun observe(owner: LifecycleOwner?, observer: Observer<Boolean>?) {
      super.observe(owner, observer)
      observer?.onChanged(value)
    }

    override fun observeForever(observer: Observer<Boolean>?) {
      super.observeForever(observer)
      observer?.onChanged(value)
    }
  }

  protected fun updateValidity() {
    val valid1 = isValid()
    println("Updating validity: ${this::class.java.simpleName}@${hashCode()} $valid1")
    valid.value = valid1
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
    val view = inflater.inflate(R.layout.setup_slide, container, false)
    val viewGroup = view.findViewById<View>(R.id.content_host) as ViewGroup
    viewGroup.addView(onCreateContent(inflater, viewGroup, savedInstanceState))

    view.findViewById<TextView>(R.id.title).setText(title)
    view.findViewById<TextView>(R.id.description).setText(descripion)

    if (savedInstanceState != null)
      setData(savedInstanceState)
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

  protected abstract fun onCreateContent(inflater: LayoutInflater, container: ViewGroup?,
                                         savedInstanceState: Bundle?): View
}
