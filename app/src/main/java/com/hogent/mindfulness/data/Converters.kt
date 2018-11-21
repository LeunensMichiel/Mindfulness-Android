//package com.hogent.mindfulness.data
//
//import android.arch.persistence.room.TypeConverter
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//
//class Converters{
//    @TypeConverter
//    fun fromString(value: String): Array<String> {
//        val listType = object : TypeToken<Array<String>>() {
//
//        }.getType()
//        return Gson().fromJson(value, listType)
//    }
//
//    @TypeConverter
//    fun fromArrayList(list: Array<String>): String {
//        val gson = Gson()
//        return gson.toJson(list)
//    }
//}