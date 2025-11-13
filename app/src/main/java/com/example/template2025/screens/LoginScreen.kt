@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.compose.material3.ExperimentalMaterial3Api   // <-- este import va aquí
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.composables.SillaDeRuedas
import com.example.template2025.composables.GlassOutlinedField
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onGoToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val fieldWidth = Modifier
        .fillMaxWidth(0.86f)   // 86% del ancho
        .widthIn(max = 420.dp) // tope máximo

    Scaffold(
        topBar = {
            // Barra azul de arriba sin funcionalidad (como la tenías)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(BlueDark)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueLight) // #dcedfc
                .padding(padding)
        ) {

            // Mascota en marca de agua
            SillaDeRuedas(
                size = 350.dp,
                alpha = 0.25f,
                alignment = Alignment.BottomEnd,
                rotation = -15f,
                offsetX = (100).dp,
                offsetY = (10).dp
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))

                Text(
                    text = "INCLUSIÓN",
                    color = BlueDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "Iniciar sesión",
                    color = BlueDark,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp
                )

                Spacer(Modifier.height(28.dp))

                // INPUT 1: estilo glass
                GlassOutlinedField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "correo / nombre de usuario",
                    modifier = fieldWidth
                )

                Spacer(Modifier.height(14.dp))

                // INPUT 2: estilo glass
                GlassOutlinedField(
                    value = pass,
                    onValueChange = { pass = it },
                    placeholder = "contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = fieldWidth
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onLogin,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BlueDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Inicia sesión", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(18.dp))

                OutlinedButton(
                    onClick = { /* TODO: Google Sign-In */ },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continuar con Google")
                }

                Spacer(Modifier.height(18.dp))

                TextButton(onClick = onGoToRegister) {
                    Text(
                        "¿No tienes cuenta? Regístrate!",
                        color = BlueDark,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
