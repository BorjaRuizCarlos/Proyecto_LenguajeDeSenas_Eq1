package com.example.template2025.composables

import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ripple
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.template2025.R
import com.example.template2025.navigation.Route
import com.example.template2025.screens.*
import com.example.template2025.ui.theme.MissionUi
import com.example.template2025.ui.theme.Template2025Theme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onLogoutClick: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val nav = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                            nav.navigate(Route.Abecedario.route)
                            scope.launch { drawerState.close() }
                        }
                        DrawerTextItem("Mi Cuenta", Icons.Filled.Person) {
                            nav.navigate(Route.Profile.route)
                            scope.launch { drawerState.close() }
                        }
                    }
                    DrawerTextItem(
                        label = "Cerrar sesión",
                        icon = Icons.Outlined.Logout, // pon tu ícono de logout si quieres
                        bold = true
                    ) {
                        scope.launch { drawerState.close() }
                        onLogoutClick()
                        onNavigateToAuth()
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Template App", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF21409A))
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = nav,
                startDestination = Route.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Route.Home.route) { HomeScreen(navController = nav) }
                composable(Route.Profile.route) { ProfileScreen() }
                composable(Route.Settings.route) { SettingsScreen() }
                composable(Route.Modules.route) { ModulesScreen(navController = nav) }
                composable(Route.Abecedario.route) {
                    AbecedarioScreen(
                        letter = "B",
                        mainImage = R.drawable.btn_abecedario_continuar,
                        onPrev = {},
                        onNext = {},
                        modifier = Modifier.padding()
                    )
                }
                composable(Route.DailyQuests.route) {
                    MisionesDiariasScreen(
                        missions = listOf(
                            MissionUi("Gana 50 XP", 43, 50, R.drawable.ic_mision_xp),
                            MissionUi("Completa 2 lecciones", 1, 2, R.drawable.ic_mision_lecciones),
                            MissionUi("Termina un modulo", 43, 50, R.drawable.ic_mision_modulo)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun currentRoute(nav: NavHostController): String? {
    val backStackEntry by nav.currentBackStackEntryAsState()
    return backStackEntry?.destination?.route
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
            // clickable sin ripple interno…
            .clickable(
                interactionSource = interaction,
                indication = null
            ) { onClick() }
            // …y agregamos la nueva API de indication para el ripple
            .indication(
                interactionSource = interaction,
                indication = ripple(
                    bounded = true,
                    color = Color.White.copy(alpha = 0.25f)
                )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = textColor)
            Spacer(Modifier.width(14.dp))
            Text(label, color = textColor, style = MaterialTheme.typography.bodyLarge)
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