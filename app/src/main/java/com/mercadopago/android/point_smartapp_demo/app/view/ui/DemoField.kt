package com.mercadopago.android.point_smartapp_demo.app.view.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

@Composable
internal fun DemoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    password: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    placeholder: String? = null,
    textFontSize: TextUnit? = null,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val currentTextStyle = LocalTextStyle.current
    val textStyle = textFontSize?.let { fontSize ->
        currentTextStyle.copy(fontSize = fontSize, lineHeight = 16.sp)
    } ?: currentTextStyle

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        textStyle = textStyle,
        visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None,
        isError = errorMessage != null,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = errorMessage?.let { message -> ({ Text(message) }) },
    )
}
