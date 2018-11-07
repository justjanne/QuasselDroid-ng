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
package androidx.lifecycle

/**
 * Callback interface for listening to [LifecycleOwner] state changes.
 *
 *
 * If you use Java 8 language, **always** prefer it over annotations.
 */
interface DefaultLifecycleObserver : FullLifecycleObserverProxy {
  /**
   * Notifies that `ON_CREATE` event occurred.
   *
   *
   * This method will be called after the [LifecycleOwner]'s `onCreate`
   * method returns.
   *
   * @param owner the component, whose state was changed
   */
  override fun onCreate(owner: LifecycleOwner) = Unit

  /**
   * Notifies that `ON_START` event occurred.
   *
   *
   * This method will be called after the [LifecycleOwner]'s `onStart` method returns.
   *
   * @param owner the component, whose state was changed
   */
  override fun onStart(owner: LifecycleOwner) = Unit

  /**
   * Notifies that `ON_RESUME` event occurred.
   *
   *
   * This method will be called after the [LifecycleOwner]'s `onResume`
   * method returns.
   *
   * @param owner the component, whose state was changed
   */
  override fun onResume(owner: LifecycleOwner) = Unit

  /**
   * Notifies that `ON_PAUSE` event occurred.
   *
   *
   * This method will be called before the [LifecycleOwner]'s `onPause` method
   * is called.
   *
   * @param owner the component, whose state was changed
   */
  override fun onPause(owner: LifecycleOwner) = Unit

  /**
   * Notifies that `ON_STOP` event occurred.
   *
   *
   * This method will be called before the [LifecycleOwner]'s `onStop` method
   * is called.
   *
   * @param owner the component, whose state was changed
   */
  override fun onStop(owner: LifecycleOwner) = Unit

  /**
   * Notifies that `ON_DESTROY` event occurred.
   *
   *
   * This method will be called before the [LifecycleOwner]'s `onStop` method
   * is called.
   *
   * @param owner the component, whose state was changed
   */
  override fun onDestroy(owner: LifecycleOwner) = Unit
}
