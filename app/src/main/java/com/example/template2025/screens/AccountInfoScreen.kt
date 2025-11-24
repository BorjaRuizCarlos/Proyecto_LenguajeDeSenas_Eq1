package com.example.template2025.screens

import androidx.compose.foundation.background
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
fun AccountInfoScreen(onBack: (() -> Unit)? = null) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(20.dp)
    ) {

        Text(
            text = "Información de la Cuenta",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF21409A)
        )

        Spacer(Modifier.height(20.dp))

        InfoItem("Correo", "user@email.com")
        InfoItem("Fecha de creación", "12 Feb 2024")
        InfoItem("Estado", "Activo")

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
fun InfoItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF6D82CC))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountInfoScreenPreview() {
    Template2025Theme { AccountInfoScreen() }
}
