package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.ui.theme.Template2025Theme

@Composable
fun SettingsScreen(
    navController: NavController,
    token: String? // 🔹 Recibe el token
) {
    // Si no hay token, no mostramos el contenido principal
    if (token.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F0F8)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró token.\nVuelve a iniciar sesión.",
                color = Color(0xFF21409A),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // estados locales (por ahora no dependen de un ViewModel)
    var username by remember { mutableStateOf("Usuario de prueba") }
    var bio by remember { mutableStateOf("Bio de prueba para ajustes") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Configuración de cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF21409A)
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFFFF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        text = "Información básica",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF21409A),
                        fontSize = 16.sp
                    )

                    // ----- Username -----
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // ----- Bio / descripción -----
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Descripción / Bio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp),
                        maxLines = 4
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón guardar
            Button(
                onClick = {
                    // Aquí iría la lógica para guardar los datos en el ViewModel/API
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21409A)),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text(text = "Guardar cambios", color = Color.White)
            }

            Spacer(Modifier.height(12.dp))

            // Botón volver sin guardar
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18379A)),
                modifier = Modifier.fillMaxWidth(0.5f)
            ) {
                Text(text = "Volver", color = Color.White)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    Template2025Theme {
        SettingsScreen(navController = rememberNavController(), token = "fake_token_for_preview")
    }
}
