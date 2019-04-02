/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.kuschku.ui.resources;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;

import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.content.res.AppCompatResources;

/**
 * Utility methods to resolve resources for components.
 */
public class MaterialResources {

  private MaterialResources() {
  }

  /**
   * Returns the {@link ColorStateList} from the given {@link TypedArray} attributes. The resource
   * can include themeable attributes, regardless of API level.
   */
  @Nullable
  public static ColorStateList getColorStateList(
    Context context, TypedArray attributes, @StyleableRes int index) {
    if (attributes.hasValue(index)) {
      int resourceId = attributes.getResourceId(index, 0);
      if (resourceId != 0) {
        ColorStateList value = AppCompatResources.getColorStateList(context, resourceId);
        if (value != null) {
          return value;
        }
      }
    }

    // Reading a single color with getColorStateList() on API 15 and below doesn't always correctly
    // read the value. Instead we'll first try to read the color directly here.

    return attributes.getColorStateList(index);
  }

  /**
   * Returns the @StyleableRes index that contains value in the attributes array. If both indices
   * contain values, the first given index takes precedence and is returned.
   */
  @StyleableRes
  static int getIndexWithValue(TypedArray attributes, @StyleableRes int a, @StyleableRes int b) {
    if (attributes.hasValue(a)) {
      return a;
    }
    return b;
  }
}
