package com.example.template2025.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.R
import com.example.template2025.ui.theme.Template2025Theme
import com.example.template2025.viewModel.ProfileViewModel

/**
 * El Composable "inteligente" que se conecta con el ViewModel.
 */
@Composable
fun EditPhotoScreen(
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit
) {
    // Llama al Composable "tonto" que solo se encarga de la UI
    EditPhotoScreenContent(
        initialAvatar = profileViewModel.selectedAvatarResId.value,
        onSave = { newAvatarResId ->
            profileViewModel.updateAvatar(newAvatarResId)
            onBack()
        },
        onBack = onBack
    )
}

/**
 * El Composable "tonto" que solo muestra la UI y no sabe nada del ViewModel.
 */
@Composable
fun EditPhotoScreenContent(
    @DrawableRes initialAvatar: Int,
    onSave: (newAvatarResId: Int) -> Unit,
    onBack: () -> Unit
) {
    // 4 opciones precargadas (pon aquí tus drawables reales)
    val avatarOptions = listOf(
        R.drawable.avatar_1,
        R.drawable.avatar_2,
        R.drawable.avatar_3,
        R.drawable.avatar_4
    )

    // Estado interno para el avatar seleccionado
    var selectedAvatar by remember {
        mutableStateOf(initialAvatar)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Cambiar foto de perfil",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF21409A)
        )

        Spacer(Modifier.height(20.dp))

        // Vista previa grande de la foto elegida
        Card(
            modifier = Modifier.size(160.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF6D82CC))
        ) {
            Image(
                painter = painterResource(id = selectedAvatar),
                contentDescription = "Foto actual",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Elige tu avatar",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF21409A)
        )

        Spacer(Modifier.height(16.dp))

        // Grid 2x2 de avatares
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarOption(
                    resId = avatarOptions[0],
                    isSelected = selectedAvatar == avatarOptions[0],
                    onClick = { selectedAvatar = avatarOptions[0] }
                )
                AvatarOption(
                    resId = avatarOptions[1],
                    isSelected = selectedAvatar == avatarOptions[1],
                    onClick = { selectedAvatar = avatarOptions[1] }
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarOption(
                    resId = avatarOptions[2],
                    isSelected = selectedAvatar == avatarOptions[2],
                    onClick = { selectedAvatar = avatarOptions[2] }
                )
                AvatarOption(
                    resId = avatarOptions[3],
                    isSelected = selectedAvatar == avatarOptions[3],
                    onClick = { selectedAvatar = avatarOptions[3] }
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                onSave(selectedAvatar) // Llama al lambda con el avatar seleccionado
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21409A)),
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Guardar cambios", color = Color.White)
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = { onBack() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18379A)),
            modifier = Modifier.fillMaxWidth(0.5f)
        ) {
            Text("Volver", color = Color.White)
        }
    }
}

@Composable
private fun AvatarOption(
    resId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF21409A) else Color.Transparent

    Card(
        modifier = Modifier
            .size(80.dp)
            .clickable { onClick() }
            .border(
                width = 3.dp,
                color = borderColor,
                shape = RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D82CC))
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = "Avatar opción",
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditPhotoScreenPreview() {
    Template2025Theme {
        // El preview ahora llama al Composable "tonto" con datos de ejemplo
        EditPhotoScreenContent(
            initialAvatar = R.drawable.avatar_1,
            onSave = {},
            onBack = {}
        )
    }
}