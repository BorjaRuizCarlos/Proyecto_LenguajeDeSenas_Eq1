@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.template2025.R
import com.example.template2025.avisodeprivacidad.AVISO_PRIVACIDAD
import com.example.template2025.composables.GlassOutlinedField
import com.example.template2025.composables.SillaDeRuedas
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.viewModel.AuthViewModel
import com.google.common.io.Files.append


//Funcion para el acuerdo de privasidad
@Composable
fun PrivacyPolicyRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    policyText: String = AVISO_PRIVACIDAD,
    linkColor: Color = Color(0, 156, 166),
    acceptOnDialogConfirm: Boolean = false // si true, marcará el checkbox al aceptar
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = linkColor,
                uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        val annotatedString = buildAnnotatedString {
            append("He leído y acepto el ")
            pushStringAnnotation(tag = "POLICY", annotation = "open")
            withStyle(
                style = SpanStyle(
                    color = linkColor,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Aviso de privacidad")
            }
            pop()
        }

        ClickableText(
            text = annotatedString,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            onClick = { offset ->
                annotatedString
                    .getStringAnnotations(tag = "POLICY", start = offset, end = offset)
                    .firstOrNull()
                    ?.let {
                        Log.d("PrivacyPolicy", "El usuario quiere ver el aviso de privacidad.")
                        showDialog = true
                    }
            }
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    if (showDialog) {
        val scrollState = rememberScrollState()

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Aviso de Privacidad") },
            text = {
                // Contenido desplazable, con altura controlada para no forzar el layout del diálogo
                Column(
                    modifier = Modifier
                        .heightIn(min = 100.dp, max = 360.dp)
                        .verticalScroll(scrollState)
                        .fillMaxWidth()
                ) {
                    Text(policyText)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (acceptOnDialogConfirm) onCheckedChange(true)
                        showDialog = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cerrar")
                }
            })
            }
}
@Composable
fun RegisterScreen(
    onRegistered: () -> Unit,
    onBackToLogin: () -> Unit,
    vm: AuthViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    // 2. CREA UN ESTADO PARA EL CHECKBOX
    var privacyPolicyAccepted by remember { mutableStateOf(false) }

    val signup by vm.signup.collectAsState()
    val snack = remember { SnackbarHostState() }

    LaunchedEffect(signup.success) {
        if (signup.success) {
            snack.showSnackbar("Cuenta creada ✅")
            vm.resetSignup()
            onRegistered()
        }
    }
    LaunchedEffect(signup.error) {
        signup.error?.let { snack.showSnackbar(it); vm.resetSignup() }
    }

    val fieldWidth = Modifier.fillMaxWidth(0.86f).widthIn(max = 420.dp)

    Scaffold(
        topBar = { Box(Modifier.fillMaxWidth().height(55.dp).background(BlueLight)) },
        snackbarHost = { SnackbarHost(snack) }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .background(BlueDark)
        ) {
            SillaDeRuedas(
                resId = R.drawable.hearing_aid,
                size = 750.dp,
                alpha = 0.20f,
                alignment = Alignment.BottomStart,
                rotation = 0f,
                offsetX = 0.dp,
                offsetY = 355.dp
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(start = 20.dp, top = 55.dp, end = 20.dp, bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cuenta Nueva",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(35.dp))

                GlassOutlinedField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = fieldWidth,
                    placeholder = "Nombre completo"
                )

                Spacer(Modifier.height(25.dp))

                GlassOutlinedField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = fieldWidth,
                    placeholder = "Correo"
                )

                Spacer(Modifier.height(25.dp))

                GlassOutlinedField(
                    value = pass,
                    onValueChange = { pass = it },
                    modifier = fieldWidth,
                    placeholder = "Contraseña",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(24.dp))

                // 3. AÑADE EL COMPOSABLE DEL AVISO DE PRIVACIDAD AQUÍ
                PrivacyPolicyRow(
                    checked = privacyPolicyAccepted,
                    onCheckedChange = { isChecked ->
                        privacyPolicyAccepted = isChecked
                    },
                    modifier = Modifier.then(fieldWidth) // Aplica el mismo ancho
                )
                // Spacer(Modifier.height(16.dp)) // El Spacer ya está dentro de PrivacyPolicyRow

                Button(
                    onClick = { vm.signup(name, email, pass) },
                    // 4. MODIFICA LA LÓGICA DEL 'enabled'
                    enabled = !signup.loading && privacyPolicyAccepted,
                    modifier = fieldWidth.height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BlueDark
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        if (signup.loading) "Creando..." else "Crear cuenta",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(onClick = onBackToLogin) {
                    Text(
                        "¿Ya tienes cuenta? Inicia sesión",
                        color = BlueLight,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = TextDecoration.Underline
                    )
                }
            } //
        }
    }
}