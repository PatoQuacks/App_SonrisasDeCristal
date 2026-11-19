package mx.tec.app_sonrisas.ui.screens.inferior

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.ui.viewmodel.EstadoAsistencia
import mx.tec.app_sonrisas.ui.viewmodel.RegistroAsistencia

@Composable
fun EmpleadosScreen(
    empleados: List<Employee>,
    nombresRoles: Map<Int, String>,
    asistencia: Map<Int, RegistroAsistencia>,
) {
    var empleadoExpandido by rememberSaveable { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
    ) {
        LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
            items(empleados, key = { it.id }) { empleado ->
                val empleadoId = empleado.id
                val expandido = empleadoExpandido == empleadoId
                FilaEmpleado(
                    empleado = empleado,
                    puesto = nombresRoles[empleado.roleId] ?: "Sin rol asignado",
                    registro = asistencia[empleadoId],
                    expandido = expandido,
                    onAlternar = {
                        empleadoExpandido = if (expandido) null else empleadoId
                    },
                )
            }
        }
    }
}

@Composable
private fun FilaEmpleado(
    empleado: Employee,
    puesto: String,
    registro: RegistroAsistencia?,
    expandido: Boolean,
    onAlternar: () -> Unit,
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
        )
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))

    if (expandido) {
        val verde = if (isSystemInDarkTheme()) Color(0xFF91D98D) else Color(0xFF146C17)
        Text(
            text = buildAnnotatedString {
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
