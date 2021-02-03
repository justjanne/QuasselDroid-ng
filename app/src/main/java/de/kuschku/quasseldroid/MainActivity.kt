package de.kuschku.quasseldroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.kuschku.quasseldroid.ui.theme.QuasseldroidTheme
import de.kuschku.quasseldroid.ui.theme.shapes
import de.kuschku.quasseldroid.ui.theme.typography
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.random.Random

val time = MutableStateFlow(ZonedDateTime.now())

inline class Password1(private val s: String)

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    lifecycleScope.launchWhenResumed {
      while (true) {
        time.value = ZonedDateTime.now()
        delay(1000)
      }
    }
    setContent {
      JetChat()
    }
  }
}

fun randomColor(): Color = Color(
  red = Random.nextInt(0, 255),
  green = Random.nextInt(0, 255),
  blue = Random.nextInt(0, 255)
)

@Composable
fun UserPhoto(
  imageUrl: String,
  modifier: Modifier = Modifier,
) {
  val ringColor = remember { randomColor() }
  CoilImage(
    data = imageUrl as Any,
    contentDescription = null,
    modifier = modifier
      .border(2.dp, ringColor, CircleShape)
      .padding(4.dp)
      .clip(CircleShape)
      .size(38.dp)
  )
}

fun parseString(text: String): AnnotatedString {
  val builder = AnnotatedString.Builder()
  var monospace = false
  var lastIndex = 0
  fun addText(index: Int) {
    val before = builder.length
    builder.append(text.substring(lastIndex, index))
    val after = builder.length
    if (monospace) builder.addStyle(
      SpanStyle(
        fontFamily = FontFamily.Monospace,
        background = Color(0xFFDEDEDE),
      ),
      before,
      after
    )
    lastIndex = index + 1
  }
  for (i in text.indices) {
    if (text[i] == '`') {
      addText(i)
      monospace = !monospace
    }
  }
  addText(text.length)

  return builder.toAnnotatedString()
}

@Composable
fun ChatText(
  text: String,
  modifier: Modifier = Modifier
) {
  Text(
    parseString(text),
    style = typography.body1,
    modifier = modifier.padding(8.dp)
  )
}

@Composable
fun ChatBubble(
  content: @Composable () -> Unit
) {
  Surface(
    color = Color(0xFFF5F5F5),
    shape = shapes.medium,
    modifier = Modifier
      .padding(2.dp)
  ) {
    content()
  }
}

@Composable
fun ChatMessage(
  authorName: String,
  authorImageUrl: String,
  dateSent: String,
  content: @Composable () -> Unit
) {
  Row {
    UserPhoto(
      authorImageUrl,
      modifier = Modifier.padding(
        start = 4.dp,
        top = 4.dp,
        bottom = 4.dp,
        end = 12.dp
      )
    )
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(authorName, style = typography.h6)
        Spacer(modifier = Modifier.padding(4.dp))
        Text(
          dateSent,
          style = typography.body2.copy(
            color = MaterialTheme.colors.onSurface
          )
        )
      }
      content()
    }
  }
}

@Composable
fun InputLine() {
  var text by remember { mutableStateOf("") }

  Row {
    Box {
      TextField(
        value = text,
        onValueChange = { text = it },
        textStyle = typography.body1,
      )
      if (text.isEmpty()) {
        Text("Send a message")
      }
    }
    Icon(Icons.Outlined.MoreVert, "more")
  }
}

@Composable
fun TimeDisplay() {
  val time: ZonedDateTime by time.collectAsState(ZonedDateTime.now())

  Text(time.format(DateTimeFormatter.ISO_DATE_TIME))
}

@Preview
@Composable
fun JetChat() {
  QuasseldroidTheme {
    Scaffold(
      topBar = { TimeDisplay() },
      bottomBar = { InputLine() },
      bodyContent = {
        Column {
          ChatMessage(
            authorName = "Ali Conors",
            authorImageUrl = "https://picsum.photos/300/300",
            dateSent = "3:50 PM",
          ) {
            ChatBubble {
              ChatText("Yeah i’ve been mainly referring to the JetNews Sample :+1:")
            }
          }
          ChatMessage(
            authorName = "Taylor Brooks",
            authorImageUrl = "https://picsum.photos/300/300",
            dateSent = "10:50 PM",
          ) {
            ChatBubble {
              ChatText("Take a look at the `Flow.collectAsState()` APIs")
            }
            ChatBubble {
              ChatText("You can use all the same stuff")
            }
          }

        }
      }
    )
  }
}