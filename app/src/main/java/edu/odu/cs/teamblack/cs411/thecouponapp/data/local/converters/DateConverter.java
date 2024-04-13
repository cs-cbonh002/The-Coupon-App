package edu.odu.cs.teamblack.cs411.thecouponapp.data.local.converters;

import androidx.room.TypeConverter;

import java.time.ZoneId;
import java.util.Date;

import java.util.TimeZone;

public class DateConverter {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.of("UTC")).toInstant().toEpochMilli();
    }
}