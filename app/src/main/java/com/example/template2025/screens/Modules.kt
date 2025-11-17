package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import com.example.template2025.viewModel.Module
import com.example.template2025.viewModel.ModuleViewModel


@Composable
fun ModulesScreen(
    navController: NavController,
    moduleViewModel: ModuleViewModel = viewModel(),
    modifier: Modifier = Modifier // Added modifier
) {
    val modules by moduleViewModel.modules.collectAsState()

    Column(
        modifier = modifier // Use the modifier here
            .fillMaxSize()
            .background(Color(0xFFE6F0F8))
            .padding(16.dp)
    ) {
        Text(
            text = "Módulos",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(modules) { module ->
                ModuleCard(module = module, onClick = {
                    navController.navigate(Route.InsideModule.createRoute(module.id))
                })
            }
        }
    }
}

@Composable
fun ModuleCard(module: Module, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFC0D6E8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start
        ) {
            Column {
                Text(
                    text = module.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Text(
                    text = module.subtitle,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Placeholder for the wavy image.
                Box(modifier = Modifier.height(40.dp))
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = module.progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0047AB),
                    trackColor = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModulesScreenPreview() {
    ModulesScreen(navController = rememberNavController())
}
