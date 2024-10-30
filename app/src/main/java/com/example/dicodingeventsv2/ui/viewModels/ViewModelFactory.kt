package com.example.dicodingeventsv2.ui.viewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dicodingeventsv2.data.remote.EventRepository
import com.example.dicodingeventsv2.di.Injection
import com.example.dicodingeventsv2.ui.SettingPreferences
import com.example.dicodingeventsv2.ui.dataStore

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val preference: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel(preference, eventRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null

        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(
                    SettingPreferences.getInstance(context.dataStore),
                    Injection.provideRepository(context)
                ).also { instance = it }
            }
    }
}