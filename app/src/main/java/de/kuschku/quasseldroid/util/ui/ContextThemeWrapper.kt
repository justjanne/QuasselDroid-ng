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

package de.kuschku.quasseldroid.util.ui

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.StyleRes
import de.kuschku.quasseldroid.R

/**
 * A ContextWrapper that allows you to modify the theme from what is in the
 * wrapped context.
 *
 * @hide
 */
open class ContextThemeWrapper : ContextWrapper {
  var themeResId: Int = 0
    private set
  private var mTheme: Resources.Theme? = null
  private var mInflater: LayoutInflater? = null
  /**
   * Used by ActivityThread to apply the overridden configuration to onConfigurationChange
   * callbacks.
   * @hide
   */
  var overrideConfiguration: Configuration? = null
    private set
  private var mResources: Resources? = null

  /**
   * Creates a new context wrapper with no theme and no base context.
   *
   *
   * **Note:** A base context **must** be attached
   * using [.attachBaseContext] before calling any other
   * method on the newly constructed context wrapper.
   */
  constructor() : super(null)

  /**
   * Creates a new context wrapper with the specified theme.
   *
   *
   * The specified theme will be applied on top of the base context's theme.
   * Any attributes not explicitly defined in the theme identified by
   * <var>themeResId</var> will retain their original values.
   *
   * @param base the base context
   * @param themeResId the resource ID of the theme to be applied on top of
   * the base context's theme
   */
  constructor(base: Context, @StyleRes themeResId: Int) : super(base) {
    this.themeResId = themeResId
  }

  /**
   * Creates a new context wrapper with the specified theme.
   *
   *
   * Unlike [.ContextThemeWrapper], the theme passed to
   * this constructor will completely replace the base context's theme.
   *
   * @param base the base context
   * @param theme the theme against which resources should be inflated
   */
  constructor(base: Context, theme: Resources.Theme?) : super(base) {
    mTheme = theme
  }

  /**
   * Call to set an "override configuration" on this context -- this is
   * a configuration that replies one or more values of the standard
   * configuration that is applied to the context.  See
   * [Context.createConfigurationContext] for more
   * information.
   *
   *
   * This method can only be called once, and must be called before any
   * calls to [.getResources] or [.getAssets] are made.
   */
  fun applyOverrideConfiguration(overrideConfiguration: Configuration) {
    if (mResources != null) {
      throw IllegalStateException(
        "getResources() or getAssets() has already been called"
      )
    }
    if (this.overrideConfiguration != null) {
      throw IllegalStateException("Override configuration has already been set")
    }
    this.overrideConfiguration = Configuration(overrideConfiguration)
  }

  override fun getResources(): Resources? {
    return resourcesInternal
  }

  private val resourcesInternal: Resources?
    get() {
      if (mResources == null) {
        if (overrideConfiguration == null) {
          mResources = super.getResources()
        } else if (Build.VERSION.SDK_INT >= 17) {
          val resContext = createConfigurationContext(overrideConfiguration)
          mResources = resContext.resources
        }
      }
      return mResources
    }

  override fun setTheme(resid: Int) {
    if (themeResId != resid) {
      themeResId = resid
      initializeTheme()
    }
  }

  override fun getTheme(): Resources.Theme? {
    if (mTheme != null) {
      return mTheme
    }

    if (themeResId == 0) {
      themeResId = R.style.Theme_AppCompat_Light
    }
    initializeTheme()

    return mTheme
  }

  override fun getSystemService(name: String): Any? {
    if (Context.LAYOUT_INFLATER_SERVICE == name) {
      if (mInflater == null) {
        mInflater = LayoutInflater.from(baseContext).cloneInContext(this)
      }
      return mInflater
    }
    return baseContext.getSystemService(name)
  }

  /**
   * Called by [.setTheme] and [.getTheme] to apply a theme
   * resource to the current Theme object.  Can override to change the
   * default (simple) behavior.  This method will not be called in multiple
   * threads simultaneously.
   *
   * @param theme The Theme object being modified.
   * @param resid The theme style resource being applied to <var>theme</var>.
   * @param first Set to true if this is the first time a style is being
   * applied to <var>theme</var>.
   */
  protected fun onApplyThemeResource(theme: Resources.Theme, resid: Int, first: Boolean) {
    theme.applyStyle(resid, true)
  }

  private fun initializeTheme() {
    val first = mTheme == null
    if (first) {
      mTheme = resources!!.newTheme()
      val theme = baseContext.theme
      if (theme != null) {
        mTheme!!.setTo(theme)
      }
    }
    onApplyThemeResource(mTheme!!, themeResId, first)
  }

  override fun getAssets(): AssetManager {
    // Ensure we're returning assets with the correct configuration.
    return resources!!.assets
  }
}
