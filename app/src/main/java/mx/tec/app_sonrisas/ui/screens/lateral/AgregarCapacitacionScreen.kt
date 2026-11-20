package mx.tec.app_sonrisas.ui.screens.lateral

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.CampoFormulario
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

@Composable
fun AgregarCapacitacionScreen(
    onCancelar: () -> Unit,
    onGuardar: (String, LocalDate) -> Unit,
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var diaVencimiento by rememberSaveable { mutableStateOf<Long?>(null) }
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }
    val fecha = diaVencimiento?.let(LocalDate::ofEpochDay)
    val contexto = LocalContext.current
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column {
            CampoFormulario(
                etiqueta = "Nombre de la capacitación",
                valor = nombre,
                onCambio = { nombre = it },
            )
            if (mostrarErrores && nombre.isBlank()) {
                Text("Escribe el nombre", color = colores.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        Column {
            Text("Fecha de vencimiento", style = MaterialTheme.typography.titleSmall)
            OutlinedButton(
                onClick = {
                    val inicial = fecha ?: LocalDate.now()
                    DatePickerDialog(
                        contexto,
                        { _, anio, mes, dia ->
                            diaVencimiento = LocalDate.of(anio, mes + 1, dia).toEpochDay()
                        },
                        inicial.year,
                        inicial.monthValue - 1,
                        inicial.dayOfMonth,
                    ).show()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(fecha?.format(formatoFecha) ?: "Seleccionar fecha")
            }
            if (mostrarErrores && fecha == null) {
                Text("Selecciona la fecha", color = colores.error, style = MaterialTheme.typography.bodySmall)
            }
        }

        Text(
            "Se asignará a todo el personal de la sucursal.",
            style = MaterialTheme.typography.bodySmall,
            color = colores.onSurfaceVariant,
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    mostrarErrores = true
                    if (nombre.isNotBlank() && fecha != null) onGuardar(nombre.trim(), fecha)
                },
                modifier = Modifier.weight(1f),
            ) {
                Text("Guardar")
            }
        }
    }
}
