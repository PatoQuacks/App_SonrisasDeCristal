package mx.tec.app_sonrisas.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
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
//   2. Si es una sección principal, agrégala a la barra inferior o al menú lateral.
//      Si se abre desde otra pantalla, agrégala a seccionPadre con su pantalla "padre".
//   3. Regístrala en PrincipalNavHost.kt con composable(...).
// ------------------------------------------------------------------
sealed class Pantalla(val route: String, val titulo: String, val icono: ImageVector) {

    // Barra inferior (bottom navigation)
    data object Inicio : Pantalla("inicio", "Inicio", Icons.Filled.Home)
    data object Asistencia : Pantalla("asistencia", "Asistencia", Icons.Filled.CheckCircle)
    data object Capacitaciones : Pantalla("capacitaciones", "Capacitaciones", Icons.Filled.DateRange)
    data object Incidencias : Pantalla("incidencias", "Incidencias", Icons.Filled.Warning)

    // Se abren desde Asistencia y Capacitaciones.
    data object Salidas : Pantalla("salidas", "Registrar salidas", Icons.Filled.CheckCircle)
    data object AgregarCapacitacion : Pantalla("agregar_capacitacion", "Agregar capacitación", Icons.Filled.DateRange)

    // Menú lateral (navigation drawer)
    data object Empleados : Pantalla("empleados", "Empleados", Icons.Filled.AccountBox)
    data object Coordinadores : Pantalla("coordinadores", "Coordinadores", Icons.Filled.Face)
    data object Salas : Pantalla("salas", "Salas", Icons.Filled.LocationOn)
    data object Configuracion : Pantalla("configuracion", "Configuración", Icons.Filled.Settings)

    // Se abren desde una sección del menú lateral (ver seccionPadre).
    data object AgregarEmpleado : Pantalla("agregar_empleados", "Agregar empleado", Icons.Filled.AccountBox)
    data object PermisosCoordinadores : Pantalla("permisos_coordinadores", "Permisos del coordinador", Icons.Filled.Face)
    data object AgregarSala : Pantalla("agregar_sala", "Agregar sala", Icons.Filled.LocationOn)
    data object Perfil : Pantalla("perfil", "Perfil", Icons.Filled.Person)
    data object AcercaDe : Pantalla("acerca_de", "Acerca de", Icons.Filled.Info)

    // Director
    data object ReporteDirector : Pantalla("reporte_director", "Reportes", Icons.AutoMirrored.Filled.List)
    data object CoberturaDirector : Pantalla("cobertura_director", "Cobertura", Icons.Filled.Home)
}

// Material Design recomienda de 3 a 5 opciones en la barra inferior.
val pantallasBarraInferior = listOf(
    Pantalla.Inicio,
    Pantalla.Asistencia,
    Pantalla.Capacitaciones,
    Pantalla.Incidencias,
)

// Menú lateral: solo las secciones principales.
val pantallasMenuLateral = listOf(
    Pantalla.Empleados,
    Pantalla.Coordinadores,
    Pantalla.Salas,
)

// Vistas de director: salen en su propio grupo del menú lateral.
val pantallasDirector = listOf(
    Pantalla.ReporteDirector,
    Pantalla.CoberturaDirector,
)

// Va al final del menú lateral, separada de las demás.
val pantallaConfiguracion = Pantalla.Configuracion

// Pantallas que viven DENTRO de otra: ruta -> su pantalla "padre".
// No salen en el menú; con esto la barra superior muestra la flecha para regresar
// y el menú (o la barra inferior) marca la sección padre.
val seccionPadre: Map<String, Pantalla> = mapOf(
    Pantalla.AgregarEmpleado.route to Pantalla.Empleados,
    Pantalla.PermisosCoordinadores.route to Pantalla.Coordinadores,
    Pantalla.AgregarSala.route to Pantalla.Salas,
    Pantalla.Perfil.route to Pantalla.Configuracion,
    Pantalla.AcercaDe.route to Pantalla.Configuracion,
    Pantalla.Salidas.route to Pantalla.Asistencia,
    Pantalla.AgregarCapacitacion.route to Pantalla.Capacitaciones,
)

// Se usa para encontrar el título de la pantalla actual.
val todasLasPantallas = pantallasBarraInferior + pantallasMenuLateral + pantallasDirector +
    listOf(
        Pantalla.Configuracion,
        Pantalla.AgregarEmpleado,
        Pantalla.PermisosCoordinadores,
        Pantalla.AgregarSala,
        Pantalla.Perfil,
        Pantalla.AcercaDe,
        Pantalla.Salidas,
        Pantalla.AgregarCapacitacion,
    )
