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
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.CampoFormulario
import mx.tec.app_sonrisas.ui.components.SelectorDesplegable

// Datos de ejemplo: todavía no hay base de datos.
private val tiposDePersonal = listOf("Asistente", "Educadora", "Cocina", "Servicios")
private val salas = listOf("Sala 1", "Sala 2", "Sala 3")

// Formulario para agregar un empleado a la sucursal.
// Por ahora "Guardar empleado" solo valida, avisa y navega (no se guarda en ningún lado).
@Composable
fun AgregarEmpleadoScreen(
    onGuardar: () -> Unit = {},
) {
    val contexto = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf(tiposDePersonal.first()) }
    var sala by remember { mutableStateOf(salas.first()) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        CampoFormulario(
            etiqueta = "Nombre completo",
            valor = nombre,
            onCambio = { nombre = it },
        )
        Spacer(Modifier.height(20.dp))
        SelectorDesplegable(
            etiqueta = "Tipo de personal",
            opciones = tiposDePersonal,
            seleccion = tipo,
            onSeleccion = { tipo = it },
        )
        Spacer(Modifier.height(20.dp))
        SelectorDesplegable(
            etiqueta = "Sala asignada",
            opciones = salas,
            seleccion = sala,
            onSeleccion = { sala = it },
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
                if (nombre.isBlank()) {
                    error = "Escribe el nombre completo del empleado"
                } else {
                    error = null
                    Toast.makeText(contexto, "Empleado \"${nombre.trim()}\" guardado", Toast.LENGTH_SHORT).show()
                    onGuardar()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Guardar empleado")
        }
    }
}
