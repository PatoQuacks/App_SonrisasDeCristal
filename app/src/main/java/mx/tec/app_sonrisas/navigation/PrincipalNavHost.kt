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
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.app_sonrisas.ui.screens.director.ReporteAsistenciaScreen
import mx.tec.app_sonrisas.ui.screens.director.SucursalesScreen
import mx.tec.app_sonrisas.ui.screens.navegarAPantalla
import mx.tec.app_sonrisas.ui.viewmodel.DirectorViewModel
import mx.tec.app_sonrisas.ui.viewmodel.PersonalViewModel


// Navegación de NIVEL 2: las pantallas que viven dentro de la parte principal.
// Este NavHost se dibuja en medio del Scaffold, entre la barra superior y la inferior.
@Composable
fun PrincipalNavHost(
    navController: NavHostController,
    usuario: Usuario?,
    modifier: Modifier = Modifier,
) {
    val personal: PersonalViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Pantalla.Inicio.route,
        modifier = modifier,
    ) {
        // Barra inferior
        composable(Pantalla.Inicio.route) {
            InicioScreen(
                onVerAusencias = { navController.navegarAPantalla(Pantalla.Asistencia) },
                onVerCapacitaciones = { navController.navegarAPantalla(Pantalla.Capacitaciones) },
            )
        }
        composable(Pantalla.Asistencia.route) {
            AsistenciaScreen(
                empleados = personal.empleados,
                asistenciaGuardada = personal.asistencia,
                onCancelar = { navController.popBackStack() },
                onGuardar = { registros ->
                    personal.guardarAsistencia(registros)
                    navController.popBackStack()
                },
            )
        }
        composable(Pantalla.Capacitaciones.route) { CapacitacionesScreen() }
        composable(Pantalla.Incidencias.route) {
            IncidenciasScreen(
                incidenciasGuardadas = personal.incidencias,
                onCancelar = { navController.popBackStack() },
                onGuardar = personal::guardarIncidencias,
            )
        }

        // Menú lateral
        composable(Pantalla.Empleados.route) {
            EmpleadosScreen(
                empleados = personal.empleados,
                asistencia = personal.asistencia,
            )
        }
        composable(Pantalla.Coordinadores.route) {
            CoordinadoresScreen(
                // navigate "normal": con el botón atrás se regresa a Coordinadores.
                onVerPermisos = {
                    navController.navigate(Pantalla.PermisosCoordinadores.route) {
                        launchSingleTop = true
                    }
                },
                onAsignar = {
                    navController.navigate(Pantalla.AgregarEmpleado.route) {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable(Pantalla.Salas.route) { SalasScreen() }
        composable(Pantalla.PermisosCoordinadores.route) {
            PermisosCoordinadoresScreen(
                onCancelar = { navController.navegarAPantalla(Pantalla.Coordinadores) },
                onGuardar = { navController.navegarAPantalla(Pantalla.Coordinadores) },
            )
        }
        composable(Pantalla.AgregarEmpleado.route) {
            AgregarEmpleadoScreen(
                onGuardar = { navController.navegarAPantalla(Pantalla.Empleados) },
            )
        }
        composable(Pantalla.AgregarSala.route) {
            AgregarSalaScreen(
                onGuardar = { navController.navegarAPantalla(Pantalla.Salas) },
            )
        }
        composable(Pantalla.Configuracion.route) { ConfiguracionScreen() }
        composable(Pantalla.AcercaDe.route) { AcercaDeScreen() }
        composable(Pantalla.Perfil.route) { PerfilScreen(usuario = usuario) }

        // --Vistas de director--
        composable(Pantalla.ReporteDirector.route) {
            val viewModel: DirectorViewModel = viewModel()
            ReporteAsistenciaScreen(
                viewModel = viewModel,
                onNavigateToSucursales = {
                    navController.navigate(Pantalla.CoberturaDirector.route) {
                        popUpTo(Pantalla.ReporteDirector.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Pantalla.CoberturaDirector.route) {
            val viewModel: DirectorViewModel = viewModel()
            SucursalesScreen(
                viewModel = viewModel,
                onNavigateToReportes = {
                    navController.navigate(Pantalla.ReporteDirector.route) {
                        popUpTo(Pantalla.CoberturaDirector.route) { inclusive = true }
                    }
                }
            )
        }


    }
}
