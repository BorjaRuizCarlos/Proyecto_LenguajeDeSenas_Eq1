package com.example.template2025.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.platform.LocalContext
import com.example.template2025.R
import com.example.template2025.navigation.Route
import com.example.template2025.screens.*
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.MissionUi
import com.example.template2025.ui.theme.Template2025Theme
import com.example.template2025.viewModel.ProfileViewModel
import com.example.template2025.dataStore.TokenStore
import com.example.template2025.dataStore.DataStore
import kotlinx.coroutines.launch

// 👇 CompositionLocal para compartir el token en toda la app
val LocalAuthToken = staticCompositionLocalOf<String?> { null }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onLogoutClick: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // ViewModel compartido para perfil (foto, username, bio)
    val profileViewModel: ProfileViewModel = viewModel()

    // 👇 Obtenemos token desde DataStore
    val context = LocalContext.current
    val token by TokenStore.tokenFlow(context).collectAsState(initial = null)

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
                            // 👇 Limpiamos token y flag de login
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
        // 👇 Inyectamos el token a todo el árbol de composables
        CompositionLocalProvider(LocalAuthToken provides token) {
            Scaffold(
                containerColor = BlueLight,
                topBar = {
                    TopAppBar(
                        title = {
                            Column {
                                Text("Template App", color = Color.White)
                                // 👇 Mostrar token recortado en barra (opcional)
                                if (!token.isNullOrBlank()) {
                                    Text(
                                        text = "Token: ${token!!.take(12)}...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                }
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
                    // --------- principales ---------
                    composable(Route.Home.route) {
                        HomeScreen(navController = nav)
                    }
                    composable(Route.Profile.route) {
                        ProfileScreen(
                            navController = nav,
                            profileViewModel = profileViewModel
                        )
                    }
                    composable(Route.Settings.route) {
                        SettingsScreen(
                            profileViewModel = profileViewModel,
                            onBack = { nav.popBackStack() }
                        )
                    }
                    composable(Route.Modules.route) {
                        ModulesScreen(navController = nav)
                    }

                    // --------- abecedario / misiones ---------
                    composable(Route.Abecedario.route) {
                        AbecedarioScreen(
                            letter = "B",
                            mainImage = R.drawable.btn_abecedario_continuar,
                            onPrev = {},
                            onNext = {},
                        )
                    }

                    composable(Route.DailyQuests.route) {
                        MisionesDiariasScreen(
                            missions = listOf(
                                MissionUi("Gana 50 XP", 43, 50, R.drawable.ic_mision_xp),
                                MissionUi("Completa 2 lecciones", 1, 2, R.drawable.ic_mision_lecciones),
                                MissionUi("Termina un módulo", 43, 50, R.drawable.ic_mision_modulo)
                            )
                        )
                    }

                    // --------- módulos ---------
                    composable(
                        route = Route.InsideModule.route,
                        arguments = listOf(navArgument("moduleId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val moduleId = backStackEntry.arguments?.getInt("moduleId")
                        InsideModulesScreen(navController = nav, moduleId = moduleId)
                    }

                    // --------- diccionario ---------
                    composable(Route.Diccionario.route) {
                        BuscadorDiccionarioRoute(
                            words = listOf(
                                "Casa", "Perro", "Gato", "Comida", "Escuela", "Libro", "Mesa",
                                "Familia", "Trabajo", "Amigo", "Agua", "Juego", "Ropa"
                            ),
                            onWordClick = { word ->
                                nav.navigate(Route.DiccionarioWord.createRoute(word))
                            }
                        )
                    }

                    composable(
                        route = Route.DiccionarioWord.route,
                        arguments = listOf(navArgument("word") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val word = backStackEntry.arguments?.getString("word") ?: "Palabra"

                        PalabraDiccionarioScreen(
                            word = word,
                            imageRes = R.drawable.btn_abecedario_continuar,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    // --------- lecciones ---------
                    composable(
                        route = Route.LessonPractice.route,
                        arguments = listOf(
                            navArgument("moduleId") { type = NavType.IntType },
                            navArgument("lessonId") { type = NavType.IntType }
                        )
                    ) { backStackEntry ->
                        val moduleId = backStackEntry.arguments?.getInt("moduleId") ?: 0
                        val lessonId = backStackEntry.arguments?.getInt("lessonId") ?: 0

                        PracticaLetraScreen(
                            titulo = "Palabra",
                            imageRes = R.drawable.btn_abecedario_continuar,
                            onContinuar = {
                                nav.navigate(
                                    Route.LessonQuestion.createRoute(
                                        moduleId = moduleId,
                                        lessonId = lessonId
                                    )
                                )
                            }
                        )
                    }

                    composable(
                        route = Route.LessonQuestion.route,
                        arguments = listOf(
                            navArgument("moduleId") { type = NavType.IntType },
                            navArgument("lessonId") { type = NavType.IntType }
                        )
                    ) {
                        PreguntaLeccionScreen(
                            pregunta = "¿Cuál de estas opciones es la correcta?",
                            respuestas = listOf("Respuesta 1", "Respuesta 2", "Respuesta 3", "Respuesta 4"),
                            imageRes = R.drawable.btn_abecedario_continuar,
                            onRespuestaClick = { },
                            onContinuar = {
                                nav.popBackStack(Route.InsideModule.route, inclusive = false)
                            }
                        )
                    }

                    // --------- opciones de perfil ---------
                    composable(Route.ProfileEditPhoto.route) {
                        EditPhotoScreen(
                            profileViewModel = profileViewModel,
                            onBack = { nav.popBackStack() }
                        )
                    }

                    composable(Route.ProfileNotifications.route) {
                        NotificationsSettingsScreen(onBack = { nav.popBackStack() })
                    }

                    composable(Route.ProfilePrivacy.route) {
                        PrivacySettingsScreen(onBack = { nav.popBackStack() })
                    }
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
