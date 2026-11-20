package mx.tec.app_sonrisas.ui.screens.lateral

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Capacitacion
import java.time.format.DateTimeFormatter

private const val TOTAL_PERSONAL = 15
private val formatoVencimiento = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private data class CapacitacionResumen(
    val nombre: String,
    val detalleFecha: String,
    val capacitados: Int,
    val totalPersonal: Int = TOTAL_PERSONAL,
) {
    val completada: Boolean get() = totalPersonal > 0 && capacitados == totalPersonal
    val avance: String
        get() {
            val estado = if (completada) "Completada" else "En progreso"
            return "$capacitados/$totalPersonal capacitados · $estado"
        }
}

private val capacitaciones = listOf(
    CapacitacionResumen("Primeros auxilios", "Fecha: 20 de octubre de 2026", 10),
    CapacitacionResumen("Protocolos de seguridad infantil", "Fecha: 24 de octubre de 2026", 8),
    CapacitacionResumen("Prevención de accidentes", "Fecha: 29 de octubre de 2026", 15),
    CapacitacionResumen("Higiene y cuidado infantil", "Fecha: 5 de noviembre de 2026", 6),
)

@Composable
fun CapacitacionesScreen(
    capacitacionesNuevas: List<Capacitacion>,
    onAgregar: () -> Unit,
) {
    var seleccion by rememberSaveable { mutableStateOf<Int?>(null) }
    val colores = MaterialTheme.colorScheme
    val listado = capacitacionesNuevas.map {
        CapacitacionResumen(
            nombre = it.nombre,
            detalleFecha = "Vence el ${it.fechaVencimiento.format(formatoVencimiento)}",
            capacitados = 0,
            totalPersonal = it.empleadosAsignados.size,
        )
    } + capacitaciones

    Column(Modifier.fillMaxSize().background(colores.surface)) {
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text = "Estancia San Pedro · Seguimiento de capacitaciones",
                style = MaterialTheme.typography.bodySmall,
                color = colores.onSurfaceVariant,
            )
            Button(
                onClick = onAgregar,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            ) {
                Text("+ Agregar capacitación")
            }
            listado.forEachIndexed { indice, capacitacion ->
                FilaCapacitacion(capacitacion, onVer = { seleccion = indice })
                if (indice < listado.lastIndex) HorizontalDivider()
            }
        }
    }

    seleccion?.let(listado::getOrNull)?.let { capacitacion ->
        AlertDialog(
            onDismissRequest = { seleccion = null },
            title = { Text(capacitacion.nombre) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(capacitacion.detalleFecha)
                    Text(capacitacion.avance)
                }
            },
            confirmButton = {
                TextButton(onClick = { seleccion = null }) { Text("Cerrar") }
            },
        )
    }
}

@Composable
private fun FilaCapacitacion(capacitacion: CapacitacionResumen, onVer: () -> Unit) {
    val colores = MaterialTheme.colorScheme
    val verde = if (isSystemInDarkTheme()) {
        Color(0xFF91D98D)
    } else {
        Color(0xFF146C3A)
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable(onClickLabel = "Ver detalle", onClick = onVer)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = capacitacion.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = capacitacion.detalleFecha,
                style = MaterialTheme.typography.bodyMedium,
                color = colores.onSurfaceVariant,
            )
            Text(
                text = capacitacion.avance,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (capacitacion.completada) verde else colores.primary,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = colores.primary,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
