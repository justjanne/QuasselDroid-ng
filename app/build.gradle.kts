import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.application")
  id("kotlin-android")
  id("kotlin-kapt")
  id("de.kuschku.justcode")
}

android {
  defaultConfig {
    applicationId = "com.iskrembilen.quasseldroid"

    setMinSdkVersion(21)
    setTargetSdkVersion(30)
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    val androidxComposeVersion: String by project.extra
    kotlinCompilerExtensionVersion = androidxComposeVersion
  }
}

kapt {
  correctErrorTypes = true
}

dependencies {
  val androidxCoreVersion: String by project.extra
  implementation("androidx.core", "core-ktx", androidxCoreVersion)

  val androidxAppcompatVersion: String by project.extra
  implementation("androidx.appcompat", "appcompat", androidxAppcompatVersion)

  val mdcVersion: String by project.extra
  implementation("com.google.android.material", "material", mdcVersion)

  val androidxComposeVersion: String by project.extra
  implementation("androidx.compose.ui", "ui", androidxComposeVersion)
  implementation("androidx.compose.material", "material", androidxComposeVersion)
  implementation("androidx.compose.ui", "ui-tooling", androidxComposeVersion)

  val androidxLifecycleVersion: String by project.extra
  implementation("androidx.lifecycle", "lifecycle-runtime-ktx", androidxLifecycleVersion)

  val androidxMultidexVersion: String by project.extra
  implementation("androidx.multidex", "multidex", androidxMultidexVersion)

  val daggerHiltVersion: String by project.extra
  implementation("com.google.dagger", "hilt-android", daggerHiltVersion)
  annotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)
  testImplementation("com.google.dagger", "hilt-android-testing", daggerHiltVersion)
  testAnnotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)
  androidTestImplementation("com.google.dagger", "hilt-android-testing", daggerHiltVersion)
  androidTestAnnotationProcessor("com.google.dagger", "hilt-android-compiler", daggerHiltVersion)

  implementation("org.threeten", "threetenbp", "1.4.0")

  implementation("io.coil-kt", "coil", "1.1.1")
  implementation("dev.chrisbanes.accompanist", "accompanist-coil", "0.5.0")

  testImplementation("junit", "junit", "4.13.1")
  androidTestImplementation("androidx.test.ext", "junit", "1.1.2")
  androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0")
}
