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

import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Dimension

class NickCountDrawable(
  count: Int,
  @Dimension private val size: Int,
  @ColorInt textColor: Int
) : Drawable() {
  private val paint = Paint().apply {
    color = textColor
    isAntiAlias = true
    style = Paint.Style.FILL
    typeface = Typeface.DEFAULT_BOLD
    textAlign = Paint.Align.CENTER
  }

  private val formattedText: String = formatCount(count)

  private fun formatCount(count: Int) = when {
    count >= 10_000 -> String.format("%.00fK", count / 1_000f)
    count >= 1_000  -> String.format("%.01fK", count / 1_000f)
    else            -> "$count"
  }

  private val showText: Boolean = count > 0

  private val icon = Path().apply {
    moveTo(16f, 13f)
    cubicTo(15.71f, 13f, 15.38f, 13f, 15.03f, 13.05f)
    cubicTo(16.19f, 13.89f, 17f, 15f, 17f, 16.5f)
    lineTo(17f, 19f)
    lineTo(23f, 19f)
    lineTo(23f, 16.5f)
    cubicTo(23f, 14.17f, 18.33f, 13f, 16f, 13f)
    moveTo(8f, 13f)
    cubicTo(5.67f, 13f, 1f, 14.17f, 1f, 16.5f)
    lineTo(1f, 19f)
    lineTo(15f, 19f)
    lineTo(15f, 16.5f)
    cubicTo(15f, 14.17f, 10.33f, 13f, 8f, 13f)
    addCircle(8f, 8f, 3f, Path.Direction.CCW)
    addCircle(16f, 8f, 3f, Path.Direction.CCW)
    close()
  }

  override fun getIntrinsicHeight() = size
  override fun getIntrinsicWidth() = size

  private val tmpPath = Path()

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  override fun draw(canvas: Canvas) {
    if (bounds.width() <= 0 || bounds.height() <= 0) {
      // Nothing to draw
      return
    }

    val smallestDimension = Math.min(bounds.width(), bounds.height())

    tmpPath.set(icon)
    tmpPath.transform(Matrix().apply {
      preScale(smallestDimension / 24f, smallestDimension / 24f, 0f, 0f)
      if (showText) {
        preTranslate(0f, -8f)
      }
    })

    paint.textSize = smallestDimension * 0.6f

    canvas.drawPath(tmpPath, paint)
    if (showText) {
      canvas.drawText(formattedText, bounds.width() / 2f,
                      bounds.height() * 0.85f - ((paint.descent() + paint.ascent()) / 2f), paint)
    }
  }
}
