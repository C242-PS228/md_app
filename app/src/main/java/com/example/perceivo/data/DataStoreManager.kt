package com.example.perceivo.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name= "user_prefs")
class DataStoreManager private constructor (private val dataStore: DataStore<Preferences>) {

    companion object {
        @Volatile
        private var INSTANCE: DataStoreManager ?= null
        fun getInstance(context: Context): DataStoreManager{
            return  INSTANCE?: synchronized(this) {
                val instance = DataStoreManager(context.dataStore)
                INSTANCE= instance
                instance
            }
        }
    }
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    val getToken: Flow<String> = dataStore.data.catch { exception ->
        if (exception is IOException) {
            emit(emptyPreferences())
        } else {
            throw exception
        }
    }
        .map { preferences ->
            preferences[TOKEN_KEY]?:""
        }
    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }

}

