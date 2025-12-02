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

    // Módulos
    data object InsideModule : Route("inside_module/{moduleId}") {
        fun createRoute(moduleId: Int) = "inside_module/$moduleId"
    }

    data object DailyQuests : Route("dailyquests")
    data object Abecedario : Route("abecedario")
    data object Diccionario : Route("diccionario")

    data object DiccionarioWord : Route("diccionario_word/{word}") {
        fun createRoute(word: String) = "diccionario_word/$word"
    }

    // Lecciones
    data object LessonPractice : Route("lesson_practice/{moduleId}/{lessonId}") {
        fun createRoute(moduleId: Int, lessonId: Int) =
            "lesson_practice/$moduleId/$lessonId"
    }

    data object LessonQuestion : Route("lesson_question/{moduleId}/{lessonId}") {
        fun createRoute(moduleId: Int, lessonId: Int) =
            "lesson_question/$moduleId/$lessonId"
    }

    // Rutas de opciones de perfil
    data object ProfileEditPhoto : Route("profile_edit_photo")
    data object ProfileNotifications : Route("profile_notifications")
    data object ProfilePrivacy : Route("profile_privacy")

    //Ruta de Lessons content
    object LessonsContent : Route("lessonsContent/{moduleId}/{lessonId}") {
        fun createRoute(moduleId: Int, lessonId: Int) = "lessonsContent/$moduleId/$lessonId"
    }
}
