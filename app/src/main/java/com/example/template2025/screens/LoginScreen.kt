package com.example.template2025.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(onLogin: () -> Unit, onGoToRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true)
            OutlinedTextField(pass, { pass = it }, label = { Text("Contraseña") },
                singleLine = true, visualTransformation = PasswordVisualTransformation()
            )
            Button(onClick = onLogin, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }
            OutlinedButton(onClick = onGoToRegister, modifier = Modifier.fillMaxWidth()) {
                Text("Crear cuenta")
            }
        }
    }
}