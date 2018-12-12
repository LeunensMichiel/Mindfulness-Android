package com.hogent.mindfulness.data.LocalDatabase.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hogent.mindfulness.domain.Model


class GroupConverter {
    @TypeConverter
    fun fromGroupModel(group: Model.Group?): String? {
        if (group == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Model.Group>(){}.getType()
        return gson.toJson(group, type)
    }

    @TypeConverter
    fun toGroupModel(group: String?): Model.Group? {
        if (group == null) {
            return null
        }
        val gson = Gson()
        val type = object : TypeToken<Model.Group>() {}.getType()
        return gson.fromJson(group, type)
    }
}