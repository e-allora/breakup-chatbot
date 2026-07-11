package com.eallora.breakupchatbot.data.di

import com.eallora.breakupchatbot.data.ai.CloudLlmAdapter
import com.eallora.breakupchatbot.data.ai.LocalLlmAdapter
import com.eallora.breakupchatbot.data.ai.LlmRouter
import com.eallora.breakupchatbot.data.remote.api.SyncApi
import com.eallora.breakupchatbot.data.security.ApiKeyProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    
    @Provides
    @Singleton
    fun provideLocalLlmAdapter(): LocalLlmAdapter = LocalLlmAdapter()
    
    @Provides
    @Singleton
    fun provideCloudLlmAdapter(
        api: SyncApi,
        apiKeyProvider: ApiKeyProvider
    ): CloudLlmAdapter = CloudLlmAdapter(api, apiKeyProvider)
    
    @Provides
    @Singleton
    fun provideLlmRouter(
        cloud: CloudLlmAdapter,
        local: LocalLlmAdapter,
        apiKeyProvider: ApiKeyProvider
    ): LlmRouter = LlmRouter(cloud, local, apiKeyProvider)
}