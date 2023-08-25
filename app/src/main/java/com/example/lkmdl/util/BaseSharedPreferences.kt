package com.example.lkmdl.util

import android.content.Context
import android.content.SharedPreferences
import com.example.lkmdl.MyApplication
import org.jetbrains.annotations.NotNull

object BaseSharedPreferences {
    val sp: SharedPreferences by lazy {
        MyApplication.context.getSharedPreferences("default", Context.MODE_PRIVATE)
    }

    fun put(@NotNull key: String, @NotNull value: Any) = with(sp.edit()) {
        when (value) {
            is Long -> putLong(key, value)
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Float -> putFloat(key, value)
            else -> putString(key, GsonUtil.toJson(value))
        }.apply()
    }

    fun find(@NotNull key: String): Boolean {
        return sp.contains(key)
    }

    /**
     * 内联函数使用的方法和属性的权限修饰符必须是public
     * */
    inline fun <reified T> get(@NotNull key: String, default: T): T {
        if (!find(key)) return default;
        return with(sp) {
            when (default) {
                is Int -> getInt(key, default) as T
                is Long -> getLong(key, default) as T
                is Float -> getFloat(key, default) as T
                is Boolean -> getBoolean(key, default) as T
                is String -> getString(key, default) as T
                else -> GsonUtil.fromJson2Object(getString(key, ""), T::class.java)
            }
        }
    }

    fun remove(@NotNull key: String): Boolean {
        if (!find(key)) return true
        sp.edit().remove(key).apply()
        return true
    }

    fun clear() {
        sp.edit().clear().apply()
    }
}