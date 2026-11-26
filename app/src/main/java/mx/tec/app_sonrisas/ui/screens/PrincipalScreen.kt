package mx.tec.app_sonrisas.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import mx.tec.app_sonrisas.navigation.Pantalla
import mx.tec.app_sonrisas.navigation.PrincipalNavHost
import mx.tec.app_sonrisas.navigation.obtenerBarraInferior
import mx.tec.app_sonrisas.navigation.seccionPadre
import mx.tec.app_sonrisas.navigation.todasLasPantallas
import mx.tec.app_sonrisas.ui.components.BarraInferior
import mx.tec.app_sonrisas.ui.components.BarraSuperior
import mx.tec.app_sonrisas.ui.components.MenuLateral
import mx.tec.app_sonrisas.ui.viewmodel.SesionViewModel

// El "cascarón" de la app: menú lateral + barra superior + barra inferior,
// y en medio el NavHost con las pantallas.
@Composable
fun PrincipalScreen(
    sesionViewModel: SesionViewModel,
    onCerrarSesion: () -> Unit,
) {
    // Datos de la sesión (token y usuario) que dejó el login.
    val sesion by sesionViewModel.estado.collectAsState()

    // NavController propio de la parte principal (distinto al de AppNavigation).
    val navController = rememberNavController()

    // Estado del menú lateral: abierto o cerrado.
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    // Abrir y cerrar el menú es una animación, por eso se hace dentro de una corrutina.
    val scope = rememberCoroutineScope()

    // Ruta de la pantalla visible. Se actualiza sola cada vez que navegamos.
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val tituloActual = when (rutaActual) {
        Pantalla.Capacitaciones.route -> "Capacitaciones de sucursal"
        else -> todasLasPantallas.find { it.route == rutaActual }?.titulo.orEmpty()
    }
    // Pestañas de la barra inferior de este usuario (dependen de su nivel, ver PermisosUsuario.kt).
    val pestanas = obtenerBarraInferior(sesion.usuario)
    // Si la pantalla actual vive dentro de otra (por ejemplo "Agregar empleado" dentro de
    // Empleados), esta es su sección padre. Se usa para marcar el menú y la barra inferior.
    // Una pantalla que es pestaña de la barra inferior (por ejemplo Perfil) no tiene padre.
    val pantallaPadre = if (pestanas.any { it.route == rutaActual }) null else seccionPadre[rutaActual]
    val rutaSeccion = pantallaPadre?.route ?: rutaActual

    // Menú lateral y barra inferior: si ya estás dentro de esa sección, regresa a su
    // pantalla principal; si no, cambia de sección y abre su pantalla principal.
    fun irASeccion(pantalla: Pantalla) {
        if (pantalla.route == rutaSeccion) {
            navController.regresarA(pantalla)
        } else {
            navController.navegarAPantalla(pantalla)
        }
    }

    // Abre una pantalla que vive dentro de la sección actual (con "atrás" se regresa).
    fun abrir(pantalla: Pantalla) {
        navController.navigate(pantalla.route) { launchSingleTop = true }
    }

    // Si el menú está abierto, el botón "atrás" lo cierra en lugar de salir.
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                usuario = sesion.usuario,
                rutaSeccion = rutaSeccion,
                onSeleccionar = { pantalla ->
                    scope.launch { drawerState.close() }
                    irASeccion(pantalla)
                },
                onVerPerfil = {
                    scope.launch { drawerState.close() }
                    abrir(Pantalla.Perfil)
                },
                onCerrarSesion = {
                    scope.launch { drawerState.close() }
                    onCerrarSesion()
                },
            )
        },
    ) {
        Scaffold(
            topBar = {
                BarraSuperior(
                    titulo = tituloActual,
                    onAbrirMenu = { scope.launch { drawerState.open() } },
                    // En pantallas internas se muestra la flecha para regresar.
                    onRegresar = if (pantallaPadre != null) {
                        { navController.navigateUp() }
                    } else {
                        null
                    },
                    acciones = {
                        if (rutaActual == Pantalla.Empleados.route) {
                            Button(
                                onClick = {
                                    navController.navigate(Pantalla.Asistencia.route) {
                                        launchSingleTop = true
                                    }
                                },
                                modifier = Modifier.padding(end = 12.dp),
                            ) {
                                Text("Tomar Asistencia")
                            }
                        }
                    },
                )
            },
            bottomBar = {
                BarraInferior(
                    rutaActual = rutaSeccion,
                    usuario = sesion.usuario,
                    onSeleccionar = { pantalla -> irASeccion(pantalla) },
                )
            },
            // Botón "+" de las secciones que permiten agregar algo.
            floatingActionButton = {
                when (rutaActual) {
                    Pantalla.Empleados.route -> BotonAgregar("Agregar empleado") {
                        abrir(Pantalla.AgregarEmpleado)
                    }
                    Pantalla.Salas.route -> BotonAgregar("Agregar sala") {
                        abrir(Pantalla.AgregarSala)
                    }
                }
            },
        ) { innerPadding ->
            PrincipalNavHost(
                navController = navController,
                usuario = sesion.usuario,
                token = sesion.token,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

// Función de extensión sobre NavController para navegar entre pantallas principales
// manteniendo una pila limpia. Siempre abre la pantalla principal de la sección.
// No usa saveState/restoreState: con esas opciones, si salías de Asistencia estando en
// "Registrar salidas", al volver a Asistencia reaparecía Salidas ("congelada") hasta
// tocar la flecha de regresar. Pasaba igual con cualquier pantalla interna.
fun NavController.navegarAPantalla(pantalla: Pantalla) {
    navigate(pantalla.route) {
        popUpTo(graph.findStartDestination().id)
        launchSingleTop = true
    }
}

// Para terminar un formulario (guardar o cancelar) y volver a su pantalla "padre".
// - Si el padre está en la pila (se llegó desde ahí), simplemente regresa a él.
// - Si no (el formulario se abrió desde el menú lateral), abre el padre desde cero.
// No usa saveState/restoreState como navegarAPantalla: con esas opciones el formulario
// se guardaba junto con su padre y "revivía" la siguiente vez que se abría esa sección.
fun NavController.regresarA(pantalla: Pantalla) {
    val regreso = popBackStack(pantalla.route, inclusive = false)
    if (!regreso) {
        navigate(pantalla.route) {
            popUpTo(graph.findStartDestination().id)
            launchSingleTop = true
        }
    }
}

@Composable
private fun BotonAgregar(texto: String, onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text(texto) },
        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
        onClick = onClick,
    )
}
