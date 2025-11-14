package com.example.template2025.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.template2025.R

/**
 * Marca de agua en una esquina. Úsalo dentro de un Box { ... }.
 */
@Composable
fun BoxScope.SillaDeRuedas(
    resId: Int = R.drawable.ruedas, // pon tu imagen en res/drawable/mascota.png
    size: Dp = 120.dp,
    alpha: Float = 0.18f,
    alignment: Alignment = Alignment.BottomEnd,
    offsetX: Dp = (-12).dp,
    offsetY: Dp = (-12).dp,
    rotation: Float = 0f,                // grados (horario positivo)
    flipHorizontal: Boolean = false,
    flipVertical: Boolean = false
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(size)
            .align(alignment)
            .offset(x = offsetX, y = offsetY)
            .graphicsLayer(
                alpha = alpha,
                rotationZ = rotation,
                scaleX = if (flipHorizontal) -1f else 1f,
                scaleY = if (flipVertical) -1f else 1f
            )
    )
}
