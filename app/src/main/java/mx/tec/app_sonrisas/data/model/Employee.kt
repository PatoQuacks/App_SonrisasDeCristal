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
)
