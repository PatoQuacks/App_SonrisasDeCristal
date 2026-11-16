package mx.tec.app_sonrisas.data.model
// clase que contiene metricas mensuales que se mostraran en la pantalla de director Reporte de Asistencia
data class ReporteAsistencia(
    val sucursalSeleccionada: String,
    val mesSeleccionado: String,
    val faltasTotales: Int,
    val retardos: Int,
    val altas: Int,
    val bajas: Int
)