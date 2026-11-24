package mx.tec.app_sonrisas.data

import androidx.datastore.preferences.core.stringPreferencesKey

object PrefKeys {
    val TOKEN  = stringPreferencesKey("token")
    val CORREO = stringPreferencesKey("correo")
    val ROL    = stringPreferencesKey("rol")
}

data class SesionPrefs(
    val token: String = "",
    val correo: String = "",
    val rol: String = "",
) {
    val haySesion: Boolean get() = token.isNotEmpty()
}
