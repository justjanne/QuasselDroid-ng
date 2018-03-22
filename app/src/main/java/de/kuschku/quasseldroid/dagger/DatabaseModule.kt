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