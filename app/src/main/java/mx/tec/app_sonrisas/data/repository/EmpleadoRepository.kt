package mx.tec.app_sonrisas.data.repository

import mx.tec.app_sonrisas.data.model.Employee

class EmpleadoRepository {
    // Datos de demostración. Los IDs y correos no corresponden al servidor.
    fun getEmpleados(): List<Employee> {
        return (1..10).map { id ->
            Employee(
                id = id,
                subsidiaryId = 1,
                roomId = 1,
                roleId = 1,
                scheduleId = 1,
                name = "Nombre de Empleado",
                email = "empleado$id@example.com",
            )
        }
    }

    // Catálogo provisional para resolver role_id al mostrar el puesto.
    fun getNombresRoles(): Map<Int, String> {
        return mapOf(1 to "Puesto")
    }
}
