package de.kuschku.quasseldroid.dagger

import android.content.Context
import dagger.Binds
import dagger.Module
import de.kuschku.quasseldroid.QuasselDroid

@Module
abstract class AppModule {
  @Binds
  abstract fun provideContext(application: QuasselDroid): Context
}