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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.ui.theme.Template2025Theme

@Composable
fun NotificationsSettingsScreen(onBack: (() -> Unit)? = null) {

    var push by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf(false) }
    var reminders by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(20.dp)
    ) {

        Text(
            text = "Notificaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF21409A)
        )

        Spacer(Modifier.height(20.dp))

        SettingSwitchItem("Notificaciones Push", push) { push = it }
        SettingSwitchItem("Correo", email) { email = it }
        SettingSwitchItem("Recordatorios diarios", reminders) { reminders = it }

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = { onBack?.invoke() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18379A)),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(0.5f)
        ) {
            Text("Volver", color = Color.White)
        }
    }
}

@Composable
fun SettingSwitchItem(text: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D82CC))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Switch(checked = value, onCheckedChange = onChange)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationsSettingsScreenPreview() {
    Template2025Theme { NotificationsSettingsScreen() }
}
