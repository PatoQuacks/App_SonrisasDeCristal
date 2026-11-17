package mx.tec.app_sonrisas.ui.screens.lateral

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.components.TituloSeccion

// Datos de ejemplo: grupos de permisos y si vienen activos o no.
private val gruposDePermisos = listOf(
    "Personal" to listOf(
        "Ver listado de empleados" to true,
        "Tomar asistencia" to true,
        "Registrar incidencias" to true,
        "Justificar faltas y retardos" to false,
    ),
    "Turnos" to listOf(
        "Editar turnos de su equipo" to true,
    ),
    "Información" to listOf(
        "Descargar reportes del día" to false,
        "Ver datos personales" to false,
    ),
)

// Permisos del coordinador seleccionado. Las casillas se pueden marcar y desmarcar;
// por ahora los cambios solo viven en esta pantalla (todavía no hay base de datos).
@Composable
fun PermisosCoordinadoresScreen(
    onCancelar: () -> Unit = {},
    onGuardar: () -> Unit = {},
) {
    val contexto = LocalContext.current

    // permiso -> activo
    val permisos = remember {
        mutableStateMapOf<String, Boolean>().apply {
            gruposDePermisos.forEach { (_, lista) -> putAll(lista) }
        }
    }
    val activos = permisos.values.count { it }

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPantalla {
            Button(
                onClick = onCancelar,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary,
                ),
                modifier = Modifier.weight(1f),
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    Toast.makeText(contexto, "Permisos guardados", Toast.LENGTH_SHORT).show()
                    onGuardar()
                },
                modifier = Modifier.weight(1f),
            ) {
                Text("Guardar Cambios")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Patricio (Turno matutino)",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "$activos de ${permisos.size} activos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            gruposDePermisos.forEach { (grupo, lista) ->
                TituloSeccion(grupo)
                lista.forEach { (permiso, _) ->
                    val activo = permisos[permiso] == true
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { permisos[permiso] = !activo }
                            .padding(start = 16.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = permiso,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f),
                        )
                        Checkbox(
                            checked = activo,
                            onCheckedChange = { permisos[permiso] = it },
                        )
                    }
                }
            }

            Text(
                text = "Los cambios de permisos quedan registrados en la bitácora con fecha, hora y usuario.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
