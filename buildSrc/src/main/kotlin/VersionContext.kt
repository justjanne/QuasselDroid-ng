data class VersionContext(val version: String)

inline fun withVersion(version: String, f: VersionContext.() -> Unit) {
  VersionContext(version).f()
}