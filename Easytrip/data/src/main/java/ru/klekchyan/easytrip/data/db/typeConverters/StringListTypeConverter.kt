package ru.klekchyan.easytrip.data.db.typeConverters

import androidx.room.TypeConverter

class StringListTypeConverter {

    @TypeConverter
    fun toString(list: List<String>) = list.joinToString(separator = ",", prefix = "", postfix = "")

    @TypeConverter
    fun toList(str: String) = str.split(",").toList()
}