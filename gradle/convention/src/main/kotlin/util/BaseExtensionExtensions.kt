package util

import com.android.build.gradle.BaseExtension
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

fun BaseExtension.kotlinOptions(configure: KotlinJvmOptions.() -> Unit): Unit =
  (this as ExtensionAware).extensions.configure("kotlinOptions", configure)
