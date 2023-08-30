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

package de.kuschku.quasseldroid.util.ui.drawable

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.util.helper.styledAttributes

class DrawerToggleActivityDrawable(context: Context, @ColorRes colorAttribute: Int) : Drawable() {
  private val size = context.resources.getDimensionPixelSize(R.dimen.drawer_toggle_size)
  private val thickness = context.resources.getDimension(R.dimen.drawer_toggle_thickness)

  private val overlayPaint = Paint().apply {
    color = context.theme.styledAttributes(colorAttribute) {
      getColor(0, 0)
    }
    style = Paint.Style.FILL
    isAntiAlias = true
  }

  private val togglePaint = Paint().apply {
    color = context.theme.styledAttributes(androidx.appcompat.R.attr.colorControlNormal) {
      getColor(0, 0)
    }
    style = Paint.Style.STROKE
    strokeJoin = Paint.Join.MITER
    strokeCap = Paint.Cap.BUTT
    isAntiAlias = true
    strokeWidth = thickness
  }


  private val overlayPath = Path().apply {
    arcTo(RectF(22f, 22f, 24f, 24f), 0f, 90f)
    arcTo(RectF(16f, 22f, 18f, 24f), 90f, 90f)
    arcTo(RectF(16f, 16f, 18f, 18f), 180f, 90f)
    arcTo(RectF(22f, 16f, 24f, 18f), 270f, 90f)
    close()
  }

  private val togglePath = Path().apply {
    // top bar
    moveTo(3f, 7f)
    rLineTo(18f, 0f)

    // draw middle bar
    moveTo(3f, 12f)
    rLineTo(18f, 0f)

    // bottom bar
    moveTo(3f, 17f)
    rLineTo(18f, 0f)
  }

  override fun getIntrinsicHeight() = size
  override fun getIntrinsicWidth() = size

  // Not supported
  override fun setAlpha(alpha: Int) = Unit

  override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

  // Not supported
  override fun setColorFilter(colorFilter: ColorFilter?) = Unit

  private val transformationMatrix = Matrix()
  private val transformedOverlayPath = Path()
  private val transformedTogglePath = Path()

  private fun transformPaths() {
    transformationMatrix.reset()
    transformationMatrix.apply {
      val smallestDimension = Math.min(bounds.width(), bounds.height())
      preScale(smallestDimension / 24f, smallestDimension / 24f, 0f, 0f)
    }

    transformedOverlayPath.set(overlayPath)
    transformedOverlayPath.transform(transformationMatrix)
    transformedTogglePath.set(togglePath)
    transformedTogglePath.transform(transformationMatrix)
  }

  override fun onBoundsChange(bounds: Rect) {
    transformPaths()
  }

  override fun draw(canvas: Canvas) {
    if (bounds.width() <= 0 || bounds.height() <= 0) {
      // Nothing to draw
      return
    }

    canvas.drawPath(transformedTogglePath, togglePaint)
    canvas.drawPath(transformedOverlayPath, overlayPaint)
  }
}
