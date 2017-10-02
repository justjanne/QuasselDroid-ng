package de.kuschku.malheur.collectors

import android.app.Application
import android.content.res.Configuration
import android.util.SparseArray
import de.kuschku.malheur.CrashContext
import java.lang.reflect.Modifier

class ConfigurationCollector(private val application: Application) :
  Collector<Map<String, Any?>, Boolean> {
  private val configValueInfo = mutableMapOf<String, SparseArray<String>>()

  private val configurationFields = listOf(
    FieldDefinition(
      fieldName = "screenHeightDp"
    ),
    FieldDefinition(
      fieldName = "screenWidthDp"
    ),
    FieldDefinition(
      fieldName = "smallestScreenWidthDp"
    ),
    FieldDefinition(
      fieldName = "navigation",
      enumPrefix = "NAVIGATION"
    ),
    FieldDefinition(
      fieldName = "navigationHidden",
      enumPrefix = "NAVIGATIONHIDDEN"
    ),
    FieldDefinition(
      fieldName = "orientation",
      enumPrefix = "ORIENTATION"
    ),
    FieldDefinition(
      fieldName = "screenLayout",
      enumPrefix = "SCREENLAYOUT",
      isFlag = true
    ),
    FieldDefinition(
      fieldName = "touchscreen",
      enumPrefix = "TOUCHSCREEN"
    ),
    FieldDefinition(
      fieldName = "uiMode",
      enumPrefix = "UI_MODE",
      isFlag = true
    )
  )

  init {
    val configurationFieldPrefixes = configurationFields.map(FieldDefinition::enumPrefix)

    Configuration::class.java.declaredFields.filter {
      Modifier.isStatic(it.modifiers)
    }.filter {
      it.type == Int::class.java
    }.filterNot {
      it.name.endsWith("_MASK")
    }.forEach { field ->
      val group = configurationFieldPrefixes.find { field.name.startsWith(it + "_") }
      if (group != null) {
        val value = field.name.substring(group.length + 1)
        configValueInfo.getOrPut(group, ::SparseArray).put(field.getInt(null), value)
      }
    }
  }

  override fun collect(context: CrashContext, config: Boolean) = configurationFields.map {
    it to Configuration::class.java.getDeclaredField(it.fieldName)
  }.filter { (_, field) ->
    field != null && !Modifier.isStatic(field.modifiers)
  }.map { (info, field) ->
    val groupInfo = configValueInfo[info.enumPrefix]
    if (groupInfo != null) {
      val value = field.getInt(application.resources.configuration)
      if (info.isFlag) {
        info.fieldName to (0 until groupInfo.size()).map { idx ->
          groupInfo.keyAt(idx) to groupInfo.valueAt(idx)
        }.filter { (key, _) ->
          value and key != 0
        }.map { (_, value) ->
          value
        }.toList()
      } else {
        val valueConstant = groupInfo[value]
        if (valueConstant == null) {
          info.fieldName to value
        } else {
          info.fieldName to valueConstant
        }
      }
    } else {
      val value = field.getInt(application.resources.configuration)
      info.fieldName to value
    }
  }.toMap()

  class FieldDefinition(
    val fieldName: String,
    val enumPrefix: String? = null,
    val isFlag: Boolean = false
  )
}
