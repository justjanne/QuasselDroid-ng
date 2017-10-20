buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.0.0-rc2")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.1.51")
    classpath("org.jetbrains.kotlin:kotlin-android-extensions:1.1.51")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
  }
}
