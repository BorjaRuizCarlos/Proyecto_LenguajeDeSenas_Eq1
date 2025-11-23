@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.template2025.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.template2025.ui.theme.BlueDark
import com.example.template2025.ui.theme.BlueLight
import com.example.template2025.ui.theme.Template2025Theme

/* ---------------------------------------------------------------- */
/* -----------------   ROUTE CON ESTADO   -------------------------- */
/* ---------------------------------------------------------------- */

@Composable
fun BuscadorDiccionarioRoute(
    words: List<String>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    BuscadorDiccionarioScreen(
        words = words,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        onWordClick = onWordClick,
        modifier = modifier
    )
}

/* ---------------------------------------------------------------- */
/* -----------------   PANTALLA PRINCIPAL   ------------------------ */
/* ---------------------------------------------------------------- */

@Composable
fun BuscadorDiccionarioScreen(
    words: List<String>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BlueLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp) // pequeño margen superior bajo la barra global
        ) {
            // --------- BUSCADOR ---------
            SearchBarDiccionario(
                query = searchQuery,
                onQueryChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --------- GRID DE PALABRAS ---------
            val filteredWords = words.filter { word ->
                word.contains(searchQuery, ignoreCase = true)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredWords) { word ->
                        WordCard(
                            text = word,
                            onClick = { onWordClick(word) }
                        )
                    }
                }
            }
        }
    }
}

/* ---------------------------------------------------------------- */
/* ------------------------   COMPONENTES   ------------------------ */
/* ---------------------------------------------------------------- */

@Composable
fun SearchBarDiccionario(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.height(52.dp),
        placeholder = {
            Text(
                text = "Search",
                fontSize = 16.sp,
                color = Color.Gray
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = Color.Gray
            )
        },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = BlueDark,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
fun WordCard(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(4f / 3f)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF9FBFF)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF30313A)
            )
        }
    }
}

/* ---------------------------------------------------------------- */
/* ---------------------------   PREVIEW   -------------------------- */
/* ---------------------------------------------------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewBuscadorDiccionario() {
    Template2025Theme {
        BuscadorDiccionarioRoute(
            words = listOf(
                "Casa", "Perro", "Gato", "Comida", "Escuela", "Libro", "Mesa",
                "Familia", "Trabajo", "Amigo", "Agua", "Juego", "Ropa"
            ),
            onWordClick = {}
        )
    }
}
