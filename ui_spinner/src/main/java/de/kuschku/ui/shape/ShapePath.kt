/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.ui.shape

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

/**
 * Represents the descriptive path of a shape. Path segments are stored in sequence so that
 * transformations can be applied to them when the [android.graphics.Path] is produced by the
 * [MaterialShapeDrawable].
 */
class ShapePath {
  private val operations = mutableListOf<PathOperation>()
  var startX = 0f
  var startY = 0f
  var endX = 0f
  var endY = 0f

  constructor() {
    reset(0f, 0f)
  }

  constructor(startX: Float, startY: Float) {
    reset(startX, startY)
  }

  fun reset(startX: Float, startY: Float) {
    this.startX = startX
    this.startY = startY
    this.endX = startX
    this.endY = startY
    this.operations.clear()
  }

  /**
   * Add a line to the ShapePath.
   *
   * @param x the x to which the line should be drawn.
   * @param y the y to which the line should be drawn.
   */
  fun lineTo(x: Float, y: Float) {
    operations.add(PathLineOperation(
      x = x,
      y = y
    ))

    endX = x
    endY = y
  }

  /**
   * Add a quad to the ShapePath.
   *
   * @param controlX the control point x of the arc.
   * @param controlY the control point y of the arc.
   * @param toX      the end x of the arc.
   * @param toY      the end y of the arc.
   */
  fun quadToPoint(controlX: Float, controlY: Float, toX: Float, toY: Float) {
    operations.add(PathQuadOperation(
      controlX = controlX,
      controlY = controlY,
      endX = toX,
      endY = toY
    ))

    endX = toX
    endY = toY
  }

  /**
   * Add an arc to the ShapePath.
   *
   * @param left       the X coordinate of the left side of the rectangle containing the arc oval.
   * @param top        the Y coordinate of the top of the rectangle containing the arc oval.
   * @param right      the X coordinate of the right side of the rectangle containing the arc oval.
   * @param bottom     the Y coordinate of the bottom of the rectangle containing the arc oval.
   * @param startAngle start angle of the arc.
   * @param sweepAngle sweep angle of the arc.
   */
  fun addArc(left: Float, top: Float, right: Float, bottom: Float,
             startAngle: Float, sweepAngle: Float) {
    operations.add(PathArcOperation(
      left = left,
      top = top,
      right = right,
      bottom = bottom,
      startAngle = startAngle,
      sweepAngle = sweepAngle
    ))

    endX = (left + right) * 0.5f + (right - left) / 2 * cos(Math.toRadians((startAngle + sweepAngle).toDouble())).toFloat()
    endY = (top + bottom) * 0.5f + (bottom - top) / 2 * sin(Math.toRadians((startAngle + sweepAngle).toDouble())).toFloat()
  }

  /**
   * Apply the ShapePath sequence to a [android.graphics.Path] under a matrix transform.
   *
   * @param transform the matrix transform under which this ShapePath is applied
   * @param path      the path to which this ShapePath is applied
   */
  fun applyToPath(transform: Matrix, path: Path) {
    for (operation in operations) {
      operation.applyToPath(transform, path)
    }
  }

  /**
   * Interface for a path operation to be appended to the operations list.
   */
  abstract class PathOperation {
    val matrix = Matrix()

    abstract fun applyToPath(transform: Matrix, path: Path)
  }

  /**
   * Straight line operation.
   */
  class PathLineOperation(
    val x: Float = 0f,
    val y: Float = 0f
  ) : PathOperation() {
    override fun applyToPath(transform: Matrix, path: Path) {
      val inverse = matrix
      transform.invert(inverse)
      path.transform(inverse)
      path.lineTo(x, y)
      path.transform(transform)
    }
  }

  /**
   * Path quad operation.
   */
  class PathQuadOperation(
    val controlX: Float = 0f,
    val controlY: Float = 0f,
    val endX: Float = 0f,
    val endY: Float = 0f
  ) : PathOperation() {
    override fun applyToPath(transform: Matrix, path: Path) {
      val inverse = matrix
      transform.invert(inverse)
      path.transform(inverse)
      path.quadTo(controlX, controlY, endX, endY)
      path.transform(transform)
    }
  }

  /**
   * Path arc operation.
   */
  class PathArcOperation(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
    val startAngle: Float = 0f,
    val sweepAngle: Float = 0f
  ) : PathOperation() {
    override fun applyToPath(transform: Matrix, path: Path) {
      val inverse = matrix
      transform.invert(inverse)
      path.transform(inverse)
      rectF.set(left, top, right, bottom)
      path.arcTo(rectF, startAngle, sweepAngle, false)
      path.transform(transform)
    }

    companion object {
      private val rectF = RectF()
    }
  }
}
