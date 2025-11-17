@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.R
import com.example.template2025.composables.MissionCard
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.MissionUi
import com.example.template2025.ui.theme.Template2025Theme

/**
 * Muestra el contenido de la pantalla de misiones diarias.
 * No contiene su propio Scaffold para poder ser reutilizable.
 */
@Composable
fun MisionesDiariasScreen(
    missions: List<MissionUi>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight)
    ) {
        // Mascota grande PNG en la esquina inferior izquierda
        Image(
            painter = painterResource(id = R.drawable.ic_mision_mascota_corner),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 0.dp)
                .size(220.dp),
            contentScale = ContentScale.Fit
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            Text(
                text = "Misiones Diarias!",
                color = BlueDark,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 34.sp
            )

            Spacer(Modifier.height(26.dp))

            missions.forEach { mission ->
                MissionCard(
                    mission = mission,
                    sofaIcon = R.drawable.ic_mision_sofa,   // PNG del sofá
                    modifier = Modifier.fillMaxWidth(0.94f)
                )
                Spacer(Modifier.height(18.dp))
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

/* PREVIEW CON PNGS */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MisionesDiariasPreview() {
    Template2025Theme {
        // El Scaffold se usa aquí solo para el preview, para simular el look & feel.
        Scaffold {
            padding ->
            val missions = listOf(
                MissionUi("Gana 50 XP", 43, 50, R.drawable.ic_mision_xp),
                MissionUi("Completa 2 lecciones", 1, 2, R.drawable.ic_mision_lecciones),
                MissionUi("Termina un modulo", 43, 50, R.drawable.ic_mision_modulo)
            )

            // Pasamos el padding del Scaffold del preview a la pantalla real.
            MisionesDiariasScreen(
                missions = missions,
                modifier = Modifier.padding(padding)
            )
        }
    }
}