import org.gradle.api.Project

fun Project.fancyVersionName(): String? {
  val commit = cmd("git", "rev-parse", "HEAD")
  val name = cmd("git", "describe", "--always", "--tags", "HEAD")

  return if (commit != null && name != null) "<a href=\\\"https://git.kuschku.de/justJanne/QuasselDroid-ng/commit/$commit\\\">$name</a>"
  else name
}
