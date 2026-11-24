package mx.tec.app_sonrisas.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private const val NOMBRE_ARCHIVO = "sesion_prefs"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = NOMBRE_ARCHIVO)
