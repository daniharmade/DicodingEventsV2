package com.example.dicodingeventsv2.ui

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    private val themeKey = booleanPreferencesKey("setting_theme")
    private val notificationKey = booleanPreferencesKey("setting_notification")

    fun getThemeSetting(): Flow<Boolean> = dataStore.data.map { it[themeKey] ?: false }

    fun getNotificationSetting(): Flow<Boolean> = dataStore.data.map { it[notificationKey] ?: false }

    suspend fun saveThemeSetting(isDarkMode: Boolean) = dataStore.edit { it[themeKey] = isDarkMode }

    suspend fun saveNotificationSetting(isActive: Boolean) = dataStore.edit { it[notificationKey] = isActive }

    companion object {
        @Volatile
        private var instance: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences =
            instance ?: synchronized(this) {
                instance ?: SettingPreferences(dataStore).also { instance = it }
            }
    }
}