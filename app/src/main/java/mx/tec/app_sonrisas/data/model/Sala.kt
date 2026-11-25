package mx.tec.app_sonrisas.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

// Sala de una estancia. Corresponde a la tabla Room del diagrama de base de datos:
//   Room(id, name, required_personal, direction), y Subsidiary "contiene" Room.
// En la BD los empleados de una sala salen de Employee.room_id; aquí se guardan sus IDs
// para mostrarlos y comparar cuánto personal tiene la sala contra el mínimo requerido.
@Serializable
data class Sala(
    val id: Int,
    @SerialName("name") val nombre: String,
    // Personal mínimo requerido (RF-05: avisar si la sala queda por debajo del mínimo).
    @SerialName("required_personal") val personalMinimo: Int,
    // IDs (Employee.id) del personal asignado a esta sala.
    @SerialName("employee_ids") val empleadosIds: Set<Int> = emptySet(),
    // Sucursal (Subsidiary) a la que pertenece la sala.
    @SerialName("subsidiary_id") val sucursalId: Int? = null,
    @SerialName("direction") val direccion: String? = null,
    // Solo vive en la app: el diagrama todavía no tiene columna para el coordinador encargado.
    @Transient val coordinadorEncargado: String = "",
) {
    val totalEmpleados: Int
        get() = empleadosIds.size

    // Cuántas personas faltan para llegar al mínimo (0 si ya se cubre).
    val faltantes: Int
        get() = (personalMinimo - totalEmpleados).coerceAtLeast(0)

    val debajoDelMinimo: Boolean
        get() = totalEmpleados < personalMinimo
}
