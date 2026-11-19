package mx.tec.app_sonrisas.ui.screens.inferior

import android.app.DatePickerDialog
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
import androidx.compose.material3.OutlinedButton
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
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.viewmodel.RegistroIncidencia
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFechaIncidente = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private val tiposIncidente = listOf(
    "Personal de Sucursal",
    "Conflicto entre niños",
    "Equipo electrónico dañado",
    "Objetos personales",
    "Material dañado",
)

@Composable
fun IncidenciasScreen(
    empleados: List<Employee>,
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
                    val registros = borrador.filter { it.tieneDatos }
                    mostrarErrores = registros.any {
                        it.tipo.isEmpty() || it.descripcion.isBlank() ||
                            it.empleadoId == null || it.fecha == null
                    }
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
                        empleados = empleados,
                        mostrarErrores = mostrarErrores && incidente.tieneDatos,
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
    empleados: List<Employee>,
    mostrarErrores: Boolean,
    onCambio: (RegistroIncidencia) -> Unit,
    onEliminar: () -> Unit,
) {
    var menuAbierto by remember { mutableStateOf(false) }
    var menuEmpleadosAbierto by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val empleado = empleados.find { it.id == incidente.empleadoId }
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
                if (mostrarErrores && incidente.tipo.isEmpty()) {
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
            val faltaDescripcion = mostrarErrores && incidente.descripcion.isBlank()
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

    Column(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text("Empleado involucrado", style = MaterialTheme.typography.titleSmall)
        Box {
            OutlinedButton(
                onClick = { menuEmpleadosAbierto = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = empleados.isNotEmpty(),
            ) {
                Text(
                    empleado?.let { "${it.name} (#${it.id})" }
                        ?: incidente.empleadoId?.let { "Empleado #$it" }
                        ?: "Seleccionar empleado",
                )
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = menuEmpleadosAbierto,
                onDismissRequest = { menuEmpleadosAbierto = false },
            ) {
                empleados.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text("${opcion.name} (#${opcion.id})") },
                        onClick = {
                            onCambio(incidente.copy(empleadoId = opcion.id))
                            menuEmpleadosAbierto = false
                        },
                    )
                }
            }
        }
        if (empleados.isEmpty()) {
            Text("No hay empleados disponibles", style = MaterialTheme.typography.bodySmall)
        } else if (mostrarErrores && incidente.empleadoId == null) {
            Text("Selecciona un empleado", color = colores.error, style = MaterialTheme.typography.bodySmall)
        }

        Text("Fecha", modifier = Modifier.padding(top = 8.dp), style = MaterialTheme.typography.titleSmall)
        OutlinedButton(
            onClick = {
                val fechaInicial = incidente.fecha ?: LocalDate.now()
                DatePickerDialog(
                    contexto,
                    { _, anio, mes, dia ->
                        onCambio(incidente.copy(fecha = LocalDate.of(anio, mes + 1, dia)))
                    },
                    fechaInicial.year,
                    fechaInicial.monthValue - 1,
                    fechaInicial.dayOfMonth,
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(incidente.fecha?.format(formatoFechaIncidente) ?: "Seleccionar fecha")
        }
        if (mostrarErrores && incidente.fecha == null) {
            Text("Selecciona una fecha", color = colores.error, style = MaterialTheme.typography.bodySmall)
        }
    }
}
