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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.template2025.R
import com.example.template2025.data.api.ApiService
import com.example.template2025.dataStore.DataStore
import com.example.template2025.dataStore.TokenStore
import com.example.template2025.navigation.Route
import com.example.template2025.screens.*
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.MissionUi
import com.example.template2025.ui.theme.Template2025Theme
import com.example.template2025.viewModel.DailyMissionsUiState
import com.example.template2025.viewModel.DailyMissionsViewModel
import com.example.template2025.viewModel.DailyMissionsViewModelFactory
import com.example.template2025.viewModel.ProfileViewModel
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
                            nav.navigate(Route.Profile.route)
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

                // PERFIL
                composable(Route.Profile.route) {
                    ProfileScreen(
                        navController = nav,
                        token = safeToken
                    )
                }

                // 🔹 MISIONS DIARIAS – AQUÍ VA EL NUEVO CÓDIGO
                composable(Route.DailyQuests.route) {

                    val token = safeToken

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
                    } else {
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
                }
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
                        onWordClick = { wordId ->
                            nav.navigate(Route.DiccionarioWord.createRoute(wordId))
                        },
                        token = safeToken   // 👈 importantísimo
                    )
                }

                composable(
                    route = Route.DiccionarioWord.route,
                    arguments = listOf(navArgument("wordId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val wordId = backStackEntry.arguments?.getInt("wordId") ?: 0
                    PalabraDiccionarioRoute(
                        wordId = wordId,
                        token = safeToken,              // 👈 le pasas el token
                        onBack = { nav.popBackStack() }
                    )
                }
                // ⭐ NUEVA ROUTE: InsideModule
                composable(
                    route = Route.InsideModule.route,
                    arguments = listOf(
                        navArgument("moduleId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val moduleId = backStackEntry.arguments?.getInt("moduleId")
                    InsideModulesScreen(
                        navController = nav,
                        moduleId = moduleId
                    )
                }

                // ⭐ NUEVA ROUTE: LessonsContent
                composable(
                    route = Route.LessonsContent.route,
                    arguments = listOf(
                        navArgument("moduleId") { type = NavType.IntType },
                        navArgument("lessonId") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val moduleId = backStackEntry.arguments?.getInt("moduleId")
                    val lessonId = backStackEntry.arguments?.getInt("lessonId")

                    LessonsContentScreen(
                        navController = nav,
                        moduleId = moduleId,
                        lessonId = lessonId
                    )
                }
            }
            }
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
