package edu.snhu.craigo_eventapp;

import androidx.room.TypeConverter;

public class PriorityConverter {

    @TypeConverter
    public static Priority fromInt(Integer intValue) {
        return intValue == null? null : Priority.fromInt(intValue);
    }

    @TypeConverter
    public static Integer PriorityToInt(Priority priority) {
        return priority == null ? null : priority.value();
    }


}
