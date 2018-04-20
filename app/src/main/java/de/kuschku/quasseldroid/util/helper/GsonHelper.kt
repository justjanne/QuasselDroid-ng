package de.kuschku.quasseldroid.util.helper

import com.google.gson.Gson
import java.io.File

inline fun <reified T> Gson.fromJson(file: File): T = this.fromJson(file.reader(), T::class.java)
inline fun <reified T> Gson.fromJson(text: String): T = this.fromJson(text, T::class.java)
