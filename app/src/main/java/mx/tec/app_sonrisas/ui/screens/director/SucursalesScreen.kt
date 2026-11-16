package mx.tec.app_sonrisas.ui.screens.director

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.tec.app_sonrisas.data.model.Sucursal
import mx.tec.app_sonrisas.ui.viewmodel.DirectorViewModel

@Composable
fun SucursalesScreen(
    viewModel: DirectorViewModel,
    onNavigateToReportes: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val sucursales = uiState.sucursales

    val StatusGreen = Color(0xFF2E7D32)
    val StatusRed = MaterialTheme.colorScheme.error

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
                    onClick = { /* Ya estamos en Cobertura/Sucursales */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Resumen de Cobertura", color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { onNavigateToReportes() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = "Reportes", color = Color.White)
                }
            }
        }

        // --- CUERPO PRINCIPAL (LISTA) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Sucursales de la fundación",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Consulta las sucursales y su personal",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))


            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(sucursales) { sucursal ->
                    SucursalItem(
                        sucursal = sucursal,
                        colorVerde = StatusGreen,
                        colorRojo = StatusRed
                    )
                    Divider(color = Color.LightGray.copy(alpha = 0.3f))
                }
            }
        }
    }
}


@Composable
fun SucursalItem(
    sucursal: Sucursal,
    colorVerde: Color,
    colorRojo: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Accion opcional al tocar una sucursal */ }
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = sucursal.nombre,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${sucursal.empleadosActual}/${sucursal.empleadosTotal} empleados",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(4.dp))


            Text(
                text = sucursal.estado,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (sucursal.esCoberturaCompleta) colorVerde else colorRojo
            )
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = "Ver detalles",
            tint = Color.Gray
        )
    }
}