package mx.tec.app_sonrisas.ui.screens.inferior

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import java.time.LocalDate

@Composable
fun SalidasScreen(
    fecha: LocalDate,
    empleados: List<Employee>,
    nombresRoles: Map<Int, String>,
    onCancelar: () -> Unit,
    onGuardar: (Set<Int>) -> Int,
) {
    var marcados by remember(fecha) { mutableStateOf(emptySet<Int>()) }
    val seleccion = empleados.map { it.id }.filter { it in marcados }.toSet()
    val colores = MaterialTheme.colorScheme
    val contexto = LocalContext.current

    Column(Modifier.fillMaxSize().background(colores.surface)) {
        EncabezadoPantalla {
            Button(
                onClick = onCancelar,
                modifier = Modifier.weight(0.43f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colores.onPrimaryContainer,
                    contentColor = colores.primaryContainer,
                ),
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val total = onGuardar(seleccion)
                    if (total > 0) marcados = emptySet()
                    val mensaje = when (total) {
                        0 -> "No se registraron salidas. Revisa la asistencia del día."
                        1 -> "Salida registrada"
                        else -> "$total salidas registradas"
                    }
                    Toast.makeText(contexto, mensaje, Toast.LENGTH_SHORT).show()
                },
                enabled = seleccion.isNotEmpty(),
                modifier = Modifier.weight(0.57f),
            ) {
                Text("Guardar Cambios")
            }
        }

        Button(
            onClick = { marcados = empleados.map { it.id }.toSet() },
            enabled = empleados.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF278287),
                contentColor = Color.White,
            ),
        ) {
            Text("Marcar todas las salidas")
        }

        if (empleados.isEmpty()) {
            Text(
                text = "No hay salidas pendientes",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge,
            )
        } else {
            LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
                items(empleados, key = { it.id }) { empleado ->
                    val nombre = "${empleado.name} (#${empleado.id})"
                    val rol = nombresRoles[empleado.roleId] ?: "Sin rol asignado"
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "$nombre\n($rol)",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Checkbox(
                            checked = empleado.id in seleccion,
                            onCheckedChange = { marcar ->
                                marcados = if (marcar) marcados + empleado.id else marcados - empleado.id
                            },
                            modifier = Modifier.semantics {
                                contentDescription = "Marcar salida de $nombre"
                            },
                        )
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}
