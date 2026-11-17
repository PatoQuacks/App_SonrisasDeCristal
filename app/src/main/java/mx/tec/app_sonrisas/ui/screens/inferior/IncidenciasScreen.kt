package mx.tec.app_sonrisas.ui.screens.inferior

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.viewmodel.RegistroIncidencia

private val tiposIncidente = listOf(
    "Personal de Sucursal",
    "Conflicto entre niños",
    "Equipo electrónico dañado",
    "Objetos personales",
    "Material dañado",
)

@Composable
fun IncidenciasScreen(
    incidenciasGuardadas: List<RegistroIncidencia>,
    onCancelar: () -> Unit,
    onGuardar: (List<RegistroIncidencia>) -> Unit,
) {
    val contexto = LocalContext.current
    val colores = MaterialTheme.colorScheme
    var siguienteId by remember { mutableStateOf((incidenciasGuardadas.maxOfOrNull { it.id } ?: 1) + 1) }
    var borrador by remember {
        mutableStateOf(incidenciasGuardadas.ifEmpty { listOf(RegistroIncidencia(id = 1)) })
    }
    var mostrarErrores by remember { mutableStateOf(false) }

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
                    val registros = borrador.filter { it.tipo.isNotEmpty() || it.descripcion.isNotBlank() }
                    mostrarErrores = registros.any { it.tipo.isEmpty() || it.descripcion.isBlank() }
                    if (!mostrarErrores) {
                        onGuardar(registros.map { it.copy(descripcion = it.descripcion.trim()) })
                        Toast.makeText(contexto, "Cambios guardados", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(0.57f),
            ) {
                Text("Guardar Cambios")
            }
        }

        Column(
            Modifier.fillMaxWidth().weight(1f)
                .verticalScroll(rememberScrollState()).padding(horizontal = 12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(Modifier.weight(0.44f), contentAlignment = Alignment.Center) {
                    Text("Tipo de Incidente", style = MaterialTheme.typography.titleSmall)
                }
                Box(Modifier.weight(0.56f), contentAlignment = Alignment.Center) {
                    Text("Descripción", style = MaterialTheme.typography.titleSmall)
                }
            }
            borrador.forEach { incidente ->
                key(incidente.id) {
                    FilaIncidente(
                        incidente = incidente,
                        mostrarErrores = mostrarErrores,
                        onCambio = { actualizado ->
                            borrador = borrador.map { if (it.id == actualizado.id) actualizado else it }
                        },
                        onEliminar = { borrador = borrador.filterNot { it.id == incidente.id } },
                    )
                    HorizontalDivider(Modifier.padding(vertical = 12.dp))
                }
            }
            Button(
                onClick = { borrador = borrador + RegistroIncidencia(siguienteId++) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF278287),
                    contentColor = Color.White,
                ),
            ) {
                Text("+ Agregar Nuevo Incidente")
            }
        }
    }
}

@Composable
private fun FilaIncidente(
    incidente: RegistroIncidencia,
    mostrarErrores: Boolean,
    onCambio: (RegistroIncidencia) -> Unit,
    onEliminar: () -> Unit,
) {
    var menuAbierto by remember { mutableStateOf(false) }
    val colores = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier.weight(0.44f).height(156.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Box {
                    FilledTonalButton(
                        onClick = { menuAbierto = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
                        Text(
                            text = incidente.tipo.ifEmpty { "Elija tipo" },
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        tiposIncidente.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    onCambio(incidente.copy(tipo = tipo))
                                    menuAbierto = false
                                },
                            )
                        }
                    }
                }
                if (mostrarErrores && incidente.tipo.isEmpty() && incidente.descripcion.isNotBlank()) {
                    Text("Elija un tipo", color = colores.error, style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(
                onClick = onEliminar,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colores.error,
                    contentColor = colores.onError,
                ),
            ) {
                Text("Eliminar Incidente", style = MaterialTheme.typography.labelSmall)
            }
        }
        Column(Modifier.weight(0.56f)) {
            val faltaDescripcion = mostrarErrores && incidente.tipo.isNotEmpty() && incidente.descripcion.isBlank()
            OutlinedTextField(
                value = incidente.descripcion,
                onValueChange = { onCambio(incidente.copy(descripcion = it)) },
                modifier = Modifier.fillMaxWidth().height(156.dp).semantics {
                    contentDescription = "Descripción del incidente"
                },
                isError = faltaDescripcion,
                shape = RectangleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colores.surfaceContainerLowest,
                    unfocusedContainerColor = colores.surfaceContainerLowest,
                ),
            )
            if (faltaDescripcion) {
                Text("Escriba una descripción", color = colores.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
