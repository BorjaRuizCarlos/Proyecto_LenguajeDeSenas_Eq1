package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.R
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.Module
import com.example.template2025.viewModel.ModuleViewModel

@Composable
fun ModulesScreen(
    navController: NavController,
    moduleViewModel: ModuleViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val modules by moduleViewModel.modules.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(8.dp))

        // ---------- TÍTULO ----------
        Text(
            text = "Módulos",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF21409A),
            textAlign = TextAlign.Center
        )

        // “Onda” decorativa bajo el título
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(14.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFFD5E5FF))
        )

        Spacer(Modifier.height(24.dp))

        // ---------- GRID DE MÓDULOS ----------
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { module ->
                ModuleCard(
                    module = module,
                    onClick = {
                        navController.navigate(Route.InsideModule.createRoute(module.id))
                    }
                )
            }
        }
    }
}

@Composable
fun ModuleCard(
    module: Module,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1.1f)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ---------- TARJETA CON PNG ----------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent // el PNG será el fondo real
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {

                //  FONDO PNG
                Image(
                    painter = painterResource(id = R.drawable.modulo_fondo),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // 🔵 CONTENIDO ENCIMA DEL PNG
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = module.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = module.subtitle,
                        fontWeight = FontWeight.Medium,
                        fontSize = 11.sp,
                        color = Color(0xFFE8ECFF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.height(12.dp))
                }
            }
        }

        // ---------- BARRA DE PROGRESO ----------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        ) {
            val progress = module.progress.coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF21409A))
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ModulesScreenPreview() {
    ModulesScreen(navController = rememberNavController())
}
