package mx.tec.app_sonrisas.data.repository

import mx.tec.app_sonrisas.data.model.ReporteAsistencia
import mx.tec.app_sonrisas.data.model.Sucursal

class DirectorRepository {

    fun getSucursales(): List<Sucursal> {
        return listOf(
            Sucursal(
                id = "1",
                nombre = "Estancia San Pedro",
                empleadosActual = 15,
                empleadosTotal = 15,
                estado = "Cobertura completa",
                esCoberturaCompleta = true
            ),
            Sucursal(
                id = "2",
                nombre = "Estancia Santiago",
                empleadosActual = 11,
                empleadosTotal = 14,
                estado = "Personal insuficiente",
                esCoberturaCompleta = false
            ),
            Sucursal(
                id = "3",
                nombre = "Estancia Centro",
                empleadosActual = 12,
                empleadosTotal = 12,
                estado = "Cobertura completa",
                esCoberturaCompleta = true
            ),
            Sucursal(
                id = "4",
                nombre = "Estancia Cumbres",
                empleadosActual = 9,
                empleadosTotal = 11,
                estado = "Personal insuficiente",
                esCoberturaCompleta = false
            ),
            Sucursal(
                id = "5",
                nombre = "Estancia Guadalupe",
                empleadosActual = 13,
                empleadosTotal = 13,
                estado = "Cobertura completa",
                esCoberturaCompleta = true
            )
        )
    }

    fun getReporteAsistencia(sucursalId: String, mes: String): ReporteAsistencia {
        return ReporteAsistencia(
            sucursalSeleccionada = "Sucursal Centro",
            mesSeleccionado = mes.ifEmpty { "Octubre 2026" },
            faltasTotales = 24,
            retardos = 17,
            altas = 6,
            bajas = 2
        )
    }
}