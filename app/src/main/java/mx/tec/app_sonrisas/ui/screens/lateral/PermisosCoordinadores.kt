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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.components.TituloSeccion
import mx.tec.app_sonrisas.ui.viewmodel.Coordinador

// Permisos del coordinador seleccionado en la pantalla de Coordinadores.
// Las casillas trabajan sobre una copia: los cambios solo se aplican al tocar
// "Guardar Cambios"; "Cancelar" los descarta.
@Composable
fun PermisosCoordinadoresScreen(
    coordinador: Coordinador,
    gruposDePermisos: List<Pair<String, List<String>>>,
    permisosIniciales: Set<String>,
    onCancelar: () -> Unit = {},
    onGuardar: (Set<String>) -> Unit = {},
) {
    val contexto = LocalContext.current

    // Copia de trabajo. Se reinicia si cambia el coordinador.
    var activos by remember(coordinador.nombre) { mutableStateOf(permisosIniciales) }
    val totalPermisos = gruposDePermisos.sumOf { (_, lista) -> lista.size }

    fun cambiarPermiso(permiso: String, activo: Boolean) {
        activos = if (activo) activos + permiso else activos - permiso
    }

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
                    Toast.makeText(
                        contexto,
                        "Permisos de ${coordinador.nombre} guardados",
                        Toast.LENGTH_SHORT,
                    ).show()
                    onGuardar(activos)
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
                    text = "${coordinador.nombre} (${coordinador.area})",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "${activos.size} de $totalPermisos activos",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            gruposDePermisos.forEach { (grupo, lista) ->
                TituloSeccion(grupo)
                lista.forEach { permiso ->
                    val activo = permiso in activos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { cambiarPermiso(permiso, !activo) }
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
                            onCheckedChange = { marcado -> cambiarPermiso(permiso, marcado) },
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
