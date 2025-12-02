package com.example.template2025.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.dataStore.DataStore
import com.example.template2025.dataStore.TokenStore
import com.example.template2025.navigation.Route
import com.example.template2025.screens.*
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme
import com.example.template2025.viewModel.ProfileViewModel
import com.example.template2025.viewModel.ProfileViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onLogoutClick: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Token desde DataStore
    val tokenFlow = remember { TokenStore.tokenFlow(context) }
    val token by tokenFlow.collectAsState(initial = null)

    val safeToken: String? = token

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF21409A)),
                drawerContainerColor = Color(0xFF21409A),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Column {
                        DrawerTextItem("Home", Icons.Filled.Home) {
                            nav.navigate(Route.Home.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerTextItem("Módulos", Icons.Filled.Menu) {
                            nav.navigate(Route.Modules.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerTextItem("Misiones Diarias", Icons.Filled.Settings) {
                            nav.navigate(Route.DailyQuests.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerTextItem("Diccionario", Icons.Filled.Menu) {
                            nav.navigate(Route.Diccionario.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerTextItem("Mi Cuenta", Icons.Filled.Person) {
                            nav.navigate(Route.ProfileGraph.route) // Navega al grafo de perfil
                            scope.launch { drawerState.close() }
                        }
                    }

                    DrawerTextItem(
                        label = "Cerrar sesión",
                        icon = Icons.Outlined.Logout,
                        bold = true
                    ) {
                        scope.launch {
                            TokenStore.clear(context)
                            DataStore.setLoggedIn(context, false)
                            drawerState.close()
                        }
                        onLogoutClick()
                        onNavigateToAuth()
                    }
                }
            }
        }
    ) {

        Scaffold(
            containerColor = BlueLight,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text("Template App", color = Color.White)
                            Text(
                                text = if (!safeToken.isNullOrBlank())
                                    "Token: ${safeToken.take(12)}..."
                                else "Sin token",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF21409A)
                    )
                )
            }
        ) { innerPadding ->

            NavHost(
                navController = nav,
                startDestination = Route.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {

                // HOME
                composable(Route.Home.route) {
                    HomeScreen(navController = nav, token = safeToken)
                }

                // GRAFO DE PERFIL (Nuevo)
                profileGraph(navController = nav, token = safeToken)

                // AJUSTES
                composable(Route.Settings.route) {
                    SettingsScreen(
                        navController = nav,
                        token = safeToken
                    )
                }

                // MÓDULOS
                composable(Route.Modules.route) {
                    ModulesScreen(navController = nav)
                }

                // ABECEDARIO
                composable(Route.Abecedario.route) {
                    AbecedarioScreen(
                        letter = "B",
                        mainImage = R.drawable.btn_abecedario_continuar,
                        onPrev = {},
                        onNext = {},
                    )
                }

                // DICCIONARIO
                composable(Route.Diccionario.route) {
                    BuscadorDiccionarioRoute(
                        words = listOf("Casa", "Perro", "Gato", "Comida", "Trabajo"),
                        onWordClick = { word ->
                            nav.navigate(Route.DiccionarioWord.createRoute(word))
                        }
                    )
                }

                composable(
                    route = Route.DiccionarioWord.route,
                    arguments = listOf(navArgument("word") { type = NavType.StringType })
                ) {
                    val word = it.arguments?.getString("word") ?: "Palabra"
                    PalabraDiccionarioScreen(
                        word = word,
                        imageRes = R.drawable.btn_abecedario_continuar,
                        onBack = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}

// Función para el grafo de navegación de perfil
fun NavGraphBuilder.profileGraph(navController: NavHostController, token: String?) {
    navigation(startDestination = Route.Profile.route, route = Route.ProfileGraph.route) {

        composable(Route.Profile.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Route.ProfileGraph.route)
            }
            val apiService = remember { ApiService.RetrofitClient.apiService }
            val profileViewModel: ProfileViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = ProfileViewModelFactory(apiService)
            )
            
            ProfileScreen(
                navController = navController,
                token = token,
                profileViewModel = profileViewModel // Pasamos el VM compartido
            )
        }
        
        composable(Route.ProfileEditPhoto.route) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Route.ProfileGraph.route)
            }
            val apiService = remember { ApiService.RetrofitClient.apiService }
            val profileViewModel: ProfileViewModel = viewModel(
                viewModelStoreOwner = parentEntry,
                factory = ProfileViewModelFactory(apiService)
            )

            EditPhotoScreen(
                profileViewModel = profileViewModel, // Pasamos la instancia compartida
                onBack = { navController.popBackStack() }
            )
        }
        // Aquí podrías añadir otras pantallas que compartan el mismo ViewModel
        // composable(Route.ProfileNotifications.route) { ... }
    }
}

@Composable
fun DrawerTextItem(
    label: String,
    icon: ImageVector,
    isDanger: Boolean = false,
    bold: Boolean = false,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val bg = if (pressed) Color(0x1AFFFFFF) else Color.Transparent
    val textColor = if (isDanger) Color(0xFFE53935) else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = textColor)
            Spacer(Modifier.width(14.dp))
            Text(
                label,
                color = textColor,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            )
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            color = Color.White.copy(alpha = 0.25f),
            thickness = 1.dp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScaffoldPreview() {
    Template2025Theme {
        MainScaffold(onLogoutClick = {}, onNavigateToAuth = {})
    }
}
