package com.example.dicodingeventsv2.di

import android.content.Context
import com.example.dicodingeventsv2.data.local.room.EventDatabase
import com.example.dicodingeventsv2.data.remote.EventRepository
import com.example.dicodingeventsv2.data.remote.retrofit.ApiConfig
import com.example.dicodingeventsv2.utils.AppExecutor

object Injection {
    fun provideRepository(context: Context) : EventRepository {
        val apiservice = ApiConfig.getApiService()
        val db = EventDatabase.getDatabase(context)
        val dao = db.eventDao()
        val executor = AppExecutor()
        return EventRepository.getInstance(apiservice, dao, executor)
    }
}