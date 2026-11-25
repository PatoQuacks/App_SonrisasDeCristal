package mx.tec.app_sonrisas.ui.screens.inferior

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.tec.app_sonrisas.ui.components.EncabezadoPantalla
import mx.tec.app_sonrisas.ui.components.TituloSeccion
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// Datos de ejemplo: todavía no hay base de datos.
private const val PRESENTES = 16
private const val TARDANZAS = 2
private const val AUSENTES = 3

private val ColorTardanza = Color(0xFF00B8F0)

// Fecha en español, por ejemplo "Domingo 22 de noviembre".
private val espanolMexico: Locale = Locale.forLanguageTag("es-MX")
private val formatoFechaInicio = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", espanolMexico)

// Vista general de la estancia para el responsable (pantalla principal después del login).
@Composable
fun InicioScreen(
    nombreResponsable: String,
    plantel: String,
    onVerAusencias: () -> Unit = {},
    onVerCapacitaciones: () -> Unit = {},
) {
    val contexto = LocalContext.current
    val fechaHoy = LocalDate.now()
        .format(formatoFechaInicio)
        .replaceFirstChar { it.titlecase(espanolMexico) }
    val colorPresente = MaterialTheme.colorScheme.secondary
    val colorAusente = MaterialTheme.colorScheme.primary
    val total = PRESENTES + TARDANZAS + AUSENTES

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPantalla(texto = plantel) {
            Button(
                onClick = {
                    Toast.makeText(contexto, "Reporte descargado (demostración)", Toast.LENGTH_SHORT).show()
                },
            ) {
                Text("Descargar reporte")
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            // Responsable y fecha
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                DatoGeneral(etiqueta = "Responsable:", valor = nombreResponsable)
                HorizontalDivider()
                DatoGeneral(etiqueta = "Fecha:", valor = fechaHoy)
                HorizontalDivider()
            }
            Spacer(Modifier.height(8.dp))

            // Asistencia de hoy
            TituloSeccion("Asistencia de hoy")
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Indicador("P", PRESENTES, "Presentes", colorPresente, Modifier.weight(1f))
                    Indicador("T", TARDANZAS, "Tardanzas", ColorTardanza, Modifier.weight(1f))
                    Indicador("A", AUSENTES, "Ausentes", colorAusente, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                // Barra proporcional: presentes, tardanzas y ausentes.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box(Modifier.weight(PRESENTES.toFloat()).fillMaxHeight().background(colorPresente))
                    Box(Modifier.weight(TARDANZAS.toFloat()).fillMaxHeight().background(ColorTardanza))
                    Box(Modifier.weight(AUSENTES.toFloat()).fillMaxHeight().background(colorAusente))
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${PRESENTES + TARDANZAS} de $total empleados registrados",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Turnos de hoy
            TituloSeccion("Turnos de hoy")
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                RenglonTurno(turno = "Matutino (7:00 – 15:00)", cobertura = "12 / 13")
                HorizontalDivider()
                RenglonTurno(turno = "Vespertino (13:00 – 20:00)", cobertura = "6 / 8")
                HorizontalDivider()
            }
            Spacer(Modifier.height(8.dp))

            // Pendientes: cada renglón lleva a otra pantalla.
            TituloSeccion("Pendientes")
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                RenglonPendiente(texto = "$AUSENTES ausencias sin justificar", onClick = onVerAusencias)
                HorizontalDivider()
                RenglonPendiente(texto = "2 capacitaciones vencen esta semana", onClick = onVerCapacitaciones)
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun DatoGeneral(etiqueta: String, valor: String) {
    Row(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

// Cuadro de color con una letra, y al lado el número con su descripción.
@Composable
private fun Indicador(
    letra: String,
    cantidad: Int,
    descripcion: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = letra, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                text = cantidad.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun RenglonTurno(turno: String, cobertura: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = turno,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = cobertura,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}

@Composable
private fun RenglonPendiente(texto: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Ver detalle",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
