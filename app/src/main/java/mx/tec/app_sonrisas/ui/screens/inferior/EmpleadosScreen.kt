package mx.tec.app_sonrisas.ui.screens.inferior

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.model.puesto
import mx.tec.app_sonrisas.ui.viewmodel.EstadoAsistencia
import mx.tec.app_sonrisas.ui.viewmodel.RegistroAsistencia

@Composable
fun EmpleadosScreen(
    empleados: List<Employee>,
    nombresRoles: Map<Int, String>,
    asistencia: Map<Int, RegistroAsistencia>,
    horarios: Map<Int, String> = emptyMap(),
    // Se llama después de que el usuario confirma la advertencia.
    onEliminar: (Employee) -> Unit = {},
) {
    var empleadoExpandido by rememberSaveable { mutableStateOf<Int?>(null) }
    // Empleado al que se le tocó el bote de basura; mientras no sea null se muestra la advertencia.
    var idPorEliminar by rememberSaveable { mutableStateOf<Int?>(null) }
    val contexto = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            // Espacio al final para que el botón "Agregar empleado" no tape al último empleado.
            contentPadding = PaddingValues(bottom = 88.dp),
        ) {
            items(empleados, key = { it.id }) { empleado ->
                val empleadoId = empleado.id
                val expandido = empleadoExpandido == empleadoId
                FilaEmpleado(
                    empleado = empleado,
                    puesto = empleado.puesto(nombresRoles),
                    turno = horarios[empleado.scheduleId] ?: "Sin turno asignado",
                    registro = asistencia[empleadoId],
                    expandido = expandido,
                    onAlternar = {
                        empleadoExpandido = if (expandido) null else empleadoId
                    },
                    onPedirEliminar = { idPorEliminar = empleadoId },
                )
            }
        }
    }

    // Advertencia antes de borrar.
    val empleadoPorEliminar = empleados.find { it.id == idPorEliminar }
    if (empleadoPorEliminar != null) {
        ConfirmarEliminarEmpleado(
            empleado = empleadoPorEliminar,
            puesto = empleadoPorEliminar.puesto(nombresRoles),
            onConfirmar = {
                if (empleadoExpandido == empleadoPorEliminar.id) empleadoExpandido = null
                idPorEliminar = null
                onEliminar(empleadoPorEliminar)
                Toast.makeText(contexto, "Empleado ${empleadoPorEliminar.name} eliminado", Toast.LENGTH_SHORT).show()
            },
            onCancelar = { idPorEliminar = null },
        )
    }
}

@Composable
private fun ConfirmarEliminarEmpleado(
    empleado: Employee,
    puesto: String,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        icon = {
            Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        },
        title = { Text("¿Eliminar empleado?") },
        text = {
            Text(
                "Vas a eliminar a ${empleado.name} (#${empleado.id}, $puesto). " +
                    "Saldrá de la lista de empleados y de su sala. Sus incidencias ya registradas " +
                    "se conservan.\n\nEsta acción no se puede deshacer.",
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
            ) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        },
    )
}

@Composable
private fun FilaEmpleado(
    empleado: Employee,
    puesto: String,
    turno: String,
    registro: RegistroAsistencia?,
    expandido: Boolean,
    onAlternar: () -> Unit,
    onPedirEliminar: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = if (expandido) "Ocultar asistencia" else "Ver asistencia",
                onClick = onAlternar,
            )
            .semantics { stateDescription = if (expandido) "Expandido" else "Contraído" }
            .heightIn(min = 64.dp)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (expandido) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "${empleado.name} ($puesto)",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onPedirEliminar) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Eliminar a ${empleado.name}",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

    if (expandido) {
        val verde = if (isSystemInDarkTheme()) Color(0xFF91D98D) else Color(0xFF146C17)
        Text(
            text = buildAnnotatedString {
                append("Turno:  $turno\n")
                append("Asistencia:  ")
                val estado = registro?.estado
                if (estado == EstadoAsistencia.Presente) {
                    withStyle(SpanStyle(color = verde)) { append(estado.etiqueta) }
                } else {
                    append(estado?.etiqueta ?: "Sin registrar")
                }
                if (registro != null) {
                    append("\nAnotado por: ")
                    withStyle(SpanStyle(color = verde)) { append("Coordinadora A") }
                    if (registro.motivo.isNotBlank()) {
                        append("\nMotivo: ")
                        append(registro.motivo)
                    }
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 28.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
    }
}
