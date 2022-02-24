package de.justjanne.quasseldroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import de.justjanne.quasseldroid.service.QuasselBackend
import de.justjanne.quasseldroid.ui.theme.QuasseldroidTheme

class MainActivity : ComponentActivity() {
  private val backend = QuasselBackend()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    backend.onCreate(this)
    setContent {
      QuasseldroidTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          QuasseldroidRouter(backend = backend)
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    backend.onStart(this)
  }

  override fun onResume() {
    super.onResume()
    backend.onResume(this)
  }

  override fun onStop() {
    super.onStop()
    backend.onStop(this)
  }

  override fun onDestroy() {
    super.onDestroy()
    backend.onDestroy(this)
  }
}
