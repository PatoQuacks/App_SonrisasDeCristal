package mx.tec.app_sonrisas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.repository.EmpleadoRepository
import java.time.LocalDate

enum class EstadoAsistencia(val letra: String, val etiqueta: String) {
    Presente("P", "Presente"),
    Tardanza("T", "Tardanza"),
    Ausente("A", "Ausente"),
}

data class RegistroAsistencia(
    val estado: EstadoAsistencia,
    val motivo: String = "",
)

data class RegistroIncidencia(
    val id: Int,
    val tipo: String = "",
    val descripcion: String = "",
    val empleadoId: Int? = null,
    val fecha: LocalDate? = null,
) {
    val tieneDatos: Boolean
        get() = tipo.isNotEmpty() || descripcion.isNotBlank() || empleadoId != null || fecha != null
}

class PersonalViewModel(
    private val repository: EmpleadoRepository = EmpleadoRepository(),
) : ViewModel() {
    var empleados: List<Employee> by mutableStateOf(repository.getEmpleados())
        private set

    val nombresRoles = repository.getNombresRoles()

    var asistencia by mutableStateOf(
        mapOf(
            1 to RegistroAsistencia(EstadoAsistencia.Presente),
            2 to RegistroAsistencia(EstadoAsistencia.Presente),
            3 to RegistroAsistencia(EstadoAsistencia.Tardanza),
        ),
    )
        private set

    var incidencias by mutableStateOf(emptyList<RegistroIncidencia>())
        private set

    fun guardarIncidencias(registros: List<RegistroIncidencia>) {
        incidencias = registros.toList()
    }

    fun guardarAsistencia(registros: Map<Int, RegistroAsistencia>) {
        asistencia = registros.toMap()
    }
}
