package com.example.dicodingeventsv2.data.remote.retrofit

import com.example.dicodingeventsv2.data.remote.response.EventResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    suspend fun getEvents(
        @Query("active")
        active: Int
    ): EventResponse

    @GET("events")
    suspend fun getUpdatedEvent(
        @Query("active")
        active: Int = -1,
        @Query("limit")
        limit: Int = 40
    ): EventResponse
}