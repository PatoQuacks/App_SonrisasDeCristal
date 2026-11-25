package mx.tec.app_sonrisas.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.remote.Usuario
import mx.tec.app_sonrisas.navigation.Pantalla
import mx.tec.app_sonrisas.navigation.obtenerMenuLateral
import mx.tec.app_sonrisas.navigation.pantallaConfiguracion

// Navigation drawer, de arriba hacia abajo:
//   - Encabezado con el usuario que inició sesión (al tocarlo se abre su perfil).
//   - Las secciones que le tocan según su nivel (obtenerMenuLateral en PermisosUsuario.kt).
//   - Configuración y Acerca de, separadas al final.
//   - "Cerrar sesión", siempre visible al final.
// Las pantallas secundarias (agregar empleado, agregar sala, permisos...) ya no salen
// aquí: se abren desde su sección. Ver seccionPadre en Rutas.kt.
@Composable
fun MenuLateral(
    usuario: Usuario?,
    rutaSeccion: String?,
    onSeleccionar: (Pantalla) -> Unit,
    onVerPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
) {
    ModalDrawerSheet {
        // Encabezado: el usuario que regresó GET /perfil.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClickLabel = "Ver perfil", onClick = onVerPerfil)
                .padding(24.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = usuario?.correo ?: "Sin sesión",
                style = MaterialTheme.typography.titleMedium,
            )
            if (usuario != null) {
                Text(
                    text = "Rol: ${usuario.rol}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider()

        // Las opciones se pueden desplazar si la pantalla es chica;
        // "Cerrar sesión" se queda fijo abajo.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
        ) {
            // Solo las secciones que le tocan al usuario según su nivel (PermisosUsuario.kt).
            // Configuración y Acerca de van al final, separadas de las demás.
            val opciones = obtenerMenuLateral(usuario)
            val alFinal = setOf(pantallaConfiguracion, Pantalla.AcercaDe)
            val principales = opciones.filterNot { it in alFinal }
            principales.forEach { pantalla ->
                OpcionMenu(pantalla, rutaSeccion, onSeleccionar)
            }
            if (principales.isNotEmpty()) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp))
            }
            opciones.filter { it in alFinal }.forEach { pantalla ->
                OpcionMenu(pantalla, rutaSeccion, onSeleccionar)
            }
        }

        HorizontalDivider()
        NavigationDrawerItem(
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = onCerrarSesion,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
        )
        Spacer(Modifier.height(12.dp))
    }
}

// Una opción del menú. Se marca si es la sección actual
// (también cuando se está en una pantalla de esa sección, como "Agregar empleado").
@Composable
private fun OpcionMenu(
    pantalla: Pantalla,
    rutaSeccion: String?,
    onSeleccionar: (Pantalla) -> Unit,
) {
    NavigationDrawerItem(
        icon = { Icon(pantalla.icono, contentDescription = null) },
        label = { Text(pantalla.titulo) },
        selected = rutaSeccion == pantalla.route,
        onClick = { onSeleccionar(pantalla) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
    )
}
