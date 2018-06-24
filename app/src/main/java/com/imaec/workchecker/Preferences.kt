package com.imaec.workchecker

import android.content.Context

/**
 * Created by imaec on 2018-06-23.
 */
class Preferences {

    companion object {
        fun get(context: Context, key: String, default: String = ""): String {
            val pref = context.getSharedPreferences("pref", 0)
            return pref.getString(key, default)
        }

        fun get(context: Context, key: String, default: Int = 0): Int {
            val pref = context.getSharedPreferences("pref", 0)
            return pref.getInt(key, default)
        }

        fun get(context: Context, key: String, default: Boolean = false): Boolean {
            val pref = context.getSharedPreferences("pref", 0)
            return pref.getBoolean(key, default)
        }

        fun set(context: Context, key: String, value: String) {
            val pref = context.getSharedPreferences("pref", 0)
            val editor = pref.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun set(context: Context, key: String, value: Int) {
            val pref = context.getSharedPreferences("pref", 0)
            val editor = pref.edit()
            editor.putInt(key, value)
            editor.apply()
        }

        fun set(context: Context, key: String, value: Boolean) {
            val pref = context.getSharedPreferences("pref", 0)
            val editor = pref.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun remove(context: Context, key: String) {
            val pref = context.getSharedPreferences("pref", 0)
            val editor = pref.edit()
            editor.remove(key)
            editor.apply()
        }
    }
}