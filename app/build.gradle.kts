/*
 * Quasseldroid - Quassel client for Android
 *
 * Copyright (c) 2019 Janne Mareike Koschinski
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

plugins {
  id("com.android.application")
  kotlin("android")
  kotlin("kapt")
}

android {
  compileSdkVersion(30)

  signingConfigs {
    SigningData.of(project.rootProject.properties("signing.properties"))?.let {
      create("default") {
        storeFile = file(it.storeFile)
        storePassword = it.storePassword
        keyAlias = it.keyAlias
        keyPassword = it.keyPassword
      }
    }
  }

  defaultConfig {
    minSdkVersion(20)
    targetSdkVersion(30)

    applicationId = "com.iskrembilen.quasseldroid"
    versionCode = cmd("git", "rev-list", "--count", "HEAD")?.toIntOrNull() ?: 1
    versionName = cmd("git", "describe", "--always", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("String", "FANCY_VERSION_NAME", "\"${fancyVersionName() ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.findByName("default")

    resConfigs("en", "en-rGB", "de", "fr", "fr-rCA", "it", "lt", "pt", "sr")

    vectorDrawables.useSupportLibrary = true

    setProperty("archivesBaseName", "Quasseldroid-$versionName")

    // Disable test runner analytics
    testInstrumentationRunnerArguments["disableAnalytics"] = "true"
    testInstrumentationRunner = "de.kuschku.quasseldroid.util.TestRunner"
  }

  buildTypes {
    getByName("release") {
      isZipAlignEnabled = true
      isMinifyEnabled = true
      isShrinkResources = true

      multiDexEnabled = false

      proguardFiles(
        getDefaultProguardFile("proguard-android.txt"),
        "proguard-rules.pro"
      )
    }

    getByName("debug") {
      applicationIdSuffix = ".debug"

      multiDexEnabled = true
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  testOptions {
    unitTests.isIncludeAndroidResources = true
  }

  lintOptions {
    isWarningsAsErrors = true
    lintConfig = file("../lint.xml")
  }

  buildFeatures {
    viewBinding = true
  }
}

dependencies {
  implementation(kotlin("stdlib", "1.4.31"))

  // App Compat
  implementation("com.google.android.material", "material", "1.1.0-alpha10")

  implementation("androidx.appcompat", "appcompat", "1.1.0")
  implementation("androidx.browser", "browser", "1.2.0")
  implementation("androidx.cardview", "cardview", "1.0.0")
  implementation("androidx.recyclerview", "recyclerview", "1.1.0")
  implementation("androidx.swiperefreshlayout", "swiperefreshlayout", "1.1.0-beta01")
  implementation("androidx.preference", "preference", "1.1.0")
  // Only needed for ringtone preference
  implementation("androidx.legacy", "legacy-preference-v14", "1.0.0")
  implementation("androidx.constraintlayout", "constraintlayout", "2.0.0-beta4")

  withVersion("2.2.5") {
    implementation("androidx.room", "room-runtime", version)
    annotationProcessor("androidx.room", "room-compiler", version)
    implementation("androidx.room", "room-rxjava2", version)
    testImplementation("androidx.room", "room-testing", version)
  }
  withVersion("2.2.0") {
    implementation("androidx.lifecycle", "lifecycle-extensions", version)
    implementation("androidx.lifecycle", "lifecycle-reactivestreams", version)
  }
  testImplementation("androidx.arch.core", "core-testing", "2.1.0")
  implementation(project(":lifecycle-ktx"))

  implementation("androidx.paging", "paging-runtime", "2.1.2")

  implementation("androidx.multidex", "multidex", "2.0.1")

  // Utility
  implementation("io.reactivex.rxjava2", "rxandroid", "2.1.1")
  implementation("io.reactivex.rxjava2", "rxjava", "2.2.12")
  implementation("org.threeten", "threetenbp", "1.4.0", classifier = "no-tzdb")
  implementation("org.jetbrains", "annotations", "17.0.0")
  implementation("com.google.code.gson", "gson", "2.8.5")
  implementation("commons-codec", "commons-codec", "1.13")
  implementation("com.squareup.retrofit2", "retrofit", "2.6.1")
  implementation("com.squareup.retrofit2", "converter-gson", "2.6.1")
  implementation("com.github.pwittchen", "reactivenetwork-rx2", "3.0.6")
  withVersion("10.1.0") {
    implementation("com.jakewharton", "butterknife", version)
    kapt("com.jakewharton", "butterknife-compiler", version)
  }

  // Quassel
  implementation(project(":viewmodel"))
  implementation(project(":persistence"))
  implementation(project(":lib")) {
    exclude(group = "org.threeten", module = "threetenbp")
  }

  // UI
  implementation("com.leinardi.android", "speed-dial", "3.0.0")
  implementation("me.zhanghai.android.materialprogressbar", "library", "1.6.1")
  implementation("com.google.android", "flexbox", "1.1.0")
  implementation(project(":ui_spinner"))
  withVersion("0.9.6.0") {
    implementation("com.afollestad.material-dialogs", "core", version)
    implementation("com.afollestad.material-dialogs", "commons", version)
  }
  withVersion("4.9.0") {
    implementation("com.github.bumptech.glide", "glide", version)
    implementation("com.github.bumptech.glide", "recyclerview-integration", version)
    kapt("com.github.bumptech.glide", "compiler", version)
  }

  // Quality Assurance
  implementation(project(":malheur"))
  withVersion("2.2") {
    debugImplementation("com.squareup.leakcanary", "leakcanary-android", version)
  }

  // Dependency Injection
  withVersion("2.24") {
    implementation("com.google.dagger", "dagger", version)
    kapt("com.google.dagger", "dagger-compiler", version)
    kapt("com.google.dagger", "dagger-android-processor", version)
    implementation("com.google.dagger", "dagger-android", version)
    implementation("com.google.dagger", "dagger-android-support", version)
  }

  testImplementation("junit", "junit", "4.12")
  testImplementation("org.robolectric", "robolectric", "4.3.1") {
    exclude(group = "org.threeten", module = "threetenbp")
    exclude(group = "com.google.auto.service", module = "auto-service")
  }

  androidTestImplementation("junit", "junit", "4.12")
  androidTestImplementation("androidx.test.espresso", "espresso-core", "3.3.0-alpha02")
  androidTestImplementation("androidx.test.espresso", "espresso-contrib", "3.3.0-alpha02")
  androidTestImplementation("androidx.test.ext", "junit", "1.1.2-alpha02")
  androidTestImplementation("androidx.test", "runner", "1.3.0-alpha02")
  androidTestImplementation("androidx.test", "rules", "1.3.0-alpha02")
}
