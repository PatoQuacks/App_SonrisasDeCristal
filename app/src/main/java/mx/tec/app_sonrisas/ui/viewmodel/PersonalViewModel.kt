package mx.tec.app_sonrisas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import mx.tec.app_sonrisas.data.model.Capacitacion
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.model.Sala
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

    // Último ID de empleado usado. Los IDs no se reutilizan al eliminar a alguien, para que
    // un empleado nuevo no "herede" las incidencias o registros de uno que se dio de baja.
    private var ultimoIdEmpleado = empleados.maxOfOrNull { it.id } ?: 0

    val nombresRoles = repository.getNombresRoles()
    val horarios = repository.getHorarios()

    // Salas de la sucursal. Solo aparecen las que se agregan con "Agregar sala".
    var salas: List<Sala> by mutableStateOf(repository.getSalas())
        private set

    // id -> nombre, para la lista "Sala asignada" de Agregar empleado.
    val nombresSalas: Map<Int, String>
        get() = salas.associate { it.id to it.nombre }

    fun existeSala(nombre: String): Boolean {
        return salas.any { it.nombre.equals(nombre.trim(), ignoreCase = true) }
    }

    fun agregarSala(nombre: String, personalMinimo: Int, coordinador: String) {
        salas = salas + Sala(
            id = (salas.maxOfOrNull { it.id } ?: 0) + 1,
            nombre = nombre.trim(),
            personalMinimo = personalMinimo,
            sucursalId = ID_SUCURSAL_SAN_PEDRO,
            coordinadorEncargado = coordinador,
        )
    }

    // Nombre del último empleado dado de alta; Empleados muestra el aviso mientras no sea null.
    var empleadoRecienAgregado by mutableStateOf<String?>(null)
        private set

    // Correos ya usados (en minúsculas), para avisar si el usuario ya existe.
    val correosRegistrados: Set<String>
        get() = empleados.map { it.email.lowercase() }.toSet()

    fun agregarEmpleado(nuevo: NuevoEmpleado) {
        val empleado = Employee(
            id = ++ultimoIdEmpleado,
            subsidiaryId = ID_SUCURSAL_SAN_PEDRO,
            roomId = nuevo.roomId,
            roleId = nuevo.roleId,
            scheduleId = nuevo.scheduleId,
            name = nuevo.nombre,
            email = nuevo.correo,
        )
        // Va al inicio de la lista para que se vea en cuanto se regresa a Empleados.
        empleados = listOf(empleado) + empleados
        // El ID del empleado se guarda también en su sala (Employee.room_id).
        salas = salas.map { sala ->
            if (sala.id == empleado.roomId) sala.copy(empleadosIds = sala.empleadosIds + empleado.id) else sala
        }
        empleadoRecienAgregado = nuevo.nombre
    }

    fun avisoEmpleadoMostrado() {
        empleadoRecienAgregado = null
    }

    // Da de baja a un empleado: sale de la lista, de su sala, de la asistencia y salidas
    // guardadas y de las capacitaciones asignadas. Sus incidencias se conservan como historial
    // (Incidencias muestra "Empleado #id" cuando la persona ya no está en la lista).
    fun eliminarEmpleado(id: Int) {
        empleados = empleados.filterNot { it.id == id }
        salas = salas.map { sala ->
            if (id in sala.empleadosIds) sala.copy(empleadosIds = sala.empleadosIds - id) else sala
        }
        asistenciasPorFecha = asistenciasPorFecha.mapValues { (_, registros) -> registros - id }
        salidasPorFecha = salidasPorFecha.mapValues { (_, salidas) -> salidas - id }
        capacitaciones = capacitaciones.map { it.copy(empleadosAsignados = it.empleadosAsignados - id) }
    }

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
