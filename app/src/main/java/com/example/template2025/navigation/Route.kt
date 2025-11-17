package com.example.template2025.navigation

sealed class Route(val route: String) {
    data object Splash : Route("splash")
    data object Auth : Route("auth")
    data object Main : Route("main")

    // Auth internas
    data object Login : Route("login")
    data object Register : Route("register")

    // Main internas
    data object Home : Route("home")
    data object Profile : Route("profile")
    data object Settings : Route("settings")
    data object Modules : Route("modules")

    // Ruta para ver el contenido de un módulo. Acepta un argumento "moduleId".
    data object InsideModule : Route("inside_module/{moduleId}") {
        fun createRoute(moduleId: Int) = "inside_module/$moduleId"
    }
    data object DailyQuests : Route("dailyquests")
}
