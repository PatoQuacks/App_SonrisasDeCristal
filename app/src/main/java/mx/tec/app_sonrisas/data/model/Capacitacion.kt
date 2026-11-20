package mx.tec.app_sonrisas.data.model

import java.time.LocalDate

data class Capacitacion(
    val nombre: String,
    val fechaVencimiento: LocalDate,
    val empleadosAsignados: Set<Int>,
)
