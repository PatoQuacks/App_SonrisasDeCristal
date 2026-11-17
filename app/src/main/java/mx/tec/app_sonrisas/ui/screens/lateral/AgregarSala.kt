package mx.tec.app_sonrisas.ui.screens.lateral

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.CampoFormulario
import mx.tec.app_sonrisas.ui.components.SelectorDesplegable

// Datos de ejemplo: todavía no hay base de datos.
private val coordinadores = listOf("Coordinador A", "Coordinador B", "Coordinador C")

// Formulario para agregar una sala a la sucursal.
// Por ahora "Guardar sala" solo valida, avisa y navega (no se guarda en ningún lado).
@Composable
fun AgregarSalaScreen(
    onGuardar: () -> Unit = {},
) {
    val contexto = LocalContext.current

    var coordinador by remember { mutableStateOf(coordinadores.first()) }
    var nombre by remember { mutableStateOf("") }
    var personalMinimo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        SelectorDesplegable(
            etiqueta = "Coordinador encargado",
            opciones = coordinadores,
            seleccion = coordinador,
            onSeleccion = { coordinador = it },
        )
        Spacer(Modifier.height(20.dp))
        CampoFormulario(
            etiqueta = "Nombre de la sala",
            valor = nombre,
            onCambio = { nombre = it },
        )
        Spacer(Modifier.height(20.dp))
        CampoFormulario(
            etiqueta = "Personal mínimo requerido",
            valor = personalMinimo,
            // Solo se aceptan dígitos.
            onCambio = { texto -> personalMinimo = texto.filter { it.isDigit() } },
            tipoTeclado = KeyboardType.Number,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Se avisa al coordinador si la asistencia baja del mínimo.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        error?.let { mensaje ->
            Spacer(Modifier.height(12.dp))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                if (nombre.isBlank() || personalMinimo.isBlank()) {
                    error = "Escribe el nombre de la sala y el personal mínimo"
                } else {
                    error = null
                    Toast.makeText(contexto, "Sala \"${nombre.trim()}\" guardada", Toast.LENGTH_SHORT).show()
                    onGuardar()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar sala")
        }
    }
}
