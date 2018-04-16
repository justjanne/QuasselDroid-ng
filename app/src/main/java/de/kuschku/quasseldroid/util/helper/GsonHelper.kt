package de.kuschku.quasseldroid.util.helper

import com.google.gson.Gson
import java.io.File

inline fun <reified T> Gson.fromJson(file: File) = this.fromJson(file.reader(), T::class.java)
inline fun <reified T> Gson.fromJson(text: String) = this.fromJson(text, T::class.java)
