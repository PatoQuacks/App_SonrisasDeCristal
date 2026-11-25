package mx.tec.app_sonrisas.ui.screens.lateral

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.viewmodel.Coordinador

// Listado de coordinadores del responsable.
// Cada renglón se abre o se cierra al tocarlo. El interruptor "Mostrar incidentes"
// cambia entre las dos versiones del diseño (con y sin incidentes registrados).
// "ver permisos" avisa cuál coordinador se tocó, para abrir SUS permisos.
@Composable
fun CoordinadoresScreen(
    coordinadores: List<Coordinador>,
    permisos: Map<String, Set<String>>,
    totalPermisos: Int,
    nombreResponsable: String,
    plantel: String,
    onVerPermisos: (String) -> Unit = {},
    onAsignar: () -> Unit = {},
) {
    // Nombres de los coordinadores cuyo detalle está abierto.
    // rememberSaveable: no se cierran al girar el teléfono.
    var abiertos by rememberSaveable {
        mutableStateOf(setOfNotNull(coordinadores.firstOrNull()?.nombre))
    }
    var mostrarIncidentes by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPantalla(texto = "$nombreResponsable · Responsable de $plantel") {
            Button(onClick = onAsignar) { Text("+ Asignar") }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Mostrar incidentes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Switch(checked = mostrarIncidentes, onCheckedChange = { mostrarIncidentes = it })
        }
        HorizontalDivider()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            coordinadores.forEach { coordinador ->
                val abierto = coordinador.nombre in abiertos
                CoordinadorItem(
                    coordinador = coordinador,
                    permisosActivos = permisos[coordinador.nombre].orEmpty().size,
                    totalPermisos = totalPermisos,
                    abierto = abierto,
                    mostrarIncidentes = mostrarIncidentes,
                    onAlternar = {
                        abiertos = if (abierto) {
                            abiertos - coordinador.nombre
                        } else {
                            abiertos + coordinador.nombre
                        }
                    },
                    onVerPermisos = { onVerPermisos(coordinador.nombre) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun CoordinadorItem(
    coordinador: Coordinador,
    permisosActivos: Int,
    totalPermisos: Int,
    abierto: Boolean,
    mostrarIncidentes: Boolean,
    onAlternar: () -> Unit,
    onVerPermisos: () -> Unit,
) {
    val colorTexto = MaterialTheme.colorScheme.onPrimaryContainer

    Column(modifier = Modifier.fillMaxWidth()) {
        // Renglón del nombre: al tocarlo se abre o se cierra el detalle.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAlternar() }
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = if (abierto) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (abierto) "Cerrar detalle" else "Abrir detalle",
                tint = colorTexto,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${coordinador.nombre} (${coordinador.area})",
                style = MaterialTheme.typography.titleSmall,
                color = colorTexto,
            )
        }

        if (abierto) {
            Column(modifier = Modifier.padding(start = 28.dp, bottom = 14.dp)) {
                Text(
                    text = "Empleados a cargo: ${coordinador.empleadosACargo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorTexto,
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    Text(
                        text = "Asistencia hoy: ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorTexto,
                    )
                    Text(
                        text = "${coordinador.presentesHoy} / ${coordinador.empleadosACargo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Permisos: $permisosActivos de $totalPermisos activos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorTexto,
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = onVerPermisos,
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp),
                    ) {
                        Text("ver permisos", style = MaterialTheme.typography.labelMedium)
                    }
                }

                if (mostrarIncidentes) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Incidentes registrados (${coordinador.incidentes.size})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorTexto,
                    )
                    Spacer(Modifier.height(6.dp))
                    if (coordinador.incidentes.isEmpty()) {
                        Text(
                            text = "Sin incidentes registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorTexto,
                        )
                    } else {
                        coordinador.incidentes.forEach { incidente ->
                            Text(
                                text = incidente,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorTexto,
                            )
                        }
                    }
                }
            }
        }
    }
}
