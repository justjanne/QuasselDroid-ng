/*
 * Copyright (C) 2018 The Android Open Source Project
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
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.Nullable;

/**
 * Utility methods to work with attributes.
 */
public class MaterialAttributes {
  /**
   * Returns the {@link TypedValue} for the provided {@code attributeResId}.
   */
  @Nullable
  public static TypedValue resolveAttribute(Context context, @AttrRes int attributeResId) {
    TypedValue typedValue = new TypedValue();
    if (context.getTheme().resolveAttribute(attributeResId, typedValue, true)) {
      return typedValue;
    }
    return null;
  }
}
