package de.justjanne.quasseldroid.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.justjanne.quasseldroid.ui.theme.QuasselTheme
import de.justjanne.quasseldroid.ui.theme.Typography

@Preview(name = "New Message", showBackground = true)
@Composable
fun NewMessageView() {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Spacer(Modifier.width(8.dp))
        Divider(
            color = QuasselTheme.security.insecure,
            modifier = Modifier
                .height(1.dp)
                .weight(1.0f)
                .align(Alignment.CenterVertically),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            "New",
            modifier = Modifier
                .align(Alignment.CenterVertically),
            color = QuasselTheme.security.insecure,
            style = Typography.body2,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.width(8.dp))
    }
}
