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

package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import de.kuschku.quasseldroid.persistence.AccountDatabase
import de.kuschku.quasseldroid.persistence.QuasselDatabase

@Module
class DatabaseModule {
  @Provides
  fun provideQuasselDatabase(context: Context): QuasselDatabase {
    return QuasselDatabase.Creator.init(context)
  }

  @Provides
  fun provideAccountsDatabase(context: Context): AccountDatabase {
    return AccountDatabase.Creator.init(context)
  }
}
