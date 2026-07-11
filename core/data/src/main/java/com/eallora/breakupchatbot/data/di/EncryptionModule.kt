package com.eallora.breakupchatbot.data.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.EncryptedSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.crypto.KeyGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EncryptionModule {
    
    @Provides
    @Singleton
    fun provideEncryptedStorage(@dagger.hilt.android.qualifiers.ApplicationContext ctx: Context): EncryptedSharedPreferences =
        EncryptedSharedPreferences.create(
            "breakupchatbot_prefs",
            MasterKeys.getOrCreate(),
            ctx,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES_256_GCM,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES_256_GCM
        )
    
    @Provides
    @Singleton
    fun provideApiKeyProvider(@dagger.hilt.android.qualifiers.ApplicationContext ctx: Context): com.eallora.breakupchatbot.data.security.ApiKeyProvider =
        com.eallora.breakupchatbot.data.security.SecureApiKeyProvider(ctx)
}

object MasterKeys {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "breakupchatbot_key"
    
    fun getOrCreate(): String {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )
        val keyGenSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
         .setEncryptionPadded(KeyProperties.ENCRYPTION_PADDING_NONE)
         .build()
        keyGenerator.init(keyGenSpec)
        keyGenerator.generateKey()
        return KEY_ALIAS
    }
}