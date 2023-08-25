package com.example.lkmdl.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.Reader
import java.lang.reflect.Type

object GsonUtil {
    private val gson: Gson by lazy {
        GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    }
    private val prettyGson: Gson by lazy {
        GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .create()
    }

    /**
     * 小写下划线的格式解析JSON字符串到对象
     *
     * 例如 is_success->isSuccess
     *
     * @param json
     * @param classOfT
     * @return
     */
    fun <T> fromJsonUnderScoreStyle(json: String?, classOfT: Class<T>?): T {
        return gson.fromJson(json, classOfT)
    }

    /**
     * JSON字符串转为Map<String></String>,String>
     *
     * @param json
     * @return
     */
    fun <T> json2Map(json: String?): T {
        return gson.fromJson(json, object : TypeToken<Map<String?, String?>?>() {}.type)
    }

    /**
     * 小写下划线的格式将对象转换成JSON字符串
     *
     * @param src
     * @return
     */
    fun toJson(src: Any?): String {
        return gson.toJson(src)
    }

    fun toJson(src: Any?, writer: Appendable?) {
        gson.toJson(src, writer)
    }

    fun toPrettyString(src: Any?): String {
        return prettyGson.toJson(src)
    }

    fun <T> fromJson2Object(src: String?, t: Class<T>?): T {
        return gson.fromJson(src, t)
    }

    fun <T> fromJson2Object(src: String?, typeOfT: Type?): T {
        return gson.fromJson(src, typeOfT)
    }

    fun <T> fromJson2Object(reader: Reader?, typeOfT: Type?): T {
        return gson.fromJson(reader, typeOfT)
    }

    fun <T> fromJson2Object(reader: Reader?, t: Class<T>?): T {
        return gson.fromJson(reader, t)
    }
}
