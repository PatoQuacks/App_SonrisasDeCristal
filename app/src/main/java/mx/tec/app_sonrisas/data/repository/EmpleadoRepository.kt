package mx.tec.app_sonrisas.data.repository

import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.model.Sala
import mx.tec.app_sonrisas.data.remote.ApiService
import mx.tec.app_sonrisas.data.remote.RetrofitClient
import mx.tec.app_sonrisas.navigation.NivelUsuario
import retrofit2.HttpException

class EmpleadoRepository(
    private val api: ApiService = RetrofitClient.api,
) {
    // Datos de demostración. Los IDs y correos no corresponden al servidor.
    fun getEmpleados(): List<Employee> {
        val tipos = getTiposDePersonal()
        return (1..10).map { id ->
            Employee(
                id = id,
                subsidiaryId = 1,
                // Sin sala asignada: la sucursal empieza sin salas.
                roomId = null,
                roleId = NivelUsuario.EMPLEADO,
                scheduleId = 1,
                name = "Nombre de Empleado",
                email = "empleado$id@example.com",
                occupation = tipos[(id - 1) % tipos.size],
            )
        }
    }

    // Catálogos provisionales (id -> nombre). Sus IDs deben coincidir con los de la base de datos.

    // role_id: los 4 roles principales. Son los mismos números de NivelUsuario
    // (PermisosUsuario.kt), que decide qué pantallas ve cada quien.
    fun getNombresRoles(): Map<Int, String> {
        return mapOf(
            NivelUsuario.DIRECTOR to "Director",
            NivelUsuario.RESPONSABLE to "Responsable",
            NivelUsuario.COORDINADOR to "Coordinador",
            NivelUsuario.EMPLEADO to "Empleado",
        )
    }

    // Tipos de personal (Employee.occupation): lista fija de opciones por ahora.
    fun getTiposDePersonal(): List<String> {
        return listOf("Asistente", "Educadora", "Cocina", "Servicios")
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
