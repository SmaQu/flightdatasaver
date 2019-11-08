package com.smaqu.rcgraphsystem.util

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class ApplicationPreferences(activity: Activity) {

    companion object {
        const val SETTINGS_PREFERENCE_FILE = "sp_separator_file_name"

        // Communication Preference
        const val PREF_SHOW_EXTRA_SIGN_KEY = "pref_show_extra_sign_key"
        const val PREF_EXTRA_SIGN_KEY = "pref_extra_sign"
        const val DEFAULT_EXTRA_SIGN = "\n"

        const val PREF_SHOW_FILE_LIST_KEY = "pref_show_file_list_key"
        const val DEFAULT_SHOW_LIST = true

        // Arduino Preference
        const val PREF_PACKAGE_KEY = "pref_package_separator"
        const val DEFAULT_PACKAGE_SEPARATOR = "&"

        const val PREF_VARIABLE_KEY = "pref_variable_separator"
        const val DEFAULT_VARIABLE_SEPARATOR = ","
    }

    private val sharedPreferences: SharedPreferences =
            activity.getSharedPreferences(SETTINGS_PREFERENCE_FILE, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    fun getSharedPreferences(prefKey: String, defaultValue: String): String {
        return sharedPreferences.getString(prefKey, defaultValue)
    }

    fun getSharedPreferences(prefKey: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(prefKey, defaultValue)
    }

    inner class Editor {

        fun putString(preferencesKey: String, stringValue: String): Editor {
            editor.putString(preferencesKey, stringValue)
            return this
        }

        fun putBoolean(preferencesKey: String, booleanValue: Boolean): Editor  {
            editor.putBoolean(preferencesKey, booleanValue)
            return this
        }

        fun apply() {
            editor.apply()
        }
    }
}
