package mx.tec.app_sonrisas.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.tec.app_sonrisas.ui.screens.LoginScreen
import mx.tec.app_sonrisas.ui.screens.PrincipalScreen
import mx.tec.app_sonrisas.ui.viewmodel.SesionViewModel

// Navegación de NIVEL 1: Login <-> Principal
@Composable
fun AppNavigation() {
    // Se crea ARRIBA del NavHost: un solo SesionViewModel para toda la app.
    // Así el token y el usuario siguen disponibles al pasar del login a la parte principal.
    val sesionViewModel: SesionViewModel = viewModel()
    val estado by sesionViewModel.estado.collectAsState()

    if (estado.verificandoSesion) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    val navController = rememberNavController()
    val destinoInicial = if (estado.usuario != null) RutasApp.PRINCIPAL else RutasApp.LOGIN

    NavHost(navController = navController, startDestination = destinoInicial) {

        composable(RutasApp.LOGIN) {
            LoginScreen(
                sesionViewModel = sesionViewModel,
                onLoginExitoso = {
                    navController.navigate(RutasApp.PRINCIPAL) {
                        // Saca el login de la pila: con "atrás" no se regresa al login.
                        popUpTo(RutasApp.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(RutasApp.PRINCIPAL) {
            PrincipalScreen(
                sesionViewModel = sesionViewModel,
                onCerrarSesion = {
                    // Borra el token y el usuario de memoria.
                    sesionViewModel.cerrarSesion()
                    navController.navigate(RutasApp.LOGIN) {
                        // Saca la parte principal de la pila: con "atrás" no se regresa a ella.
                        popUpTo(RutasApp.PRINCIPAL) { inclusive = true }
                    }
                }
            )
        }
    }
}