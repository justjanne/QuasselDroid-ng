package de.kuschku.malheur.util

fun reflectionCollectConstants(klass: Class<*>?) = klass?.declaredFields
  ?.mapNotNull {
    var result: Pair<String, Any?>? = null
    try {
      result = it.name to it.get(null)
    } catch (e: IllegalAccessException) {
    } catch (e: IllegalArgumentException) {
    }
    result
  }?.toMap()

fun <T> reflectionCollectGetters(klass: Class<T>?) = klass?.declaredMethods
  ?.filter { it.parameterTypes.isEmpty() && it.returnType != Void::class.java }
  ?.filter { it.name != "getClass" }
  ?.filter { it.name.startsWith("get") || it.name.startsWith("is") }
  ?.mapNotNull {
    var result: Pair<String, Any?>? = null
    try {
      result = it.name to it.invoke(it)
    } catch (e: IllegalAccessException) {
    } catch (e: IllegalArgumentException) {
    }
    result
  }?.toMap()
