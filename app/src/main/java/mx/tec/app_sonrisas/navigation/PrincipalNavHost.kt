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
import mx.tec.app_sonrisas.ui.screens.lateral.AvisoEmpleadoAgregado
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
import mx.tec.app_sonrisas.ui.screens.regresarA
import mx.tec.app_sonrisas.ui.viewmodel.DirectorViewModel
import mx.tec.app_sonrisas.ui.viewmodel.PersonalViewModel
import mx.tec.app_sonrisas.ui.viewmodel.ResponsableViewModel
import java.time.LocalDate


// Navegación de NIVEL 2: las pantallas que viven dentro de la parte principal.
// Este NavHost se dibuja en medio del Scaffold, entre la barra superior y la inferior.
@Composable
fun PrincipalNavHost(
    navController: NavHostController,
    usuario: Usuario?,
    token: String?,
    modifier: Modifier = Modifier,
) {
    val personal: PersonalViewModel = viewModel()
    // Datos compartidos por las pantallas del Responsable (coordinadores, permisos...).
    val responsable: ResponsableViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = destinoInicialPara(usuario),
        modifier = modifier,
    ) {
        // Barra inferior
        composable(Pantalla.Inicio.route) {
            InicioScreen(
                nombreResponsable = responsable.nombreResponsable,
                plantel = responsable.plantel,
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
                horarios = personal.horarios,
                onEliminar = { empleado -> personal.eliminarEmpleado(empleado.id) },
            )
            // Pop-up después de dar de alta a un empleado en "Agregar empleado".
            personal.empleadoRecienAgregado?.let { nombre ->
                AvisoEmpleadoAgregado(
                    nombre = nombre,
                    onCerrar = personal::avisoEmpleadoMostrado,
                )
            }
        }
        composable(Pantalla.Coordinadores.route) {
            CoordinadoresScreen(
                coordinadores = responsable.coordinadores,
                permisos = responsable.permisos,
                totalPermisos = responsable.totalPermisos,
                nombreResponsable = responsable.nombreResponsable,
                plantel = responsable.plantel,
                // navigate "normal": con el botón atrás se regresa a Coordinadores.
                onVerPermisos = { nombre ->
                    responsable.seleccionarCoordinador(nombre)
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
        composable(Pantalla.Salas.route) {
            SalasScreen(
                salas = personal.salas,
                empleados = personal.empleados,
                plantel = responsable.plantel,
            )
        }
        composable(Pantalla.PermisosCoordinadores.route) {
            val coordinador = responsable.coordinadorSeleccionado
            PermisosCoordinadoresScreen(
                coordinador = coordinador,
                gruposDePermisos = responsable.gruposDePermisos,
                permisosIniciales = responsable.permisosDe(coordinador.nombre),
                onCancelar = { navController.regresarA(Pantalla.Coordinadores) },
                onGuardar = { activos ->
                    responsable.guardarPermisos(coordinador.nombre, activos)
                    navController.regresarA(Pantalla.Coordinadores)
                },
            )
        }
        composable(Pantalla.AgregarEmpleado.route) {
            AgregarEmpleadoScreen(
                token = token,
                tiposDePersonal = personal.nombresRoles,
                salas = personal.nombresSalas,
                horarios = personal.horarios,
                correosRegistrados = personal.correosRegistrados,
                onCancelar = { navController.popBackStack() },
                onEmpleadoAgregado = { nuevo ->
                    personal.agregarEmpleado(nuevo)
                    navController.regresarA(Pantalla.Empleados)
                },
                onAgregarSala = {
                    navController.navigate(Pantalla.AgregarSala.route) { launchSingleTop = true }
                },
            )
        }
        composable(Pantalla.AgregarSala.route) {
            AgregarSalaScreen(
                coordinadores = responsable.coordinadores.map { it.nombre },
                nombreRepetido = personal::existeSala,
                onGuardar = { nombre, personalMinimo, coordinador ->
                    personal.agregarSala(nombre, personalMinimo, coordinador)
                    // Si se abrió desde Agregar empleado, regresa ahí (la sala nueva ya aparece
                    // en "Sala asignada"); si no, regresa a Salas.
                    val anterior = navController.previousBackStackEntry?.destination?.route
                    if (anterior == Pantalla.AgregarEmpleado.route) {
                        navController.popBackStack()
                    } else {
                        navController.regresarA(Pantalla.Salas)
                    }
                },
            )
        }
        composable(Pantalla.Configuracion.route) {
            // Perfil y Acerca de se abren desde aquí (ya no están en el menú lateral).
            ConfiguracionScreen(
                onVerPerfil = {
                    navController.navigate(Pantalla.Perfil.route) { launchSingleTop = true }
                },
                onVerAcercaDe = {
                    navController.navigate(Pantalla.AcercaDe.route) { launchSingleTop = true }
                },
            )
        }
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
