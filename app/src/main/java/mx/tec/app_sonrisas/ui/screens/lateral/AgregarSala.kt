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

// Formulario para agregar una sala a la sucursal.
// "Guardar sala" valida los datos y se los pasa a onGuardar, que la agrega a la lista de salas.
// La lista de coordinadores es la misma que se ve en la pantalla de Coordinadores.
@Composable
fun AgregarSalaScreen(
    coordinadores: List<String>,
    // Regresa true si ya existe una sala con ese nombre (para no duplicarlas).
    nombreRepetido: (String) -> Boolean = { false },
    onGuardar: (nombre: String, personalMinimo: Int, coordinador: String) -> Unit = { _, _, _ -> },
) {
    val contexto = LocalContext.current

    var coordinador by remember { mutableStateOf(coordinadores.firstOrNull().orEmpty()) }
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
            // Solo se aceptan dígitos (máximo 3).
            onCambio = { texto -> personalMinimo = texto.filter { it.isDigit() }.take(3) },
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
                val minimo = personalMinimo.toIntOrNull()
                if (nombre.isBlank() || minimo == null) {
                    error = "Escribe el nombre de la sala y el personal mínimo"
                } else if (minimo < 1) {
                    error = "El personal mínimo debe ser de al menos 1 persona"
                } else if (nombreRepetido(nombre)) {
                    error = "Ya existe una sala llamada \"${nombre.trim()}\""
                } else {
                    error = null
                    Toast.makeText(contexto, "Sala \"${nombre.trim()}\" guardada", Toast.LENGTH_SHORT).show()
                    onGuardar(nombre.trim(), minimo, coordinador)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar sala")
        }
    }
}
