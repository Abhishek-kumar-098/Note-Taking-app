package com.example.notetakingapp.utils

import android.content.Context
import com.example.notetakingapp.utils.Constants.PREFS_TOKEN_FILES
import com.example.notetakingapp.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context : Context) {

    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILES, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

}
