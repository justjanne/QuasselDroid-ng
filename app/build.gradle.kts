import com.android.build.gradle.AppExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KaptAnnotationProcessorOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import java.io.ByteArrayOutputStream
import java.util.*

apply {
  plugin("com.android.application")
  plugin("kotlin-android")
  plugin("kotlin-kapt")
}

android {
  compileSdkVersion(26)
  buildToolsVersion("26.0.0")

  signingConfigs {
    val signing = project.rootProject.properties("signing.properties")
    if (signing != null) {
      create("release") {
        storeFile = file(signing.getProperty("storeFile"))
        storePassword = signing.getProperty("storePassword")
        keyAlias = signing.getProperty("keyAlias")
        keyPassword = signing.getProperty("keyPassword")
      }
    }
  }

  defaultConfig {
    minSdkVersion(15)
    targetSdkVersion(26)

    applicationId = "de.kuschku.quasseldroid_ng.test"
    versionCode = 1
    versionName = cmd("git", "describe", "--tags", "HEAD") ?: "1.0.0"

    buildConfigField("String", "GIT_HEAD", "\"${cmd("git", "rev-parse", "HEAD") ?: ""}\"")
    buildConfigField("long", "GIT_COMMIT_DATE", "${cmd("git", "show", "-s", "--format=%ct") ?: 0}L")

    signingConfig = signingConfigs.getByName("release")

    setProperty("archivesBaseName", "QuasselDroidNG-$versionName")

    javaCompileOptions {
      annotationProcessorOptions {
        arguments = mapOf(
          "room.schemaLocation" to "$projectDir/schemas"
        )
      }
    }
  }

  buildTypes {
    getByName("release") {
      //proguardFiles("proguard-rules.pro")
    }

    getByName("debug") {
      applicationIdSuffix = "debug"
    }
  }
}

val appCompatVersion = "26.1.0"
val appArchVersion = "1.0.0-alpha9-1"
dependencies {
  implementation(kotlin("stdlib"))

  implementation(appCompat("support-v4")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appCompat("appcompat-v7")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appCompat("design")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appCompat("customtabs")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appCompat("cardview-v7")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appCompat("recyclerview-v7")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }

  implementation(appArch("lifecycle", "runtime", version = "1.0.0")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  implementation(appArch("lifecycle", "extensions")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  kapt(appArch("lifecycle", "compiler"))

  implementation(appArch("persistence.room", "runtime")) {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  kapt(appArch("persistence.room", "compiler"))

  implementation(appArch("paging", "runtime", version = "1.0.0-alpha1")) {
    exclude(group = "junit", module = "junit")
  }

  implementation("org.threeten:threetenbp:1.3.6") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }

  implementation("com.jakewharton:butterknife:8.7.0") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  kapt("com.jakewharton:butterknife-compiler:8.7.0")

  implementation("ch.acra:acra:4.9.2") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }

  implementation(project(":invokerannotations"))
  kapt(project(":invokergenerator"))

  testImplementation("android.arch.persistence.room:testing:1.0.0-alpha9") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  testImplementation("junit:junit:4.12") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }

  androidTestImplementation("com.android.support.test:runner:0.5") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
  androidTestImplementation("com.android.support.test:rules:0.5") {
    exclude(group = "com.android.support", module = "support-media-compat")
  }
}

kapt {
  arguments(delegateClosureOf<KaptAnnotationProcessorOptions> {
    arg("eventBusIndex", "de.kuschku.quasseldroid_ng.EventBusIndex")
  })
}

fun cmd(vararg command: String) = try {
  val stdOut = ByteArrayOutputStream()
  exec {
    commandLine(*command)
    standardOutput = stdOut
  }
  stdOut.toString(Charsets.UTF_8.name()).trim()
} catch (e: Throwable) {
  e.printStackTrace()
  null
}

fun Project.properties(fileName: String): Properties? {
  val file = file(fileName)
  if (!file.exists())
    return null
  val props = Properties()
  props.load(file.inputStream())
  return props
}

/**
 * Builds the dependency notation for the named AppCompat [module] at the given [version].
 *
 * @param module simple name of the AppCompat module, for example "cardview-v7".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appCompat(module: String, version: String? = null)
  = "com.android.support:$module:${version ?: appCompatVersion}"

/**
 * Builds the dependency notation for the named AppArch [module] at the given [version].
 *
 * @param module simple name of the AppArch module, for example "persistence.room".
 * @param submodule simple name of the AppArch submodule, for example "runtime".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appArch(module: String, submodule: String, version: String? = null)
  = "android.arch.$module:$submodule:${version ?: appArchVersion}"

fun Project.android(f: AppExtension.() -> Unit)
  = configure(f)

fun Project.kapt(f: KaptExtension.() -> Unit)
  = configure(f)

fun DependencyHandlerScope.androidJacocoAgent(dependencyNotation: Any)
  = "androidJacocoAgent"(dependencyNotation)

fun DependencyHandlerScope.androidJacocoAgent(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidJacocoAgent"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidJacocoAnt(dependencyNotation: Any)
  = "androidJacocoAnt"(dependencyNotation)

fun DependencyHandlerScope.androidJacocoAnt(dependencyNotation: String,
                                            dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidJacocoAnt"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestAnnotationProcessor(dependencyNotation: Any)
  = "androidTestAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.androidTestAnnotationProcessor(dependencyNotation: String,
                                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestApk(dependencyNotation: Any)
  = "androidTestApk"(dependencyNotation)

fun DependencyHandlerScope.androidTestApk(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: Any)
  = "androidTestImplementation"(dependencyNotation)

fun DependencyHandlerScope.androidTestImplementation(dependencyNotation: String,
                                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestJackPlugin(dependencyNotation: Any)
  = "androidTestJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.androidTestJackPlugin(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestProvided(dependencyNotation: Any)
  = "androidTestProvided"(dependencyNotation)

fun DependencyHandlerScope.androidTestProvided(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.androidTestWearApp(dependencyNotation: Any)
  = "androidTestWearApp"(dependencyNotation)

fun DependencyHandlerScope.androidTestWearApp(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "androidTestWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.annotationProcessor(dependencyNotation: Any)
  = "annotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.annotationProcessor(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "annotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.apk(dependencyNotation: Any)
  = "apk"(dependencyNotation)

fun DependencyHandlerScope.apk(dependencyNotation: String,
                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "apk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.archives(dependencyNotation: Any)
  = "archives"(dependencyNotation)

fun DependencyHandlerScope.archives(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "archives"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.implementation(dependencyNotation: Any)
  = "implementation"(dependencyNotation)

fun DependencyHandlerScope.implementation(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "implementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugAnnotationProcessor(dependencyNotation: Any)
  = "debugAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.debugAnnotationProcessor(dependencyNotation: String,
                                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugApk(dependencyNotation: Any)
  = "debugApk"(dependencyNotation)

fun DependencyHandlerScope.debugApk(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any)
  = "debugImplementation"(dependencyNotation)

fun DependencyHandlerScope.debugImplementation(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugJackPlugin(dependencyNotation: Any)
  = "debugJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.debugJackPlugin(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugProvided(dependencyNotation: Any)
  = "debugProvided"(dependencyNotation)

fun DependencyHandlerScope.debugProvided(dependencyNotation: String,
                                         dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugWearApp(dependencyNotation: Any)
  = "debugWearApp"(dependencyNotation)

fun DependencyHandlerScope.debugWearApp(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.default(dependencyNotation: Any)
  = "default"(dependencyNotation)

fun DependencyHandlerScope.default(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "default"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.jackPlugin(dependencyNotation: Any)
  = "jackPlugin"(dependencyNotation)

fun DependencyHandlerScope.jackPlugin(dependencyNotation: String,
                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "jackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kapt(dependencyNotation: Any)
  = "kapt"(dependencyNotation)

fun DependencyHandlerScope.kapt(dependencyNotation: String,
                                dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kapt"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptAndroidTest(dependencyNotation: Any)
  = "kaptAndroidTest"(dependencyNotation)

fun DependencyHandlerScope.kaptAndroidTest(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptAndroidTest"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptDebug(dependencyNotation: Any)
  = "kaptDebug"(dependencyNotation)

fun DependencyHandlerScope.kaptDebug(dependencyNotation: String,
                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptDebug"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptRelease(dependencyNotation: Any)
  = "kaptRelease"(dependencyNotation)

fun DependencyHandlerScope.kaptRelease(dependencyNotation: String,
                                       dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptRelease"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTest(dependencyNotation: Any)
  = "kaptTest"(dependencyNotation)

fun DependencyHandlerScope.kaptTest(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTest"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTestDebug(dependencyNotation: Any)
  = "kaptTestDebug"(dependencyNotation)

fun DependencyHandlerScope.kaptTestDebug(dependencyNotation: String,
                                         dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTestDebug"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kaptTestRelease(dependencyNotation: Any)
  = "kaptTestRelease"(dependencyNotation)

fun DependencyHandlerScope.kaptTestRelease(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kaptTestRelease"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.provided(dependencyNotation: Any)
  = "provided"(dependencyNotation)

fun DependencyHandlerScope.provided(dependencyNotation: String,
                                    dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "provided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseAnnotationProcessor(dependencyNotation: Any)
  = "releaseAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.releaseAnnotationProcessor(dependencyNotation: String,
                                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseApk(dependencyNotation: Any)
  = "releaseApk"(dependencyNotation)

fun DependencyHandlerScope.releaseApk(dependencyNotation: String,
                                      dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: Any)
  = "releaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseJackPlugin(dependencyNotation: Any)
  = "releaseJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.releaseJackPlugin(dependencyNotation: String,
                                             dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: Any)
  = "releaseProvided"(dependencyNotation)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseWearApp(dependencyNotation: Any)
  = "releaseWearApp"(dependencyNotation)

fun DependencyHandlerScope.releaseWearApp(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: Any)
  = "testAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testApk(dependencyNotation: Any)
  = "testApk"(dependencyNotation)

fun DependencyHandlerScope.testApk(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testImplementation(dependencyNotation: Any)
  = "testImplementation"(dependencyNotation)

fun DependencyHandlerScope.testImplementation(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugAnnotationProcessor(dependencyNotation: Any)
  = "testDebugAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testDebugAnnotationProcessor(dependencyNotation: String,
                                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugApk(dependencyNotation: Any)
  = "testDebugApk"(dependencyNotation)

fun DependencyHandlerScope.testDebugApk(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: Any)
  = "testDebugImplementation"(dependencyNotation)

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugJackPlugin(dependencyNotation: Any)
  = "testDebugJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testDebugJackPlugin(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: Any)
  = "testDebugProvided"(dependencyNotation)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: String,
                                             dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugWearApp(dependencyNotation: Any)
  = "testDebugWearApp"(dependencyNotation)

fun DependencyHandlerScope.testDebugWearApp(dependencyNotation: String,
                                            dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testJackPlugin(dependencyNotation: Any)
  = "testJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testJackPlugin(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testProvided(dependencyNotation: Any)
  = "testProvided"(dependencyNotation)

fun DependencyHandlerScope.testProvided(dependencyNotation: String,
                                        dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseAnnotationProcessor(dependencyNotation: Any)
  = "testReleaseAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testReleaseAnnotationProcessor(dependencyNotation: String,
                                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseApk(dependencyNotation: Any)
  = "testReleaseApk"(dependencyNotation)

fun DependencyHandlerScope.testReleaseApk(dependencyNotation: String,
                                          dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseApk"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: Any)
  = "testReleaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: String,
                                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseJackPlugin(dependencyNotation: Any)
  = "testReleaseJackPlugin"(dependencyNotation)

fun DependencyHandlerScope.testReleaseJackPlugin(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseJackPlugin"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: Any)
  = "testReleaseProvided"(dependencyNotation)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseWearApp(dependencyNotation: Any)
  = "testReleaseWearApp"(dependencyNotation)

fun DependencyHandlerScope.testReleaseWearApp(dependencyNotation: String,
                                              dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testWearApp(dependencyNotation: Any)
  = "testWearApp"(dependencyNotation)

fun DependencyHandlerScope.testWearApp(dependencyNotation: String,
                                       dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testWearApp"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.wearApp(dependencyNotation: Any)
  = "wearApp"(dependencyNotation)

fun DependencyHandlerScope.wearApp(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "wearApp"(dependencyNotation, dependencyConfiguration)
