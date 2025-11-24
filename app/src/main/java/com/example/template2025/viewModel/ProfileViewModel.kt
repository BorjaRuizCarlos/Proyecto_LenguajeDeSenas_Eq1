package com.example.template2025.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.template2025.R

class ProfileViewModel : ViewModel() {

    // ---------- Avatar ----------
    private val _selectedAvatarResId = mutableStateOf<Int>(R.drawable.ic_launcher_foreground)
    val selectedAvatarResId: State<Int> = _selectedAvatarResId

    fun updateAvatar(resId: Int) {
        _selectedAvatarResId.value = resId
    }

    // ---------- Username ----------
    private val _username = mutableStateOf("Ricardo")
    val username: State<String> = _username

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    // ---------- Descripción / bio ----------
    private val _bio = mutableStateOf(
        "This is my bio and thank you for taking time to read it as it means a lot to me."
    )
    val bio: State<String> = _bio

    fun updateBio(newBio: String) {
        _bio.value = newBio
    }
}
