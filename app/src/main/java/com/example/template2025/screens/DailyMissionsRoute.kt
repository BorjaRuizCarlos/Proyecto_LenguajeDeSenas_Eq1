package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.template2025.data.api.ApiService
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.viewModel.DailyMissionsUiState
import com.example.template2025.viewModel.DailyMissionsViewModel
import com.example.template2025.viewModel.DailyMissionsViewModelFactory

@Composable
fun DailyMissionsRoute(
    token: String?
) {
    // Sin token -> mensaje de error
    if (token.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró token.\nVuelve a iniciar sesión.",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    val apiService = ApiService.RetrofitClient.apiService

    val viewModel: DailyMissionsViewModel = viewModel(
        factory = DailyMissionsViewModelFactory(apiService)
    )

    LaunchedEffect(token) {
        viewModel.fetchDailyMissions(token)
    }

    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {
        is DailyMissionsUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is DailyMissionsUiState.Error -> {
            val message = (uiState as DailyMissionsUiState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar misiones:\n$message",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        is DailyMissionsUiState.Success -> {
            val missions = (uiState as DailyMissionsUiState.Success).missions
            MisionesDiariasScreen(
                missions = missions,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
