package mx.tec.app_sonrisas.data.repository

import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.model.Sala
import mx.tec.app_sonrisas.data.remote.ApiService
import mx.tec.app_sonrisas.data.remote.RetrofitClient
import retrofit2.HttpException

class EmpleadoRepository(
    private val api: ApiService = RetrofitClient.api,
) {
    // Datos de demostración. Los IDs y correos no corresponden al servidor.
    fun getEmpleados(): List<Employee> {
        return (1..10).map { id ->
            Employee(
                id = id,
                subsidiaryId = 1,
                // Sin sala asignada: la sucursal empieza sin salas.
                roomId = null,
                roleId = 1,
                scheduleId = 1,
                name = "Nombre de Empleado",
                email = "empleado$id@example.com",
            )
        }
    }

    // Catálogos provisionales (id -> nombre). Sus IDs deben coincidir con los de la base de datos.

    // role_id: tipo de personal.
    fun getNombresRoles(): Map<Int, String> {
        return mapOf(
            1 to "Asistente",
            2 to "Educadora",
            3 to "Cocina",
            4 to "Servicios",
        )
    }

    // room_id: salas de la sucursal. Empieza vacía: solo aparecen las que se agregan
    // desde "Agregar sala" (más adelante vendrán de la API).
    fun getSalas(): List<Sala> {
        return emptyList()
    }

    // schedule_id: los dos horarios fijos que van a estar en la base de datos.
    fun getHorarios(): Map<Int, String> {
        return mapOf(
            1 to "Matutino (7:00 – 15:00)",
            2 to "Vespertino (13:00 – 20:00)",
        )
    }

    // Pregunta a la API si ya hay un usuario con ese correo.
    // Regresa true si existe y false si no. Cualquier otra respuesta (401, 500...)
    // se lanza como HttpException para que el ViewModel muestre el mensaje adecuado.
    suspend fun existeUsuario(token: String, correo: String): Boolean {
        val respuesta = api.buscarUsuario("Bearer $token", correo)
        return when {
            respuesta.isSuccessful -> true
            respuesta.code() == 404 -> false
            else -> throw HttpException(respuesta)
        }
    }
}
