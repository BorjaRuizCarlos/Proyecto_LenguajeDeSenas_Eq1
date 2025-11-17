package com.example.template2025.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.template2025.navigation.Route
import com.example.template2025.screens.DailyQuestsScreen
import com.example.template2025.screens.HomeScreen
import com.example.template2025.screens.ModulesScreen

import com.example.template2025.screens.ProfileScreen
import com.example.template2025.screens.SettingsScreen
import com.example.template2025.ui.theme.Template2025Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


data class BottomItem(val route: String, val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(onLogoutClick: () -> Unit, onNavigateToAuth: () -> Unit) {
    val nav = rememberNavController()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {

            // ... código anterior ...
            ModalDrawerSheet(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight()
                    .background(Color(0xFF21409A)),
                drawerContainerColor = Color(0xFF21409A),
            ) {
                // 1. REEMPLAZA EL BOX POR UN COLUMN
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp, bottom = 16.dp), // Añade padding inferior
                    horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente los elementos
                    verticalArrangement = Arrangement.SpaceBetween // ⬅️ IMPORTANTE: Esto empuja el último elemento al final
                ) {
                    // 2. Columna para los ítems de navegación superiores
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        DrawerItem(nav, "Home", Route.Home.route, drawerState, scope)
                        DrawerItem(nav, "Módulos", Route.Modules.route, drawerState, scope)
                        DrawerItem(nav, "Misiones Diarias", Route.DailyQuests.route, drawerState, scope)
                        DrawerItem(nav, "Mi Cuenta", Route.Profile.route, drawerState, scope)
                        NavigationDrawerItem(
                            label = {

                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Cerrar sesión",
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },

                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onLogoutClick()
                                onNavigateToAuth()
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 14.dp)
                                .background(
                                    color = Color(0xFF8BA7D7),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .height(46.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFF8BA7D7),
                                unselectedContainerColor = Color(0xFF8BA7D7),
                                selectedTextColor = Color.White,
                                unselectedTextColor = Color.White,
                                selectedIconColor = Color.White,
                                unselectedIconColor = Color.White
                            )
                        )
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF21409A)
                    )
                )

            },


        ) { innerPadding ->
            NavHost(navController = nav, startDestination = Route.Home.route, modifier = Modifier.padding(innerPadding)) {
                composable(Route.Home.route)     { HomeScreen() }
                composable(Route.Profile.route)  { ProfileScreen() }
                composable(Route.Settings.route) { SettingsScreen() }
                composable(Route.Modules.route) { ModulesScreen(navController = nav) } // Corregido
                composable(Route.DailyQuests.route) { DailyQuestsScreen() }
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
fun DrawerItem(
    navController: NavController,
    label: String,
    dest: String,
    drawerState: DrawerState,
    scope: CoroutineScope
) {

    NavigationDrawerItem(
        label = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        },
        selected = false, // siempre falso para no usar estilo default
        onClick = {
            navController.navigate(dest)
            scope.launch { drawerState.close() }
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(vertical = 14.dp)
            .background(
                color = Color(0xFF8BA7D7),        // azul claro pastilla
                shape = RoundedCornerShape(20.dp) // redondeado
            )
            .height(46.dp),
        shape = RoundedCornerShape(20.dp),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = Color(0xFF8BA7D7),
            unselectedContainerColor = Color(0xFF8BA7D7),
            selectedTextColor = Color.White,
            unselectedTextColor = Color.White,
            selectedIconColor = Color.White,
            unselectedIconColor = Color.White
        )
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScaffoldPreview() {
    Template2025Theme {
        MainScaffold(onLogoutClick = {}, onNavigateToAuth = {})
    }
}
