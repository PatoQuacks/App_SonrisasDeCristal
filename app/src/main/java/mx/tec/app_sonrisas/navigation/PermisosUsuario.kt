package mx.tec.app_sonrisas.navigation

import mx.tec.app_sonrisas.data.remote.Usuario

object NivelUsuario {
    const val DIRECTOR = 1
    const val RESPONSABLE = 2
    const val COORDINADOR = 3
    const val EMPLEADO = 4
}

fun esDirector(usuario: Usuario?): Boolean =
    usuario?.id == NivelUsuario.DIRECTOR

fun destinoInicialPara(usuario: Usuario?): String =
    if (esDirector(usuario)) {
        Pantalla.CoberturaDirector.route
    } else {
        Pantalla.Inicio.route
    }

fun obtenerBarraInferior(usuario: Usuario?): List<Pantalla> =
    when (usuario?.id) {
        NivelUsuario.DIRECTOR -> listOf(
            Pantalla.CoberturaDirector,
            Pantalla.ReporteDirector,
            Pantalla.Perfil,
        )
        NivelUsuario.RESPONSABLE,
        NivelUsuario.COORDINADOR -> listOf(
            Pantalla.Inicio,
            Pantalla.Asistencia,
            Pantalla.Incidencias,
            Pantalla.Perfil,
        )
        else -> listOf(
            Pantalla.Inicio,
            Pantalla.Capacitaciones,
            Pantalla.Perfil,
        )
    }

fun obtenerMenuLateral(usuario: Usuario?): List<Pantalla> {
    val id = usuario?.id ?: NivelUsuario.EMPLEADO
    if (id == NivelUsuario.DIRECTOR) {
        return listOf(
            Pantalla.Configuracion,
            Pantalla.AcercaDe,
        )
    }

    val opciones = mutableListOf<Pantalla>()

    if (id <= NivelUsuario.COORDINADOR) {
        opciones.add(Pantalla.Empleados)
        opciones.add(Pantalla.Capacitaciones)
    }
    if (id <= NivelUsuario.RESPONSABLE) {
        opciones.add(Pantalla.Coordinadores)
        opciones.add(Pantalla.Salas)
    }

    opciones.add(Pantalla.Configuracion)
    opciones.add(Pantalla.AcercaDe)
    return opciones
}
