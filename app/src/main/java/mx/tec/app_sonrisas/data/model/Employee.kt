package mx.tec.app_sonrisas.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Representación del empleado para la app. password_hash pertenece al servidor.
// Los IDs de relaciones aceptan null mientras se confirma la nulabilidad del esquema.
@Serializable
data class Employee(
    val id: Int,
    @SerialName("subsidiary_id") val subsidiaryId: Int?,
    @SerialName("room_id") val roomId: Int?,
    @SerialName("role_id") val roleId: Int?,
    @SerialName("schedule_id") val scheduleId: Int?,
    val name: String,
    val email: String,
    // Tipo de trabajo que hace el empleado (Asistente, Educadora, Cocina...).
    // Es null para director, responsable y coordinador: ellos no tienen esta etiqueta.
    val occupation: String? = null,
)

// Lo que se muestra entre paréntesis junto al nombre: su tipo de personal si lo tiene;
// si no (director, responsable, coordinador), el nombre de su rol.
fun Employee.puesto(nombresRoles: Map<Int, String>): String =
    occupation ?: nombresRoles[roleId] ?: "Sin puesto asignado"

