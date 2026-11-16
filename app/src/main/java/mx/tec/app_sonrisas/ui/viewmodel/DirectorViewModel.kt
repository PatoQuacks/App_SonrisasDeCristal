package mx.tec.app_sonrisas.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.tec.app_sonrisas.data.model.ReporteAsistencia
import mx.tec.app_sonrisas.data.model.Sucursal
import mx.tec.app_sonrisas.data.repository.DirectorRepository

data class DirectorUiState(
    val sucursales: List<Sucursal> = emptyList(),
    val reporteAsistencia: ReporteAsistencia? = null,
    val sucursalSeleccionadaFiltro: String = "Sucursal Centro",
    val mesSeleccionadoFiltro: String = "Octubre 2026",
    val isLoading: Boolean = false
)

class DirectorViewModel : ViewModel() {

    // Instanciamos el repositorio (más adelante se puede inyectar con Hilt/Dagger si usan inyección de dependencias)
    private val repository = DirectorRepository()

    private val _uiState = MutableStateFlow(DirectorUiState())
    val uiState: StateFlow<DirectorUiState> = _uiState.asStateFlow()

    init {
        // Al arrancar el ViewModel, cargamos los datos iniciales
        cargarDatos()
    }

    fun cargarDatos() {
        val listaSucursales = repository.getSucursales()
        val reporteInicial = repository.getReporteAsistencia("1", "Octubre 2026")

        _uiState.update { currentState ->
            currentState.copy(
                sucursales = listaSucursales,
                reporteAsistencia = reporteInicial
            )
        }
    }


    fun seleccionarSucursal(nombreSucursal: String) {
        _uiState.update { it.copy(sucursalSeleccionadaFiltro = nombreSucursal) }
        actualizarReporteFiltrado()
    }

    fun seleccionarMes(mes: String) {
        _uiState.update { it.copy(mesSeleccionadoFiltro = mes) }
        actualizarReporteFiltrado()
    }

    private fun actualizarReporteFiltrado() {
        val estadoActual = _uiState.value
        val reporteActualizado = repository.getReporteAsistencia(
            sucursalId = estadoActual.sucursalSeleccionadaFiltro,
            mes = estadoActual.mesSeleccionadoFiltro
        )
        _uiState.update { it.copy(reporteAsistencia = reporteActualizado) }
    }
}