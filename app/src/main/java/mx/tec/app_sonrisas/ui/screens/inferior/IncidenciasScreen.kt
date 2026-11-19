package mx.tec.app_sonrisas.ui.screens.inferior

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.viewmodel.RegistroIncidencia
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val formatoFechaIncidente = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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
                        it.descripcion.isBlank() || it.empleadoId == null || it.fecha == null
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
            borrador.forEachIndexed { indice, incidente ->
                key(incidente.id) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Incidente ${indice + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f),
                        )
                        Button(
                            onClick = { borrador = borrador.filterNot { it.id == incidente.id } },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colores.error,
                                contentColor = colores.onError,
                            ),
                        ) {
                            Text("Eliminar Incidente")
                        }
                    }
                    FilaIncidente(
                        incidente = incidente,
                        empleados = empleados,
                        mostrarErrores = mostrarErrores && incidente.tieneDatos,
                        onCambio = { actualizado ->
                            borrador = borrador.map { if (it.id == actualizado.id) actualizado else it }
                        },
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
) {
    var menuEmpleadosAbierto by remember { mutableStateOf(false) }
    val contexto = LocalContext.current
    val empleado = empleados.find { it.id == incidente.empleadoId }
    val colores = MaterialTheme.colorScheme
    Column(Modifier.fillMaxWidth().padding(top = 4.dp)) {
        val faltaDescripcion = mostrarErrores && incidente.descripcion.isBlank()
        OutlinedTextField(
            value = incidente.descripcion,
            onValueChange = { onCambio(incidente.copy(descripcion = it)) },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 132.dp),
            isError = faltaDescripcion,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = colores.surfaceContainerLowest,
                unfocusedContainerColor = colores.surfaceContainerLowest,
            ),
        )
        if (faltaDescripcion) {
            Text("Escriba una descripción", color = colores.error, style = MaterialTheme.typography.bodySmall)
        }

        val campoEmpleado: @Composable (Modifier, String) -> Unit = { modifier, textoSelector ->
            Column(modifier) {
                Text(
                    "Empleado involucrado",
                    style = MaterialTheme.typography.titleSmall,
                )
                Box {
                    OutlinedButton(
                        onClick = { menuEmpleadosAbierto = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = empleados.isNotEmpty(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                    ) {
                        Text(
                            empleado?.let { "${it.name} (#${it.id})" }
                                ?: incidente.empleadoId?.let { "Empleado #$it" }
                                ?: textoSelector,
                            modifier = Modifier.weight(1f, fill = false),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
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
            }
        }
        val campoFecha: @Composable (Modifier, String) -> Unit = { modifier, textoSelector ->
            Column(modifier) {
                Text("Fecha", style = MaterialTheme.typography.titleSmall)
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
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
                ) {
                    Text(incidente.fecha?.format(formatoFechaIncidente) ?: textoSelector)
                }
                if (mostrarErrores && incidente.fecha == null) {
                    Text("Selecciona una fecha", color = colores.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        BoxWithConstraints(Modifier.fillMaxWidth().padding(top = 12.dp)) {
            val densidad = LocalDensity.current
            val medidorTexto = rememberTextMeasurer()
            val estiloBoton = MaterialTheme.typography.labelLarge
            val enFila = maxWidth >= 336.dp && densidad.fontScale <= 1.15f
            val separacion = 8.dp
            val anchoSelector = if (enFila) {
                with(densidad) { ((constraints.maxWidth - separacion.roundToPx()) / 2).toDp() }
            } else {
                maxWidth
            }
            val anchoTextoEmpleado = with(densidad) { (anchoSelector - 34.dp).roundToPx() }
            val anchoTextoFecha = with(densidad) { (anchoSelector - 16.dp).roundToPx() }
            val textoEmpleado = if (
                medidorTexto.measure("Seleccionar empleado", style = estiloBoton, softWrap = false).size.width <= anchoTextoEmpleado
            ) "Seleccionar empleado" else "Elegir empleado"
            val textoFecha = if (
                medidorTexto.measure("Seleccionar fecha", style = estiloBoton, softWrap = false).size.width <= anchoTextoFecha
            ) "Seleccionar fecha" else "Elegir fecha"

            if (enFila) {
                Row(horizontalArrangement = Arrangement.spacedBy(separacion)) {
                    campoEmpleado(Modifier.width(anchoSelector), textoEmpleado)
                    campoFecha(Modifier.width(anchoSelector), textoFecha)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(separacion)) {
                    campoEmpleado(Modifier.fillMaxWidth(), textoEmpleado)
                    campoFecha(Modifier.fillMaxWidth(), textoFecha)
                }
            }
        }
    }
}
