package mx.tec.app_sonrisas.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

// Las rutas de la API que usa la app.
interface ApiService {

    // Pública: correo + password -> token
    @POST("login")
    suspend fun login(@Body datos: LoginRequest): LoginResponse

    // Protegida: hay que mandar "Bearer <token>"
    @GET("perfil")
    suspend fun perfil(@Header("Authorization") auth: String): Usuario

    // Protegida: busca un usuario por su correo.
    //   200 -> el usuario ya existe
    //   404 -> no existe
    // RUTA PROPUESTA: si la API usa otra ruta para esto, solo cambien el texto de @GET.
    // Se pide Response<Unit> porque solo importa el código de respuesta, no el cuerpo.
    @GET("usuarios/{correo}")
    suspend fun buscarUsuario(
        @Header("Authorization") auth: String,
        @Path("correo") correo: String,
    ): Response<Unit>
}
