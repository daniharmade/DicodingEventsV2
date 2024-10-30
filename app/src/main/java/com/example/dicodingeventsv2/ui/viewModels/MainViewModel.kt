package com.example.dicodingeventsv2.ui.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingeventsv2.data.local.entity.EventEntity
import com.example.dicodingeventsv2.data.remote.EventRepository
import com.example.dicodingeventsv2.ui.SettingPreferences
import com.example.dicodingeventsv2.utils.Result
import kotlinx.coroutines.launch


class MainViewModel(
    private val settingPreferences: SettingPreferences,
    private val eventRepository: EventRepository
) : ViewModel() {
    fun getUpcomingEvent(): LiveData<Result<List<EventEntity>>> {
        return eventRepository.getAllEvents(1)
    }

    fun getFinishedEvent(): LiveData<Result<List<EventEntity>>> {
        return eventRepository.getAllEvents(0)
    }

    fun getFavoriteEvent(): LiveData<List<EventEntity>> {
        return eventRepository.getFavoriteEvent()
    }

    fun searchEvent(query: String): LiveData<Result<List<EventEntity>>> {
        return eventRepository.searchEvent(query)
    }

    fun getThemeSettings(): LiveData<Boolean> {
        return settingPreferences.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveThemeSetting(isDarkMode)
        }
    }

    fun addEventToFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, true)
        }
    }

    fun removeEventFromFavorite(event: EventEntity) {
        viewModelScope.launch {
            eventRepository.setFavoriteEvent(event, false)
        }
    }

    fun getNotificationSetting(): LiveData<Boolean> {
        return settingPreferences.getNotificationSetting().asLiveData()
    }

    fun saveNotificationSetting(isActive: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveNotificationSetting(isActive)
        }
    }

}