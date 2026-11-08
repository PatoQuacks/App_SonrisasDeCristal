package mx.tec.app_sonrisas.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import mx.tec.app_sonrisas.data.remote.Usuario
import mx.tec.app_sonrisas.ui.screens.inferior.AsistenciaScreen
import mx.tec.app_sonrisas.ui.screens.inferior.EmpleadosScreen
import mx.tec.app_sonrisas.ui.screens.inferior.IncidenciasScreen
import mx.tec.app_sonrisas.ui.screens.inferior.InicioScreen
import mx.tec.app_sonrisas.ui.screens.inferior.PerfilScreen
import mx.tec.app_sonrisas.ui.screens.lateral.AcercaDeScreen
import mx.tec.app_sonrisas.ui.screens.lateral.AgregarEmpleadoScreen
import mx.tec.app_sonrisas.ui.screens.lateral.AgregarSalaScreen
import mx.tec.app_sonrisas.ui.screens.lateral.CapacitacionesScreen
import mx.tec.app_sonrisas.ui.screens.lateral.ConfiguracionScreen
import mx.tec.app_sonrisas.ui.screens.lateral.CoordinadoresScreen
import mx.tec.app_sonrisas.ui.screens.lateral.PermisosCoordinadoresScreen
import mx.tec.app_sonrisas.ui.screens.lateral.SalasScreen

// Navegación de NIVEL 2: las pantallas que viven dentro de la parte principal.
// Este NavHost se dibuja en medio del Scaffold, entre la barra superior y la inferior.
@Composable
fun PrincipalNavHost(
    navController: NavHostController,
    usuario: Usuario?,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.route,
        modifier = modifier,
    ) {
        // Barra inferior
        composable(Pantalla.Inicio.route) { InicioScreen() }
        composable(Pantalla.Asistencia.route) { AsistenciaScreen() }
        composable(Pantalla.Capacitaciones.route) { CapacitacionesScreen() }
        composable(Pantalla.Incidencias.route) { IncidenciasScreen() }

        // Menú lateral
        composable(Pantalla.Empleados.route) { EmpleadosScreen() }
        composable(Pantalla.Coordinadores.route) { CoordinadoresScreen() }
        composable(Pantalla.Salas.route) { SalasScreen() }
        composable(Pantalla.PermisosCoordinadores.route) { PermisosCoordinadoresScreen() }
        composable(Pantalla.AgregarEmpleado.route) { AgregarEmpleadoScreen() }
        composable(Pantalla.AgregarSala.route) { AgregarSalaScreen() }
        composable(Pantalla.Configuracion.route) { ConfiguracionScreen() }
        composable(Pantalla.AcercaDe.route) { AcercaDeScreen() }
        composable(Pantalla.Perfil.route) { PerfilScreen(usuario = usuario) }
    }
}
