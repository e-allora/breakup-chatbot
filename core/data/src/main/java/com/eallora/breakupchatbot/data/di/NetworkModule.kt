package com.eallora.breakupchatbot.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private val json = Json { ignoreUnknownKeys = true }
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.breakupchatbot.com/v1/")
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
    
    @Provides
    @Singleton
    fun provideSyncApi(retrofit: Retrofit): com.eallora.breakupchatbot.data.remote.api.SyncApi =
        retrofit.create(com.eallora.breakupchatbot.data.remote.api.SyncApi::class.java)
}