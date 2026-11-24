package mx.tec.app_sonrisas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.tec.app_sonrisas.data.remote.Usuario
import mx.tec.app_sonrisas.data.repository.AuthRepository
import mx.tec.app_sonrisas.data.repository.PrefsRepository
import retrofit2.HttpException
import java.io.IOException

data class SesionUiState(
    val verificandoSesion: Boolean = true,
    val cargando: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val usuario: Usuario? = null,
)

class SesionViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = AuthRepository()
    private val prefsRepo = PrefsRepository(app)

    private val _estado = MutableStateFlow(SesionUiState())
    val estado: StateFlow<SesionUiState> = _estado.asStateFlow()

    init {
        restaurarSesion()
    }

    private fun restaurarSesion() {
        viewModelScope.launch {
            val prefs = prefsRepo.prefsFlow.first()
            if (!prefs.haySesion) {
                _estado.update { it.copy(verificandoSesion = false) }
                return@launch
            }

            try {
                val usuario = repository.obtenerPerfil(prefs.token)
                _estado.update {
                    it.copy(
                        verificandoSesion = false,
                        token = prefs.token,
                        usuario = usuario,
                    )
                }
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    prefsRepo.cerrarSesion()
                }
                _estado.update { it.copy(verificandoSesion = false) }
            } catch (e: Exception) {
                _estado.update { it.copy(verificandoSesion = false) }
            }
        }
    }

    fun iniciarSesion(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _estado.update { it.copy(error = "Escribe tu correo y tu contraseña") }
            return
        }
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                // 1. Login: correo + password -> token
                val token = repository.iniciarSesion(correo.trim(), password)
                // 2. Con el token pedimos el perfil
                val usuario = repository.obtenerPerfil(token)
                prefsRepo.guardarSesion(
                    token = token,
                    correo = usuario.correo,
                    rol = usuario.rol,
                )
                _estado.update { it.copy(cargando = false, token = token, usuario = usuario) }
            } catch (e: HttpException) {
                val mensaje = when (e.code()) {
                    400 -> "Escribe tu correo y tu contraseña"
                    401 -> "Correo o contraseña incorrectos"
                    else -> "Error del servidor (${e.code()})"
                }
                _estado.update { it.copy(cargando = false, error = mensaje) }
            } catch (e: IOException) {
                _estado.update {
                    it.copy(cargando = false, error = "No se pudo conectar con el servidor")
                }
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = "Error inesperado: ${e.message}") }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            prefsRepo.cerrarSesion()
            _estado.value = SesionUiState(verificandoSesion = false)
        }
    }
}
