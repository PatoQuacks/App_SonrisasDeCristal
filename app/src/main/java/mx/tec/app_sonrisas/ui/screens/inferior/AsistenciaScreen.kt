package mx.tec.app_sonrisas.ui.screens.inferior

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.ui.viewmodel.EstadoAsistencia
import mx.tec.app_sonrisas.ui.viewmodel.RegistroAsistencia

private val coloresAsistencia = listOf(Color(0xFF0C9E53), Color(0xFFE5A000), Color(0xFFC4110D))
private val anchoColumna = 48.dp

@Composable
fun AsistenciaScreen(
    empleados: List<Employee>,
    nombresRoles: Map<Int, String>,
    asistenciaGuardada: Map<Int, RegistroAsistencia>,
    onCancelar: () -> Unit,
    onGuardar: (Map<Int, RegistroAsistencia>) -> Unit,
) {
    var borrador by remember(asistenciaGuardada) { mutableStateOf(asistenciaGuardada) }
    val colores = MaterialTheme.colorScheme

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
            Button(onClick = { onGuardar(borrador) }, modifier = Modifier.weight(0.57f)) {
                Text("Guardar Cambios")
            }
        }

        Button(
            onClick = {
                borrador = empleados.associate { it.id to RegistroAsistencia(EstadoAsistencia.Presente) }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF278287),
                contentColor = Color.White,
            ),
        ) {
            Text("Dar asistencia a todo el personal")
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text("Empleado", fontWeight = FontWeight.Medium)
            }
            EstadoAsistencia.entries.forEachIndexed { indice, estado ->
                Box(Modifier.width(anchoColumna), contentAlignment = Alignment.Center) {
                    Box(
                        Modifier.size(32.dp).background(coloresAsistencia[indice]),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = estado.letra,
                            color = if (estado == EstadoAsistencia.Tardanza) Color.Black else Color.White,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.semantics { contentDescription = estado.etiqueta },
                        )
                    }
                }
            }
        }

        LazyColumn(Modifier.fillMaxWidth().weight(1f)) {
            items(empleados, key = { it.id }) { empleado ->
                val empleadoId = empleado.id
                val registro = borrador[empleadoId]
                Row(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 66.dp)
                        .padding(start = 12.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${empleado.name}\n(${nombresRoles[empleado.roleId] ?: "Sin rol asignado"})",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                    )
                    EstadoAsistencia.entries.forEach { estado ->
                        val seleccionado = registro?.estado == estado
                        Box(Modifier.width(anchoColumna), contentAlignment = Alignment.Center) {
                            Checkbox(
                                checked = seleccionado,
                                onCheckedChange = { marcar ->
                                    borrador = if (marcar) {
                                        borrador + (empleadoId to RegistroAsistencia(estado))
                                    } else {
                                        borrador - empleadoId
                                    }
                                },
                                modifier = Modifier.semantics {
                                    contentDescription = "${estado.etiqueta}, empleado $empleadoId"
                                },
                            )
                        }
                    }
                }

                if (registro != null && registro.estado != EstadoAsistencia.Presente) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(colores.surfaceVariant)
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                            Text("Motivo", fontWeight = FontWeight.Medium)
                        }
                        OutlinedTextField(
                            value = registro.motivo,
                            onValueChange = { motivo ->
                                borrador = borrador + (empleadoId to registro.copy(motivo = motivo))
                            },
                            modifier = Modifier.weight(1f).semantics {
                                contentDescription = "Motivo, empleado $empleadoId"
                            },
                            shape = RectangleShape,
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colores.surface,
                                unfocusedContainerColor = colores.surface,
                            ),
                        )
                    }
                }
            }
        }
    }
}
