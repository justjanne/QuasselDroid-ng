package de.justjanne.quasseldroid.util.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun LazyListState.OnBottomReached(
  buffer: Int = 0,
  onLoadMore: () -> Unit
) {
  require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

  val shouldLoadMore = remember {
    derivedStateOf {
      val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
        ?: return@derivedStateOf true
      lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
    }
  }

  LaunchedEffect(shouldLoadMore) {
    snapshotFlow { shouldLoadMore.value }
      .collect { if (it) onLoadMore() }
  }
}

@Composable
fun LazyListState.OnTopReached(
  buffer: Int = 0,
  onLoadMore: () -> Unit
) {
  require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

  val shouldLoadMore = remember {
    derivedStateOf {
      val lastVisibleItem = layoutInfo.visibleItemsInfo.firstOrNull()
        ?: return@derivedStateOf true
      lastVisibleItem.index <= buffer
    }
  }

  LaunchedEffect(shouldLoadMore) {
    snapshotFlow { shouldLoadMore.value }
      .collect { if (it) onLoadMore() }
  }
}
