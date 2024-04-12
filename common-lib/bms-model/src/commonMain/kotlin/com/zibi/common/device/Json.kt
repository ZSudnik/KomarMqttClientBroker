package com.zibi.common.device

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.lang.reflect.Type

object Json {
    private val gson = Gson()
    fun <T> fromJson(str: String?, type: Type?): T {
        return gson.fromJson(str, type)
    }

    fun <T> fromJson(str: String?, type: Class<T>?): T {
        return gson.fromJson(str, type)
    }

    fun toJson(obj: Any?): String {
        return gson.toJson(obj)
    }

    @Throws(Exception::class)
    fun parse(str: String?): ParsedObj {
        return try {
            val jElement = JsonParser.parseString(str)
            ParsedObj(jElement.asJsonObject)
        } catch (e: Exception) {
            throw e
        }
    }

    @Throws(Exception::class)
    fun parseArray(str: String?): Array<ParsedObj?> {
        return try {
            val jArray = JsonParser.parseString(str).asJsonArray
            val res = arrayOfNulls<ParsedObj>(jArray.size())
            for (i in 0 until jArray.size()) {
                res[i] = ParsedObj(jArray[i].asJsonObject)
            }
            res
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    class ParsedObj internal constructor(private val jsonObject: JsonObject) {
        fun getString(name: String?): String? {
            val element = jsonObject[name] ?: return null
            return element.asString
        }

        fun getBoolean(name: String?): Boolean? {
            val element = jsonObject[name] ?: return null
            return element.asBoolean
        }

        fun getInt(name: String?): Int? {
            val element = jsonObject[name] ?: return null
            return element.asInt
        }

        fun getObject(name: String?): ParsedObj? {
            val element = jsonObject[name] ?: return null
            return ParsedObj(element.asJsonObject)
        }

        fun getArray(name: String?): Array<ParsedObj?>? {
//            java.lang.IllegalStateException: Not a JSON Array: null
            val arr = jsonObject[name] ?: return null
            return try {
                val jArray = arr.asJsonArray
                if (jArray == null || jArray.size() == 0) return null
                val res = arrayOfNulls<ParsedObj>(jArray.size())
                for (i in 0 until jArray.size()) {
                    res[i] = ParsedObj(jArray[i].asJsonObject)
                }
                res
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun getIntArray(name: String?): IntArray? {
//            java.lang.IllegalStateException: Not a JSON Array: null
            val arr = jsonObject[name] ?: return null
            return try {
                val jArray = arr.asJsonArray
                if (jArray == null || jArray.size() == 0) return null
                val res = IntArray(jArray.size())
                for (i in 0 until jArray.size()) {
                    res[i] = jArray[i].asInt
                }
                res
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }


        fun <T> fromJson(type: Class<T>?): T {
            return gson.fromJson(jsonObject.toString(), type)
        }
    }
}
