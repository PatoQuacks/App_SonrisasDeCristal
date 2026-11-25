package mx.tec.app_sonrisas.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// Datos de las pantallas del Responsable que se comparten entre varias de ellas
// (Inicio, Coordinadores, Permisos y Agregar sala).
// Datos de ejemplo: todavía no hay base de datos.

data class Coordinador(
    val nombre: String,
    val area: String,
    val empleadosACargo: Int,
    val presentesHoy: Int,
    val incidentes: List<String>,
)

class ResponsableViewModel : ViewModel() {

    // Un solo lugar para el nombre del responsable y del plantel (antes Inicio decía
    // "Carlos" y Coordinadores "Diego").
    val nombreResponsable = "Carlos"
    val plantel = "Plantel A"

    val coordinadores = listOf(
        Coordinador(
            nombre = "Patricio",
            area = "Turno matutino",
            empleadosACargo = 13,
            presentesHoy = 12,
            incidentes = listOf(
                "03 nov · Caída en el área de juegos",
                "29 oct · Golpe leve durante una actividad",
            ),
        ),
        Coordinador(
            nombre = "Valeria Méndez",
            area = "Turno vespertino",
            empleadosACargo = 8,
            presentesHoy = 6,
            incidentes = listOf("30 oct · Material dañado en la sala"),
        ),
        Coordinador(
            nombre = "Ricardo Salinas",
            area = "Cocina y servicios",
            empleadosACargo = 5,
            presentesHoy = 5,
            incidentes = emptyList(),
        ),
    )

    // Permisos agrupados como en el diseño: grupo -> permisos del grupo.
    val gruposDePermisos: List<Pair<String, List<String>>> = listOf(
        "Personal" to listOf(
            "Ver listado de empleados",
            "Tomar asistencia",
            "Registrar incidencias",
            "Justificar faltas y retardos",
        ),
        "Turnos" to listOf(
            "Editar turnos de su equipo",
        ),
        "Información" to listOf(
            "Descargar reportes del día",
            "Ver datos personales",
        ),
    )

    val totalPermisos = gruposDePermisos.sumOf { (_, lista) -> lista.size }

    // Nombre del coordinador -> permisos que tiene activos.
    var permisos by mutableStateOf(
        mapOf(
            "Patricio" to setOf(
                "Ver listado de empleados",
                "Tomar asistencia",
                "Registrar incidencias",
                "Editar turnos de su equipo",
            ),
            "Valeria Méndez" to setOf(
                "Ver listado de empleados",
                "Tomar asistencia",
                "Registrar incidencias",
                "Justificar faltas y retardos",
                "Editar turnos de su equipo",
            ),
            "Ricardo Salinas" to setOf(
                "Ver listado de empleados",
                "Tomar asistencia",
                "Registrar incidencias",
            ),
        ),
    )
        private set

    // Coordinador cuyos permisos se ven en "Permisos de Coordinadores".
    // Si se entra desde el menú lateral, se muestra el primero.
    private var nombreSeleccionado by mutableStateOf(coordinadores.first().nombre)

    val coordinadorSeleccionado: Coordinador
        get() = coordinadores.firstOrNull { it.nombre == nombreSeleccionado } ?: coordinadores.first()

    fun seleccionarCoordinador(nombre: String) {
        nombreSeleccionado = nombre
    }

    fun permisosDe(nombre: String): Set<String> = permisos[nombre].orEmpty()

    fun guardarPermisos(nombre: String, activos: Set<String>) {
        permisos = permisos + (nombre to activos)
    }
}
