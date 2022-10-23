package edu.snhu.craigo_eventapp;

import androidx.room.TypeConverter;

import java.sql.Date;

// see https://developer.android.com/training/data-storage/room/referencing-data

/**
 * Type converters tell Room how it can breakdown complex objects into
 * simpler, supported types.
 */

public class DateConverter {

    @TypeConverter
    public static Date fromLong(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToLong(Date date) {
        return date == null ? null : date.getTime();
    }
}
