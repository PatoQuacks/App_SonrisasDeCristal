package mx.tec.app_sonrisas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import mx.tec.app_sonrisas.data.repository.EmpleadoRepository
import retrofit2.HttpException
import java.io.IOException

private const val MENSAJE_USUARIO_EXISTE = "Este usuario ya existe. Usa otro correo."
private const val MENSAJE_SESION_EXPIRADA = "Tu sesión expiró. Cierra sesión y vuelve a entrar."

// Lo que captura el formulario "Agregar empleado".
// La contraseña NO viaja aquí a propósito: la app no la guarda (ver AgregarEmpleado.kt).
data class NuevoEmpleado(
    val nombre: String,
    val correo: String,
    val roleId: Int,
    val roomId: Int,
    val scheduleId: Int,
)

// Se crea dentro de la pantalla "Agregar empleado", así que se reinicia cada vez que se abre.
// Hace la verificación del usuario contra la API y avisa a la pantalla cómo salió.
class AgregarEmpleadoViewModel(
    private val repository: EmpleadoRepository = EmpleadoRepository(),
) : ViewModel() {

    // true mientras se consulta la API (el botón muestra un indicador de carga).
    var verificando by mutableStateOf(false)
        private set

    // Mensaje debajo del campo de usuario cuando ese usuario ya existe.
    var errorUsuario by mutableStateOf<String?>(null)
        private set

    // Errores de conexión o del servidor.
    var errorGeneral by mutableStateOf<String?>(null)
        private set

    // Se llena cuando el usuario NO existe: la pantalla lo entrega y regresa a Empleados.
    var empleadoListo by mutableStateOf<NuevoEmpleado?>(null)
        private set

    fun limpiarErrorUsuario() {
        errorUsuario = null
    }

    fun empleadoEntregado() {
        empleadoListo = null
    }

    fun verificarYAgregar(token: String?, nuevo: NuevoEmpleado, correosRegistrados: Set<String>) {
        if (verificando) return
        errorUsuario = null
        errorGeneral = null

        // 1. Revisión local contra los empleados que ya están en la lista (datos de ejemplo).
        if (nuevo.correo.lowercase() in correosRegistrados) {
            errorUsuario = MENSAJE_USUARIO_EXISTE
            return
        }
        if (token == null) {
            errorGeneral = MENSAJE_SESION_EXPIRADA
            return
        }

        // 2. Revisión en la base de datos a través de la API.
        viewModelScope.launch {
            verificando = true
            try {
                if (repository.existeUsuario(token, nuevo.correo)) {
                    errorUsuario = MENSAJE_USUARIO_EXISTE
                } else {
                    // Siguiente paso, cuando la API tenga la ruta para crear usuarios:
                    // mandar aquí el correo y la contraseña (POST) antes de dar el alta por buena.
                    empleadoListo = nuevo
                }
            } catch (e: HttpException) {
                errorGeneral = when (e.code()) {
                    401 -> MENSAJE_SESION_EXPIRADA
                    403 -> "Tu usuario no tiene permiso para dar de alta empleados."
                    else -> "Error del servidor (${e.code()}). Intenta de nuevo."
                }
            } catch (e: IOException) {
                errorGeneral = "No se pudo conectar con el servidor. Revisa tu conexión e intenta de nuevo."
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                errorGeneral = "Error inesperado: ${e.message}"
            } finally {
                verificando = false
            }
        }
    }
}
