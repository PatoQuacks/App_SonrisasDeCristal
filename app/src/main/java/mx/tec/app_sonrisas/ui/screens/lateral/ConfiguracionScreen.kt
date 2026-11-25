package mx.tec.app_sonrisas.ui.screens.lateral

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mx.tec.app_sonrisas.navigation.Pantalla
import mx.tec.app_sonrisas.ui.components.TituloSeccion

// Configuración agrupa las pantallas secundarias que antes estaban sueltas en el menú lateral.
// Aquí se pueden agregar más opciones de la app conforme se necesiten.
@Composable
fun ConfiguracionScreen(
    onVerPerfil: () -> Unit,
    onVerAcercaDe: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        TituloSeccion("Cuenta")
        OpcionConfiguracion(
            pantalla = Pantalla.Perfil,
            descripcion = "Correo y rol de la sesión actual",
            onClick = onVerPerfil,
        )

        TituloSeccion("Aplicación")
        OpcionConfiguracion(
            pantalla = Pantalla.AcercaDe,
            descripcion = "Equipo y versión de la app",
            onClick = onVerAcercaDe,
        )
    }
}

@Composable
private fun OpcionConfiguracion(
    pantalla: Pantalla,
    descripcion: String,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(pantalla.titulo) },
        supportingContent = { Text(descripcion) },
        leadingContent = { Icon(pantalla.icono, contentDescription = null) },
        trailingContent = {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}
