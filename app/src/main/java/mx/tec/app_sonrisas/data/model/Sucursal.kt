package mx.tec.app_sonrisas.data.model
// clase que ayuda a pintar la pantalla de listado de sucursales del director
data class Sucursal(
    val id: String,
    val nombre: String,
    val empleadosActual: Int,
    val empleadosTotal: Int,
    val estado: String, // ya sea "cobertura completa" o "Personal insuficiente"
    val esCoberturaCompleta: Boolean // para cambiar el color del texto

)
