package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.ui.theme.Template2025Theme

@Composable
fun PrivacySettingsScreen(onBack: (() -> Unit)? = null) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(20.dp)
    ) {

        Text(
            text = "Privacidad",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF21409A)
        )

        Spacer(Modifier.height(20.dp))

        PrivacyItem("Cambiar contraseña")
        PrivacyItem("Términos de uso")
        PrivacyItem("Gestión de datos personales")

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
fun PrivacyItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D82CC))
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacySettingsScreenPreview() {
    Template2025Theme { PrivacySettingsScreen() }
}
