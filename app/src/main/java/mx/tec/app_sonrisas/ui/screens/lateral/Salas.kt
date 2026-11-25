package mx.tec.app_sonrisas.ui.screens.lateral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.data.model.Employee
import mx.tec.app_sonrisas.data.model.Sala
import mx.tec.app_sonrisas.navigation.Pantalla
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla

// Listado de las salas de la sucursal. Solo aparecen las salas que se han agregado.
// El botón "Agregar sala" es el botón flotante de PrincipalScreen.
@Composable
fun SalasScreen(
    salas: List<Sala>,
    empleados: List<Employee>,
    plantel: String = "",
) {
    val nombresEmpleados = empleados.associate { it.id to it.name }
    val resumen = if (salas.size == 1) "1 sala registrada" else "${salas.size} salas registradas"

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPantalla(texto = if (plantel.isBlank()) resumen else "$plantel · $resumen")

        if (salas.isEmpty()) {
            SinSalas()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // Espacio al final para que el botón flotante no tape la última sala.
                contentPadding = PaddingValues(bottom = 88.dp),
            ) {
                items(salas, key = { it.id }) { sala ->
                    SalaItem(sala = sala, nombresEmpleados = nombresEmpleados)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Composable
private fun SinSalas() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Pantalla.Salas.icono,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Aún no hay salas registradas",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Toca \"Agregar sala\" para crear la primera.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun SalaItem(
    sala: Sala,
    nombresEmpleados: Map<Int, String>,
) {
    val colorTexto = MaterialTheme.colorScheme.onPrimaryContainer

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = sala.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colorTexto,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "ID ${sala.id}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (sala.coordinadorEncargado.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Coordinador: ${sala.coordinadorEncargado}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorTexto,
            )
        }

        Spacer(Modifier.height(4.dp))
        Text(
            text = "Personal: ${sala.totalEmpleados} · Mínimo requerido: ${sala.personalMinimo}",
            style = MaterialTheme.typography.bodyMedium,
            color = colorTexto,
        )
        Text(
            text = if (sala.debajoDelMinimo) {
                "Faltan ${sala.faltantes} para cubrir el mínimo"
            } else {
                "Personal mínimo cubierto"
            },
            style = MaterialTheme.typography.bodySmall,
            color = if (sala.debajoDelMinimo) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.secondary
            },
        )

        Spacer(Modifier.height(8.dp))
        if (sala.empleadosIds.isEmpty()) {
            Text(
                text = "Sin empleados asignados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            Text(
                text = "Empleados en la sala",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = colorTexto,
            )
            sala.empleadosIds.sorted().forEach { id ->
                Text(
                    text = "#$id · ${nombresEmpleados[id] ?: "Empleado sin nombre"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
