package com.example.template2025.ui.theme

import androidx.annotation.DrawableRes

data class MissionUi(
    val title: String,
    val current: Int,
    val total: Int,
    @DrawableRes val characterIcon: Int
)
