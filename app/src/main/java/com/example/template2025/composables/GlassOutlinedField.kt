package com.example.template2025.composables

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    // #F0F8FE con ~56% opacidad
    val glass = Color(0x8EF0F8FE)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        // Altura mínima “compacta” que no corta el placeholder (≈52–56dp)
        modifier = modifier.heightIn(min = 52.dp),
        singleLine = true,
        placeholder = { Text(placeholder) },
        visualTransformation = visualTransformation,
        textStyle = TextStyle(fontSize = 16.sp),
        shape = RoundedCornerShape(15.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = glass,
            unfocusedContainerColor = glass,
            disabledContainerColor = glass,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            disabledBorderColor = Color.Transparent
        )
    )
}
