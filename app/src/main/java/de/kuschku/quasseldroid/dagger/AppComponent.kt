package de.kuschku.quasseldroid.dagger

import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import de.kuschku.quasseldroid.QuasselDroid
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    DatabaseModule::class,
    SettingsModule::class
  ]
)
interface AppComponent : AndroidInjector<QuasselDroid> {
  @Component.Builder
  abstract class Builder : AndroidInjector.Builder<QuasselDroid>()
}

