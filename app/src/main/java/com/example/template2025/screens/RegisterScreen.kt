@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.template2025.R
import com.example.template2025.composables.GlassOutlinedField
import com.example.template2025.composables.SillaDeRuedas
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.viewModel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit
) {
    val vm: AuthViewModel = viewModel()
    val ui by vm.signup.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val fieldWidth = Modifier
        .fillMaxWidth(0.86f)
        .widthIn(max = 420.dp)

    val snackbarHostState = remember { SnackbarHostState() }

    // Navega cuando se registre ok
    LaunchedEffect(ui.success) {
        if (ui.success) {
            onRegistered()
            vm.resetSignup()
        }
    }
    // Muestra errores
    LaunchedEffect(ui.error) {
        ui.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .background(BlueLight)
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueDark)
                .padding(padding)
        ) {
            SillaDeRuedas(
                resId = R.drawable.hearing_aid,
                size = 750.dp,
                alpha = 0.20f,
                alignment = Alignment.BottomStart,
                rotation = 0f,
                offsetX = 0.dp,
                offsetY = 370.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))

                Text(
                    text = "Cuenta Nueva",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(24.dp))

                GlassOutlinedField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Nombre completo",
                    modifier = fieldWidth
                )
                Spacer(Modifier.height(12.dp))

                GlassOutlinedField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Correo",
                    modifier = fieldWidth
                )
                Spacer(Modifier.height(12.dp))

                GlassOutlinedField(
                    value = pass,
                    onValueChange = { pass = it },
                    placeholder = "Contraseña",
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = fieldWidth
                )

                Spacer(Modifier.height(22.dp))

                Button(
                    onClick = { vm.signup(name, email, pass) },
                    enabled = !ui.loading && name.isNotBlank() && email.isNotBlank() && pass.length >= 6,
                    modifier = fieldWidth.height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BlueDark,
                        disabledContainerColor = Color.White.copy(alpha = 0.5f),
                        disabledContentColor = BlueDark.copy(alpha = 0.5f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    if (ui.loading) {
                        CircularProgressIndicator(strokeWidth = 2.dp, color = BlueDark)
                    } else {
                        Text("Crear cuenta", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = onBackToLogin) {
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = BlueLight,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
        }
    }
}
