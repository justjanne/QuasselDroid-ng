package de.kuschku.malheur.collectors

import de.kuschku.malheur.CrashContext

interface Collector<out DataType, in ConfigType> {
  fun collect(context: CrashContext, config: ConfigType): DataType?
}

inline fun <DataType, ConfigType> Collector<DataType, ConfigType>.collectIf(
  context: CrashContext,
  config: ConfigType?
) = if (config != null) collect(context, config) else null

inline fun <DataType> collectIf(enabled: Boolean, closure: () -> DataType?) =
  if (enabled) closure() else null
