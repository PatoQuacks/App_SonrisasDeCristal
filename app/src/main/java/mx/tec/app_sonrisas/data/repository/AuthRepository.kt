package mx.tec.app_sonrisas.data.repository

import mx.tec.app_sonrisas.data.remote.ApiService
import mx.tec.app_sonrisas.data.remote.LoginRequest
import mx.tec.app_sonrisas.data.remote.RetrofitClient
import mx.tec.app_sonrisas.data.remote.Usuario

// Habla el idioma de la app: "inicia sesión", "dame mi perfil".
// El ViewModel no sabe de URLs, headers ni del prefijo "Bearer".
class AuthRepository(
    private val api: ApiService = RetrofitClient.api,
) {
    suspend fun iniciarSesion(correo: String, password: String): String =
        api.login(LoginRequest(correo, password)).token

    suspend fun obtenerPerfil(token: String): Usuario =
        api.perfil("Bearer $token")
}
