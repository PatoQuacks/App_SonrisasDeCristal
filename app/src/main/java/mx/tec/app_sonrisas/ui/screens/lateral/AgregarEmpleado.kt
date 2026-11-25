package mx.tec.app_sonrisas.ui.screens.lateral

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import mx.tec.app_sonrisas.ui.components.CampoFormulario
import mx.tec.app_sonrisas.ui.components.SelectorDesplegable
import mx.tec.app_sonrisas.ui.components.TituloSeccion
import mx.tec.app_sonrisas.ui.viewmodel.AgregarEmpleadoViewModel
import mx.tec.app_sonrisas.ui.viewmodel.NuevoEmpleado

// Por el momento solo se pide un mínimo de caracteres.
private const val MIN_CARACTERES_CONTRASENA = 8

// Formato básico de correo: nombre@dominio.ext
private val formatoCorreo = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

// Formulario para dar de alta a un empleado de la sucursal. Está en tres partes:
//   1. Datos del empleado: nombre, turno (matutino o vespertino), tipo de personal y sala.
//   2. Acceso a la app: usuario (correo) y contraseña.
//   3. Botones: Cancelar / Guardar empleado.
// Al guardar se valida el formulario y luego se pregunta a la API si el usuario ya existe
// (AgregarEmpleadoViewModel). Si ya existe, se avisa debajo del campo de usuario;
// si no, se entrega el empleado con onEmpleadoAgregado y la navegación regresa a Empleados.
@Composable
fun AgregarEmpleadoScreen(
    token: String?,
    tiposDePersonal: Map<Int, String>,
    salas: Map<Int, String>,
    horarios: Map<Int, String>,
    correosRegistrados: Set<String>,
    onCancelar: () -> Unit,
    onEmpleadoAgregado: (NuevoEmpleado) -> Unit,
    // Se usa cuando todavía no hay salas registradas.
    onAgregarSala: () -> Unit = {},
    viewModel: AgregarEmpleadoViewModel = viewModel(),
) {
    // Datos del empleado (rememberSaveable: no se pierden al girar el teléfono).
    var nombre by rememberSaveable { mutableStateOf("") }
    var tipoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var salaId by rememberSaveable { mutableStateOf<Int?>(null) }
    var horarioId by rememberSaveable { mutableStateOf<Int?>(null) }

    // Acceso a la app.
    var correo by rememberSaveable { mutableStateOf("") }
    // La contraseña usa remember y no rememberSaveable a propósito: así no queda
    // en el estado que Android respalda cuando la app pasa a segundo plano.
    var contrasena by remember { mutableStateOf("") }

    // Los errores se muestran a partir del primer intento de guardar.
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }

    val errorNombre = if (nombre.isBlank()) "Escribe el nombre completo" else null
    val errorTipo = if (tipoId == null) "Elige el tipo de personal" else null
    val errorSala = if (salaId == null) "Elige la sala" else null
    val errorHorario = if (horarioId == null) "Elige el turno" else null
    val errorCorreo = when {
        correo.isBlank() -> "Escribe el usuario (correo) del empleado"
        !formatoCorreo.matches(correo) -> "Escribe un correo válido, por ejemplo nombre@correo.com"
        else -> null
    }
    val errorContrasena = if (contrasena.length < MIN_CARACTERES_CONTRASENA) {
        "La contraseña debe tener al menos $MIN_CARACTERES_CONTRASENA caracteres"
    } else {
        null
    }
    val formularioValido = listOf(
        errorNombre, errorTipo, errorSala, errorHorario, errorCorreo, errorContrasena,
    ).all { it == null }

    // Cuando el ViewModel confirma que el usuario no existe, se entrega el empleado.
    val empleadoListo = viewModel.empleadoListo
    LaunchedEffect(empleadoListo) {
        if (empleadoListo != null) {
            viewModel.empleadoEntregado()
            onEmpleadoAgregado(empleadoListo)
        }
    }

    val habilitado = !viewModel.verificando
    val colores = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // 1. Datos del empleado
        TituloSeccion("Datos del empleado")
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CampoFormulario(
                etiqueta = "Nombre completo",
                valor = nombre,
                onCambio = { nombre = it },
                error = errorNombre.siSeMuestra(mostrarErrores),
                capitalizacion = KeyboardCapitalization.Words,
                habilitado = habilitado,
            )
            SelectorDesplegable(
                etiqueta = "Turno",
                opciones = horarios.values.toList(),
                seleccion = horarioId?.let { horarios[it] }.orEmpty(),
                onSeleccion = { horarioId = horarios.idDe(it) },
                error = errorHorario.siSeMuestra(mostrarErrores),
                textoVacio = "Matutino o vespertino",
                habilitado = habilitado,
            )
            SelectorDesplegable(
                etiqueta = "Tipo de personal",
                opciones = tiposDePersonal.values.toList(),
                seleccion = tipoId?.let { tiposDePersonal[it] }.orEmpty(),
                onSeleccion = { tipoId = tiposDePersonal.idDe(it) },
                error = errorTipo.siSeMuestra(mostrarErrores),
                habilitado = habilitado,
            )
            // Solo aparecen las salas que se han registrado en "Agregar sala".
            if (salas.isEmpty()) {
                SinSalasRegistradas(
                    error = errorSala.siSeMuestra(mostrarErrores),
                    habilitado = habilitado,
                    onAgregarSala = onAgregarSala,
                )
            } else {
                SelectorDesplegable(
                    etiqueta = "Sala asignada",
                    opciones = salas.values.toList(),
                    seleccion = salaId?.let { salas[it] }.orEmpty(),
                    onSeleccion = { salaId = salas.idDe(it) },
                    error = errorSala.siSeMuestra(mostrarErrores),
                    habilitado = habilitado,
                )
            }
        }

        // 2. Acceso a la app
        TituloSeccion("Acceso a la app")
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CampoFormulario(
                etiqueta = "Usuario (correo)",
                valor = correo,
                onCambio = { texto ->
                    // Sin espacios; si cambia el usuario, se quita el aviso de "ya existe".
                    correo = texto.filterNot { it.isWhitespace() }
                    viewModel.limpiarErrorUsuario()
                },
                tipoTeclado = KeyboardType.Email,
                error = viewModel.errorUsuario ?: errorCorreo.siSeMuestra(mostrarErrores),
                ayuda = "Con este usuario el empleado iniciará sesión",
                habilitado = habilitado,
            )
            CampoFormulario(
                etiqueta = "Contraseña",
                valor = contrasena,
                onCambio = { contrasena = it },
                error = errorContrasena.siSeMuestra(mostrarErrores),
                ayuda = "Mínimo $MIN_CARACTERES_CONTRASENA caracteres",
                esContrasena = true,
                habilitado = habilitado,
            )
        }

        // Errores de conexión o del servidor.
        viewModel.errorGeneral?.let { mensaje ->
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = colores.error,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // 3. Botones
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onCancelar,
                enabled = habilitado,
                modifier = Modifier.weight(1f),
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    mostrarErrores = true
                    if (formularioValido) {
                        viewModel.verificarYAgregar(
                            token = token,
                            nuevo = NuevoEmpleado(
                                nombre = nombre.trim(),
                                correo = correo.lowercase(),
                                roleId = tipoId!!,
                                roomId = salaId!!,
                                scheduleId = horarioId!!,
                            ),
                            correosRegistrados = correosRegistrados,
                        )
                    }
                },
                enabled = habilitado,
                modifier = Modifier.weight(1f),
            ) {
                if (viewModel.verificando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = LocalContentColor.current,
                    )
                } else {
                    Text("Guardar empleado")
                }
            }
        }

        if (viewModel.verificando) {
            Text(
                text = "Verificando usuario… La primera consulta del día puede tardar hasta un minuto.",
                style = MaterialTheme.typography.bodySmall,
                color = colores.onSurfaceVariant,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            )
        }
    }
}

// "Sala asignada" cuando la sucursal todavía no tiene salas: avisa y ofrece crear una.
@Composable
private fun SinSalasRegistradas(
    error: String?,
    habilitado: Boolean,
    onAgregarSala: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Sala asignada",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Text(
            text = "Todavía no hay salas registradas. Agrega una sala para poder asignarla.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedButton(onClick = onAgregarSala, enabled = habilitado) {
            Text("+ Agregar sala")
        }
        error?.let { mensaje ->
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

// Pop-up que se muestra en Empleados después de dar de alta a alguien.
@Composable
fun AvisoEmpleadoAgregado(nombre: String, onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) },
        title = { Text("Empleado agregado") },
        text = { Text("Empleado $nombre agregado exitosamente") },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Aceptar") }
        },
    )
}

// El mensaje de error solo se muestra después del primer intento de guardar.
private fun String?.siSeMuestra(mostrarErrores: Boolean): String? = if (mostrarErrores) this else null

// Busca el id de una opción del catálogo a partir del nombre que se eligió en la lista.
private fun Map<Int, String>.idDe(nombre: String): Int? = entries.firstOrNull { it.value == nombre }?.key
