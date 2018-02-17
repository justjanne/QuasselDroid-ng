buildscript {
  repositories {
    google()
    jcenter()
  }
  dependencies {
    classpath("com.android.tools.build:gradle:3.0.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.21")
    classpath("org.jetbrains.kotlin:kotlin-android-extensions:1.2.21")
  }
}

allprojects {
  repositories {
    google()
    jcenter()
    maven(url = "https://jitpack.io")
  }
}
