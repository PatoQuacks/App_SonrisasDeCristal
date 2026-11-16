package mx.tec.app_sonrisas.ui.screens.director

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.app_sonrisas.ui.viewmodel.DirectorViewModel

@Composable
fun ReporteAsistenciaScreen(
    viewModel: DirectorViewModel,
    onNavigateToSucursales: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val reporte = uiState.reporteAsistencia

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- ENCABEZADO SUPERIOR (Solo botones de navegación) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.secondary)
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { onNavigateToSucursales() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Resumen de Cobertura", color = Color.White)
                }
                Button(
                    onClick = { /* Reportes */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Reportes", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
            }
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            // (Sucursal y Mes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "SUCURSAL", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = uiState.sucursalSeleccionadaFiltro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }
                }

                OutlinedCard(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.outlinedCardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "MES", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                            Text(text = uiState.mesSeleccionadoFiltro, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RESUMEN MENSUAL ${uiState.mesSeleccionadoFiltro.uppercase()}",
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MetricRow(title = "Faltas totales", value = reporte?.faltasTotales?.toString() ?: "0")
                    Divider(color = Color.LightGray.copy(alpha = 0.2f))
                    MetricRow(title = "Retardos", value = reporte?.retardos?.toString() ?: "0")
                    Divider(color = Color.LightGray.copy(alpha = 0.2f))
                    MetricRow(title = "Altas", value = reporte?.altas?.toString() ?: "0")
                    Divider(color = Color.LightGray.copy(alpha = 0.2f))
                    MetricRow(title = "Bajas", value = reporte?.bajas?.toString() ?: "0")
                }
            }

            Spacer(modifier = Modifier.weight(1f))


            Button(
                onClick = { /* Accion futura de descarga */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Descargar reporte completo", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun MetricRow(title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 15.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}