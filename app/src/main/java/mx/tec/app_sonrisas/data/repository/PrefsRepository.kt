package mx.tec.app_sonrisas.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import mx.tec.app_sonrisas.data.PrefKeys
import mx.tec.app_sonrisas.data.SesionPrefs
import mx.tec.app_sonrisas.data.dataStore

class PrefsRepository(private val context: Context) {

    val prefsFlow: Flow<SesionPrefs> =
        context.dataStore.data.map { p ->
            SesionPrefs(
                token  = p[PrefKeys.TOKEN] ?: "",
                correo = p[PrefKeys.CORREO] ?: "",
                rol    = p[PrefKeys.ROL] ?: "",
            )
        }

    suspend fun guardarSesion(token: String, correo: String, rol: String) {
        context.dataStore.edit { p ->
            p[PrefKeys.TOKEN] = token
            p[PrefKeys.CORREO] = correo
            p[PrefKeys.ROL] = rol
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { p ->
            p.remove(PrefKeys.TOKEN)
            p.remove(PrefKeys.CORREO)
            p.remove(PrefKeys.ROL)
        }
    }
}
