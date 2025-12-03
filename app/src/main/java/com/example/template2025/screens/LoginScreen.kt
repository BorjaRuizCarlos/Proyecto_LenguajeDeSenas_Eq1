@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.template2025.R
import com.example.template2025.composables.GlassOutlinedField
import com.example.template2025.composables.SillaDeRuedas
import com.example.template2025.dataStore.DataStore
import com.example.template2025.dataStore.TokenStore
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit,
    onGoToRegister: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    val login by vm.login.collectAsState()
    val snack = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Cuando el login es success -> guardar token + logged_in
    LaunchedEffect(login.success) {
        if (login.success) {
            val token = login.accessToken

            // Debug rápido
            println("LOGIN TOKEN = $token")

            if (!token.isNullOrBlank()) {
                TokenStore.saveToken(context, token)
                DataStore.setLoggedIn(context, true)
            }

            snack.showSnackbar("Bienvenido 👋")
            vm.resetLogin()
            onLoginOk()
        }
    }

    // Errores
    LaunchedEffect(login.error) {
        login.error?.let {
            snack.showSnackbar(it)
            vm.resetLogin()
        }
    }

    val fieldWidth = Modifier
        .fillMaxWidth(0.86f)
        .widthIn(max = 420.dp)

    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(BlueDark)
            )
        },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueLight)
                .padding(padding)
        ) {
            SillaDeRuedas(
                resId = R.drawable.ruedas,
                size = 350.dp,
                alpha = 0.25f,
                alignment = Alignment.BottomEnd,
                rotation = -15f,
                offsetX = 100.dp,
                offsetY = 10.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, top = 45.dp, end = 20.dp, bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("INCLUSIÓN", color = BlueDark, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Text(
                    "Iniciar sesión",
                    color = BlueDark,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 36.sp
                )
                Spacer(Modifier.height(28.dp))

                GlassOutlinedField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = fieldWidth,
                    placeholder = "Correo"

                )

                Spacer(Modifier.height(14.dp))

                GlassOutlinedField(
                    value = pass,
                    onValueChange = { pass = it },
                    modifier = fieldWidth,
                    placeholder = "Contraseña",
                    visualTransformation = PasswordVisualTransformation()

                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { vm.login(email.trim(), pass) },
                    enabled = !login.loading,
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
                    Text(
                        if (login.loading) "Entrando…" else "Inicia sesión",
                        fontWeight = FontWeight.Bold
                    )
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
