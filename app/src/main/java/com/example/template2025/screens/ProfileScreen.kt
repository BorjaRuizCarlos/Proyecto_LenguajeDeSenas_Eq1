package com.example.template2025.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.template2025.data.api.ApiService
import com.example.template2025.navigation.Route
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme
import com.example.template2025.viewModel.ProfileUiState
import com.example.template2025.viewModel.ProfileViewModel
import com.example.template2025.viewModel.ProfileViewModelFactory

@Composable
fun ProfileScreen(
    navController: NavController,
    token: String?, 
    profileViewModel: ProfileViewModel // Vuelve a aceptar el ViewModel
) {
    // Si no hay token, avisamos y NO llamamos a la API
    if (token.isNullOrBlank()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BlueLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No se encontró token.\nVuelve a iniciar sesión.",
                color = Color(0xFF21409A),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    // Llamamos al backend cada vez que cambie el token
    LaunchedEffect(token) {
        profileViewModel.fetchProfileData(token)
    }

    val uiState by profileViewModel.uiState.collectAsState()

    when (uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueLight),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ProfileUiState.Error -> {
            val message = (uiState as ProfileUiState.Error).message
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueLight)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error al cargar el perfil:\n$message",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }

        is ProfileUiState.Success -> {
            // Obtenemos los datos del ViewModel una vez que el estado es Success
            val avatarRes by profileViewModel.selectedAvatarResId
            val username by profileViewModel.username
            val bio by profileViewModel.bio

            // 👇 El mismo UI que ya tenías, ahora dentro del caso Success
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlueLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // -------- TARJETA PRINCIPAL DEL PERFIL --------
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 40.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF6D8EF4)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            // Foto + indicador de estado
                            Box(
                                modifier = Modifier.size(130.dp),
                                contentAlignment = Alignment.TopEnd
                            ) {
                                Image(
                                    painter = painterResource(id = avatarRes),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(130.dp)
                                        .clip(RoundedCornerShape(32.dp))
                                )

                                Box(
                                    modifier = Modifier
                                        .offset(x = (-6).dp, y = 6.dp)
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF4CD964))
                                )
                            }

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = username,          // ← viene del ViewModel
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "ricardo30@gmail.com", // si luego quieres, también lo pasamos al VM
                                fontSize = 14.sp,
                                color = Color(0xFFE5EBFF)
                            )

                            Spacer(Modifier.height(24.dp))

                            Divider(
                                color = Color(0xFFE5EBFF).copy(alpha = 0.6f),
                                thickness = 1.dp,
                                modifier = Modifier.fillMaxWidth(0.9f)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                text = bio,             // ← viene del ViewModel
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    Spacer(Modifier.height(32.dp))

                    // -------- OPCIONES --------
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        ProfileOptionCard(
                            title = "Configuración",
                            subtitle = "Preferencias de la cuenta",
                            icon = Icons.Default.Settings
                        ) {
                            navController.navigate(Route.Settings.route)
                        }

                        ProfileOptionCard(
                            title = "Cambiar foto de perfil",
                            subtitle = "Actualiza tu imagen",
                            icon = Icons.Default.Edit
                        ) {
                            navController.navigate(Route.ProfileEditPhoto.route)
                        }

                        ProfileOptionCard(
                            title = "Notificaciones",
                            subtitle = "Sonido, vibración y recordatorios",
                            icon = Icons.Default.Notifications
                        ) {
                            //navController.navigate(Route.ProfileNotifications.route)
                        }

                        ProfileOptionCard(
                            title = "Privacidad y seguridad",
                            subtitle = "Contraseña, sesión y datos",
                            icon = Icons.Default.Lock
                        ) {
                            //navController.navigate(Route.ProfilePrivacy.route)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF21409A)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF21409A)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFE5EBFF)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    Template2025Theme {
        val nav = rememberNavController()
        // La preview ahora solo puede mostrar el estado sin token,
        // ya que no tiene acceso al ViewModel real ni a la capa de red.
        ProfileScreen(navController = nav, token = null, profileViewModel = viewModel()) // Error esperado en preview
    }
}
