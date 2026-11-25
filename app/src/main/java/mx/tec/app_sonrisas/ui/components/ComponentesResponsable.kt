package mx.tec.app_sonrisas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

// Componentes compartidos por las pantallas del Responsable
// (Inicio, Coordinadores, Permisos, Agregar sala y Agregar empleado).

// Franja del color de la barra superior, pegada debajo de ella.
// Muestra un texto a la izquierda y botones de acción a la derecha.
@Composable
fun EncabezadoPantalla(
    texto: String = "",
    acciones: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (texto.isNotEmpty()) {
            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onSecondary,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
        }
        acciones()
    }
}

// Banda de color claro que separa las secciones de una pantalla.
@Composable
fun TituloSeccion(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

// Campo de texto con su etiqueta arriba, como en los formularios del diseño.
// - error: si no es null, el campo se pinta en rojo y muestra el mensaje debajo.
// - ayuda: texto gris debajo del campo (se reemplaza por el error cuando hay uno).
// - esContrasena: oculta lo que se escribe y agrega el botón "Ver / Ocultar".
@Composable
fun CampoFormulario(
    etiqueta: String,
    valor: String,
    onCambio: (String) -> Unit,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    error: String? = null,
    ayuda: String? = null,
    esContrasena: Boolean = false,
    capitalizacion: KeyboardCapitalization = KeyboardCapitalization.None,
    habilitado: Boolean = true,
) {
    var contrasenaVisible by remember { mutableStateOf(false) }
    val mensaje = error ?: ayuda

    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onCambio,
            singleLine = true,
            enabled = habilitado,
            isError = error != null,
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (esContrasena) KeyboardType.Password else tipoTeclado,
                capitalization = capitalizacion,
            ),
            visualTransformation = if (esContrasena && !contrasenaVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (esContrasena) {
                {
                    TextButton(
                        onClick = { contrasenaVisible = !contrasenaVisible },
                        enabled = habilitado,
                    ) {
                        Text(if (contrasenaVisible) "Ocultar" else "Ver")
                    }
                }
            } else {
                null
            },
            supportingText = if (mensaje != null) {
                { Text(mensaje) }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// Lista desplegable con su etiqueta arriba.
// - seleccion vacía ("") muestra textoVacio en gris, para obligar a elegir una opción.
// - error: si no es null, el campo se pinta en rojo y muestra el mensaje debajo.
@Composable
fun SelectorDesplegable(
    etiqueta: String,
    opciones: List<String>,
    seleccion: String,
    onSeleccion: (String) -> Unit,
    error: String? = null,
    textoVacio: String = "Selecciona una opción",
    habilitado: Boolean = true,
) {
    var abierto by remember { mutableStateOf(false) }

    Column {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(Modifier.height(6.dp))
        Box {
            OutlinedTextField(
                value = seleccion,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                enabled = habilitado,
                isError = error != null,
                placeholder = { Text(textoVacio) },
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Abrir lista")
                },
                supportingText = if (error != null) {
                    { Text(error) }
                } else {
                    null
                },
                modifier = Modifier.fillMaxWidth(),
            )
            // Capa transparente encima del campo: al tocarla se abre la lista.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = habilitado) { abierto = true }
            )
            DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onSeleccion(opcion)
                            abierto = false
                        },
                    )
                }
            }
        }
    }
}
