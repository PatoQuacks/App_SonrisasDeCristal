package mx.tec.app_sonrisas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.tec.app_sonrisas.navigation.AppNavigation
import mx.tec.app_sonrisas.ui.theme.AppBaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Si tu proyecto tiene otro nombre, el tema también se llama distinto
            // (por ejemplo MiProyectoTheme). Revisa ui/theme/Theme.kt.
            AppBaseTheme {
                AppNavigation()
            }
        }
    }
}
