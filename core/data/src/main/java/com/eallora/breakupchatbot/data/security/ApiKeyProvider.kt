package com.eallora.breakupchatbot.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import javax.inject.Inject

interface ApiKeyProvider {
    fun getApiKey(): String?
    fun saveApiKey(key: String)
    fun clearApiKey()
    fun hasApiKey(): Boolean
}

class SecureApiKeyProvider @Inject constructor(
    private val prefs: EncryptedSharedPreferences
) : ApiKeyProvider {
    
    companion object {
        private const val KEY_API_KEY = "api_key"
    }
    
    override fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)
    
    override fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }
    
    override fun clearApiKey() {
        prefs.edit().remove(KEY_API_KEY).apply()
    }
    
    override fun hasApiKey(): Boolean = getApiKey()?.isNotBlank() == true
}