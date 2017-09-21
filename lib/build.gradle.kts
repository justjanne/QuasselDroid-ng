
import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependency
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

apply {
  plugin("kotlin")
  plugin("kotlin-kapt")
}

val appCompatVersion = "26.1.0"
dependencies {
  implementation(kotlin("stdlib"))

  implementation(appCompat("support-annotations"))
  implementation("org.threeten:threetenbp:1.3.6")
  implementation("io.reactivex.rxjava2:rxjava:2.1.3")

  implementation(project(":invokerannotations"))
  kapt(project(":invokergenerator"))

  testImplementation("junit:junit:4.12")
}

tasks.withType(KotlinCompile::class.java) {
  kotlinOptions {
    freeCompilerArgs = listOf(
      "-Xno-param-assertions",
      "-Xno-call-assertions"
    )
  }
}

/**
 * Builds the dependency notation for the named AppCompat [module] at the given [version].
 *
 * @param module simple name of the AppCompat module, for example "cardview-v7".
 * @param version optional desired version, null implies [appCompatVersion].
 */
fun appCompat(module: String, version: String? = null)
  = "com.android.support:$module:${version ?: appCompatVersion}"

fun Project.kapt(f: KaptExtension.() -> Unit)
  = configure(f)

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

fun DependencyHandlerScope.debugImplementation(dependencyNotation: Any)
  = "debugImplementation"(dependencyNotation)

fun DependencyHandlerScope.debugImplementation(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.debugProvided(dependencyNotation: Any)
  = "debugProvided"(dependencyNotation)

fun DependencyHandlerScope.debugProvided(dependencyNotation: String,
                                         dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "debugProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.default(dependencyNotation: Any)
  = "default"(dependencyNotation)

fun DependencyHandlerScope.default(dependencyNotation: String,
                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "default"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.kapt(dependencyNotation: Any)
  = "kapt"(dependencyNotation)

fun DependencyHandlerScope.kapt(dependencyNotation: String,
                                dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "kapt"(dependencyNotation, dependencyConfiguration)

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

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: Any)
  = "releaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.releaseImplementation(dependencyNotation: String,
                                                 dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: Any)
  = "releaseProvided"(dependencyNotation)

fun DependencyHandlerScope.releaseProvided(dependencyNotation: String,
                                           dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "releaseProvided"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: Any)
  = "testAnnotationProcessor"(dependencyNotation)

fun DependencyHandlerScope.testAnnotationProcessor(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testAnnotationProcessor"(dependencyNotation, dependencyConfiguration)

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

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: Any)
  = "testDebugImplementation"(dependencyNotation)

fun DependencyHandlerScope.testDebugImplementation(dependencyNotation: String,
                                                   dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: Any)
  = "testDebugProvided"(dependencyNotation)

fun DependencyHandlerScope.testDebugProvided(dependencyNotation: String,
                                             dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testDebugProvided"(dependencyNotation, dependencyConfiguration)

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

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: Any)
  = "testReleaseImplementation"(dependencyNotation)

fun DependencyHandlerScope.testReleaseImplementation(dependencyNotation: String,
                                                     dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseImplementation"(dependencyNotation, dependencyConfiguration)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: Any)
  = "testReleaseProvided"(dependencyNotation)

fun DependencyHandlerScope.testReleaseProvided(dependencyNotation: String,
                                               dependencyConfiguration: ExternalModuleDependency.() -> Unit)
  = "testReleaseProvided"(dependencyNotation, dependencyConfiguration)
