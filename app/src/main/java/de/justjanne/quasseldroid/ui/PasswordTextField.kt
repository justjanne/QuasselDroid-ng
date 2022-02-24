package de.justjanne.quasseldroid.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import de.justjanne.quasseldroid.R

@Composable
fun PasswordTextField(
  value: TextFieldValue,
  onValueChange: (TextFieldValue) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
    keyboardType = KeyboardType.Password
  ),
  keyboardActions: KeyboardActions = KeyboardActions(),
  singleLine: Boolean = false,
  maxLines: Int = Int.MAX_VALUE,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  shape: Shape = MaterialTheme.shapes.small,
  colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
  val (showPassword, setShowPassword) = remember { mutableStateOf(false) }
  val painter = painterResource(
    if (showPassword) R.drawable.ic_eye_off
    else R.drawable.ic_eye
  )

  OutlinedTextField(
    value,
    onValueChange,
    modifier,
    enabled,
    readOnly,
    textStyle,
    label,
    placeholder,
    leadingIcon,
    {
      IconButton(onClick = { setShowPassword(!showPassword) }) {
        Icon(painter = painter, contentDescription = "")
      }
      trailingIcon?.invoke()
    },
    isError,
    if (showPassword) VisualTransformation.None
    else PasswordVisualTransformation(),
    keyboardOptions,
    keyboardActions,
    singleLine,
    maxLines,
    interactionSource,
    shape,
    colors
  )
}

@Composable
fun PasswordTextField(
  value: String,
  onValueChange: (String) -> Unit,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  readOnly: Boolean = false,
  textStyle: TextStyle = LocalTextStyle.current,
  label: @Composable (() -> Unit)? = null,
  placeholder: @Composable (() -> Unit)? = null,
  leadingIcon: @Composable (() -> Unit)? = null,
  trailingIcon: @Composable (() -> Unit)? = null,
  isError: Boolean = false,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(
    keyboardType = KeyboardType.Password
  ),
  keyboardActions: KeyboardActions = KeyboardActions(),
  singleLine: Boolean = false,
  maxLines: Int = Int.MAX_VALUE,
  interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
  shape: Shape = MaterialTheme.shapes.small,
  colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
  val (showPassword, setShowPassword) = remember { mutableStateOf(false) }
  val painter = painterResource(
    if (showPassword) R.drawable.ic_eye_off
    else R.drawable.ic_eye
  )

  OutlinedTextField(
    value,
    onValueChange,
    modifier,
    enabled,
    readOnly,
    textStyle,
    label,
    placeholder,
    leadingIcon,
    {
      IconButton(onClick = { setShowPassword(!showPassword) }) {
        Icon(painter = painter, contentDescription = "")
      }
      trailingIcon?.invoke()
    },
    isError,
    if (showPassword) VisualTransformation.None
    else PasswordVisualTransformation(),
    keyboardOptions,
    keyboardActions,
    singleLine,
    maxLines,
    interactionSource,
    shape,
    colors
  )
}
