package mx.tec.app_sonrisas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.tec.app_sonrisas.data.model.Capacitacion
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.repository.EmpleadoRepository
import java.time.LocalDate
import java.time.LocalDateTime

private const val ID_SUCURSAL_SAN_PEDRO = 1

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

    var capacitaciones by mutableStateOf(emptyList<Capacitacion>())
        private set

    fun agregarCapacitacion(nombre: String, fechaVencimiento: LocalDate) {
        val empleadosAsignados = empleados
            .filter { it.subsidiaryId == ID_SUCURSAL_SAN_PEDRO }
            .map { it.id }
            .toSet()
        capacitaciones = listOf(Capacitacion(nombre.trim(), fechaVencimiento, empleadosAsignados)) +
            capacitaciones
    }

    private var asistenciasPorFecha by mutableStateOf(
        mapOf(
            LocalDate.now() to mapOf(
                1 to RegistroAsistencia(EstadoAsistencia.Presente),
                2 to RegistroAsistencia(EstadoAsistencia.Presente),
                3 to RegistroAsistencia(EstadoAsistencia.Tardanza),
            ),
        ),
    )

    private var salidasPorFecha by mutableStateOf<Map<LocalDate, Map<Int, LocalDateTime>>>(emptyMap())

    val asistencia: Map<Int, RegistroAsistencia>
        get() = asistenciaDelDia(LocalDate.now())

    var incidencias by mutableStateOf(emptyList<RegistroIncidencia>())
        private set

    fun guardarIncidencias(registros: List<RegistroIncidencia>) {
        incidencias = registros.toList()
    }

    fun asistenciaDelDia(fecha: LocalDate): Map<Int, RegistroAsistencia> {
        return asistenciasPorFecha[fecha].orEmpty()
    }

    fun guardarAsistencia(registros: Map<Int, RegistroAsistencia>, fecha: LocalDate) {
        asistenciasPorFecha = asistenciasPorFecha + (fecha to registros.toMap())
    }

    fun empleadosConSalidaPendiente(fecha: LocalDate): List<Employee> {
        val asistenciaDelDia = asistenciaDelDia(fecha)
        val salidas = salidasPorFecha[fecha].orEmpty()
        return empleados.filter { empleado ->
            val estado = asistenciaDelDia[empleado.id]?.estado
            (estado == EstadoAsistencia.Presente || estado == EstadoAsistencia.Tardanza) &&
                empleado.id !in salidas
        }
    }

    fun guardarSalidas(fecha: LocalDate, empleadosMarcados: Set<Int>): Int {
        val ahora = LocalDateTime.now()
        if (fecha != ahora.toLocalDate()) return 0

        val nuevasSalidas = empleadosConSalidaPendiente(fecha)
            .filter { it.id in empleadosMarcados }
            .associate { it.id to ahora }
        if (nuevasSalidas.isNotEmpty()) {
            val salidasDelDia = salidasPorFecha[fecha].orEmpty() + nuevasSalidas
            salidasPorFecha = salidasPorFecha + (fecha to salidasDelDia)
        }
        return nuevasSalidas.size
    }
}
