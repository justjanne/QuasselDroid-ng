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

package de.kuschku.quasseldroid.util.compatibility;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ServiceLifecycleDispatcher;

/**
 * A Service that is also a {@link LifecycleOwner}.
 */
@SuppressLint("Registered")
public class FixedLifecycleService extends Service implements LifecycleOwner {

  private final ServiceLifecycleDispatcher mDispatcher = new ServiceLifecycleDispatcher(this);

  @CallSuper
  @Override
  public void onCreate() {
    mDispatcher.onServicePreSuperOnCreate();
    super.onCreate();
  }

  @CallSuper
  @Nullable
  @Override
  public IBinder onBind(@Nullable Intent intent) {
    mDispatcher.onServicePreSuperOnBind();
    return null;
  }

  @SuppressWarnings("deprecation")
  @CallSuper
  @Override
  public void onStart(@Nullable Intent intent, int startId) {
    mDispatcher.onServicePreSuperOnStart();
    super.onStart(intent, startId);
  }

  // this method is added only to annotate it with @CallSuper.
  // In usual service super.onStartCommand is no-op, but in LifecycleService
  // it results in mDispatcher.onServicePreSuperOnStart() call, because
  // super.onStartCommand calls onStart().
  @CallSuper
  @Override
  public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
    return super.onStartCommand(intent, flags, startId);
  }

  @CallSuper
  @Override
  public void onDestroy() {
    mDispatcher.onServicePreSuperOnDestroy();
    super.onDestroy();
  }

  @Override
  @NonNull
  public Lifecycle getLifecycle() {
    return mDispatcher.getLifecycle();
  }
}
