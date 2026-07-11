package com.eallora.breakupchatbot.di

import android.content.Context
import androidx.room.Room
import com.eallora.breakupchatbot.data.local.AppDatabase
import com.eallora.breakupchatbot.data.local.dao.ConversationDao
import com.eallora.breakupchatbot.data.local.dao.MessageDao
import com.eallora.breakupchatbot.data.local.dao.ExerciseDao
import com.eallora.breakupchatbot.data.local.dao.ProgressDao
import com.eallora.breakupchatbot.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(
            ctx,
            AppDatabase::class.java,
            "breakupchatbot.db"
        ).build()
    
    @Provides
    fun provideConversationDao(db: AppDatabase): ConversationDao = db.conversationDao()
    
    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
    
    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()
    
    @Provides
    fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()
    
    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()
}