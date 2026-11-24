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
import mx.tec.app_sonrisas.ui.screens.inferior.SalidasScreen
import mx.tec.app_sonrisas.ui.screens.lateral.AcercaDeScreen
import mx.tec.app_sonrisas.ui.screens.lateral.AgregarCapacitacionScreen
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
import java.time.LocalDate


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
        startDestination = destinoInicialPara(usuario),
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
            val fecha = LocalDate.now()
            AsistenciaScreen(
                empleados = personal.empleados,
                nombresRoles = personal.nombresRoles,
                asistenciaGuardada = personal.asistenciaDelDia(fecha),
                onCancelar = { navController.popBackStack() },
                onGuardar = { registros ->
                    personal.guardarAsistencia(registros, fecha)
                },
                onAbrirSalidas = { cambios ->
                    if (cambios != null) personal.guardarAsistencia(cambios, fecha)
                    navController.navigate(Pantalla.Salidas.route) { launchSingleTop = true }
                },
            )
        }
        composable(Pantalla.Salidas.route) {
            val fecha = LocalDate.now()
            SalidasScreen(
                fecha = fecha,
                empleados = personal.empleadosConSalidaPendiente(fecha),
                nombresRoles = personal.nombresRoles,
                onCancelar = { navController.popBackStack() },
                onGuardar = { marcados -> personal.guardarSalidas(fecha, marcados) },
            )
        }
        composable(Pantalla.Capacitaciones.route) {
            CapacitacionesScreen(
                capacitacionesNuevas = personal.capacitaciones,
                onAgregar = {
                    navController.navigate(Pantalla.AgregarCapacitacion.route) { launchSingleTop = true }
                },
            )
        }
        composable(Pantalla.AgregarCapacitacion.route) {
            AgregarCapacitacionScreen(
                onCancelar = { navController.popBackStack() },
                onGuardar = { nombre, fecha ->
                    personal.agregarCapacitacion(nombre, fecha)
                    navController.popBackStack()
                },
            )
        }
        composable(Pantalla.Incidencias.route) {
            IncidenciasScreen(
                empleados = personal.empleados,
                incidenciasGuardadas = personal.incidencias,
                onCancelar = { navController.popBackStack() },
                onGuardar = personal::guardarIncidencias,
            )
        }

        // Menú lateral
        composable(Pantalla.Empleados.route) {
            EmpleadosScreen(
                empleados = personal.empleados,
                nombresRoles = personal.nombresRoles,
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
