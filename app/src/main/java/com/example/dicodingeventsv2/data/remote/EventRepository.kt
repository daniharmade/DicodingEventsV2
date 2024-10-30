package com.example.dicodingeventsv2.data.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.dicodingeventsv2.data.local.entity.EventEntity
import com.example.dicodingeventsv2.data.local.room.EventDao
import com.example.dicodingeventsv2.data.remote.response.EventResponse
import com.example.dicodingeventsv2.data.remote.retrofit.ApiService
import com.example.dicodingeventsv2.utils.AppExecutor
import com.example.dicodingeventsv2.utils.Result
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext

class EventRepository private constructor(
    private val apiService: ApiService,
    private val eventDao: EventDao,
    private val appExecutors: AppExecutor
) {

    fun getAllEvents(active: Int): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getEvents(active)

            val data = response.listEvents
            val eventList = data.map { event ->
                val isFinished = active == 0
                val isUpcoming = active == 1
                val isFavorite = event.name.let {
                    eventDao.isEventFavorite(it)
                }
                EventEntity(
                    event.id,
                    event.name,
                    event.summary,
                    event.description,
                    event.imageLogo,
                    event.mediaCover,
                    event.category,
                    event.ownerName,
                    event.cityName,
                    event.quota,
                    event.registrants,
                    event.beginTime,
                    event.endTime,
                    event.link,
                    isFavorite,
                    isFinished,
                    isUpcoming
                )
            }

            eventDao.insertEventsData(eventList)
            emit(Result.Success(eventList))

        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getFavoriteEvent(): LiveData<List<EventEntity>> {
        return eventDao.getFavoriteEvent()
    }

    fun searchEvent(query: String): LiveData<Result<List<EventEntity>>> = liveData {
        emit(Result.Loading)
        try {
            val dataLocal = eventDao.searchFinishedEvent(query).map { eventList ->
                if (eventList.isNotEmpty()) {
                    Result.Success(eventList)
                } else {
                    Result.Error("Error: $query")
                }
            }
            emitSource(dataLocal)
        } catch (exception: Exception) {
            emit(Result.Error(exception.message.toString()))
        }
    }

    suspend fun setFavoriteEvent(event: EventEntity, favorite: Boolean) {
        event.isFavorite = favorite
        withContext(appExecutors.diskIO.asCoroutineDispatcher()) {
            eventDao.updateEventsData(event)
        }
    }

    suspend fun getNearestEvent(): EventResponse? {
        val getEvent = try {
            apiService.getUpdatedEvent(active = -1, limit = 1)
        } catch (e: Exception) {
            null
        }
        return getEvent
    }

    companion object {
        @Volatile
        private var INSTANCE: EventRepository? = null

        fun getInstance(
            apiService: ApiService, eventDao: EventDao, appExecutors: AppExecutor
        ): EventRepository = INSTANCE ?: synchronized(this) {
            INSTANCE ?: EventRepository(apiService, eventDao, appExecutors)
        }.also { INSTANCE = it }
    }
}