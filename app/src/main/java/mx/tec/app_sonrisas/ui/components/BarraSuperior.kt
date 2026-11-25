package mx.tec.app_sonrisas.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

// TopAppBar todavía está marcada como experimental en Material 3,
// por eso se necesita @OptIn.
// Si onRegresar no es null (pantallas que se abren desde otra), se muestra la flecha
// para regresar en lugar del botón del menú.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(
    titulo: String,
    onAbrirMenu: () -> Unit,
    onRegresar: (() -> Unit)? = null,
    acciones: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(titulo) },
        navigationIcon = {
            if (onRegresar != null) {
                IconButton(onClick = onRegresar) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                }
            } else {
                IconButton(onClick = onAbrirMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
            navigationIconContentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    )
}
