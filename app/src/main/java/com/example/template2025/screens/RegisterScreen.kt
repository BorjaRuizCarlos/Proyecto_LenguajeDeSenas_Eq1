@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.template2025.screens

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
    onBackToLogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val signup by vm.signup.collectAsState()
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(signup.success) { if (signup.success) { snack.showSnackbar("Cuenta creada ✅"); vm.resetSignup(); onRegistered() } }
    LaunchedEffect(signup.error) { signup.error?.let { snack.showSnackbar(it); vm.resetSignup() } }

    val fieldWidth = Modifier.fillMaxWidth(0.86f).widthIn(max = 420.dp)

    Scaffold(
        topBar = {
            Box(Modifier.fillMaxWidth().height(44.dp).background(BlueLight))
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        Box(Modifier.fillMaxSize().background(BlueDark).padding(padding)) {
            SillaDeRuedas(
                resId = R.drawable.hearing_aid,
                size = 750.dp, alpha = 0.20f,
                alignment = Alignment.BottomStart,
                rotation = 0f, offsetX = 0.dp, offsetY = 370.dp
            )

            Column(
                Modifier.fillMaxSize().padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))
                Text("Cuenta Nueva", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(24.dp))

                // Nombre
                GlassOutlinedField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = fieldWidth,
                    placeholder = "Nombre completo"
                )

                Spacer(Modifier.height(12.dp))

// Correo
                GlassOutlinedField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = fieldWidth,
                    placeholder = "Correo"
                )

                Spacer(Modifier.height(12.dp))

// Contraseña
                GlassOutlinedField(
                    value = pass,
                    onValueChange = { pass = it },
                    modifier = fieldWidth,
                    placeholder = "Contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )


                Button(
                    onClick = { vm.signup(name, email, pass) },
                    enabled = !signup.loading,
                    modifier = fieldWidth.height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = BlueDark),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(if (signup.loading) "Creando..." else "Crear cuenta", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onBackToLogin) {
                    Text("¿Ya tienes cuenta? Inicia sesión", color = BlueLight, fontWeight = FontWeight.SemiBold, textDecoration = TextDecoration.Underline)
                }
            }
        }
    }
}
