package mx.tec.app_sonrisas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

// ------------------------------------------------------------------
// NIVEL 1: rutas de la app completa
// La app tiene solo dos "lugares": el login y la parte principal.
// ------------------------------------------------------------------
object RutasApp {
    const val LOGIN = "login"
    const val PRINCIPAL = "principal"
}

// ------------------------------------------------------------------
// NIVEL 2: pantallas dentro de la parte principal
// Cada pantalla guarda su ruta, el título de la barra superior y su ícono.
// Para agregar una pantalla nueva:
//   1. Agrega aquí un data object.
//   2. Agrégalo a una de las listas de abajo (barra inferior o menú lateral).
//   3. Regístralo en PrincipalNavHost.kt con composable(...).
// ------------------------------------------------------------------
sealed class Pantalla(val route: String, val titulo: String, val icono: ImageVector) {

    // Barra inferior (bottom navigation)
    data object Inicio : Pantalla("inicio", "Inicio", Icons.Filled.Home)
    data object Asistencia : Pantalla("asistencia", "Asistencia", Icons.Filled.CheckCircle)
    data object Capacitaciones : Pantalla("capacitaciones", "Capacitaciones", Icons.Filled.DateRange)
    data object Incidencias : Pantalla("incidencias", "Incidencias", Icons.Filled.Warning)

    // Menú lateral (navigation drawer)
    data object Empleados : Pantalla("empleados", "Empleados", Icons.Filled.AccountBox)
    data object AgregarEmpleado : Pantalla("agregar_empleados", "Agregar Empleados", Icons.Filled.AccountBox)
    data object Salas : Pantalla("salas", "Salas", Icons.Filled.LocationOn)
    data object AgregarSala : Pantalla("agregar_sala", "Agregar Sala", Icons.Filled.LocationOn)

    data object Coordinadores : Pantalla("coordinadores", "Coordinadores", Icons.Filled.Face)
    data object PermisosCoordinadores : Pantalla("permisos_coordinadores", "Permisos de Coordinadores", Icons.Filled.Face)
    data object Configuracion : Pantalla("configuracion", "Configuración", Icons.Filled.Settings)
    data object AcercaDe : Pantalla("acerca_de", "Acerca de", Icons.Filled.Info)
    data object Perfil : Pantalla("perfil", "Perfil", Icons.Filled.Person)
}

// Material Design recomienda de 3 a 5 opciones en la barra inferior.
val pantallasBarraInferior = listOf(
    Pantalla.Inicio,
    Pantalla.Asistencia,
    Pantalla.Capacitaciones,
    Pantalla.Incidencias,
)

val pantallasMenuLateral = listOf(
    Pantalla.Empleados,
    Pantalla.Coordinadores,
    Pantalla.Salas,
    Pantalla.PermisosCoordinadores,
    Pantalla.AgregarEmpleado,
    Pantalla.AgregarSala,
    Pantalla.Configuracion,
    Pantalla.AcercaDe,
    Pantalla.Perfil,
)

// Se usa para encontrar el título de la pantalla actual.
val todasLasPantallas = pantallasBarraInferior + pantallasMenuLateral
