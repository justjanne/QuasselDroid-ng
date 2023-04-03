package de.justjanne.quasseldroid.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform

inline fun <T, R> Flow<T?>.mapNullable(crossinline transform: suspend (value: T) -> R): Flow<R?> =
  transform { value ->
    emit(value?.let { transform(it) })
  }

inline fun <T, R> Flow<T?>.flatMapLatestNullable(crossinline transform: suspend (value: T) -> Flow<R>): Flow<R?> =
  transform { value ->
    if (value == null) emit(null)
    else emitAll(transform(value))
  }

@Composable
inline fun <T> rememberFlow(initial: T, crossinline calculation: @DisallowComposableCalls () -> Flow<T>): T {
  return remember(calculation).collectAsState(initial).value
}

@Composable
inline fun <T> rememberFlow(crossinline calculation: @DisallowComposableCalls () -> StateFlow<T>): T {
  return remember(calculation).collectAsState().value
}
