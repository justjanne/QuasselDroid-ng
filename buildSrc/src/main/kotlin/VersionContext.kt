data class VersionContext(val version: String)

inline fun withVersion(version: Any?, f: VersionContext.() -> Unit) {
  (version as? String)?.let {
    VersionContext(version).f()
  }
}