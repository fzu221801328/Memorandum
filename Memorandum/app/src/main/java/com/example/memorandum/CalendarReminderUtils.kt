package com.example.memorandum

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.CalendarContract
import android.util.Log
import androidx.annotation.RequiresApi
import java.util.*

public class CalendarReminderUtils(private val context: Context) {
    var CALENDER_URL = "content://com.android.calendar/calendars"
    var CALENDER_EVENT_URL = "content://com.android.calendar/events"
    var CALENDER_REMINDER_URL = "content://com.android.calendar/reminders"

    var CALENDARS_NAME = "boohee"
    var CALENDARS_ACCOUNT_NAME = "BOOHEE@boohee.com"
    var CALENDARS_ACCOUNT_TYPE = "com.android.boohee"
    var CALENDARS_DISPLAY_NAME = "BOOHEE账户"

    /**
     * 检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
     * 获取账户成功返回账户id，否则返回-1
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun checkAndAddCalendarAccount(): Int {
        var oldId = checkCalendarAccount()
        if (oldId >= 0) {
            return oldId
        } else {
            var addId: Long = addCalendarAccount()
            if (addId >= 0) {
                return checkCalendarAccount()
            } else {
                return -1
            }
        }
    }

    /**
     * 检查是否存在现有账户，存在则返回账户id，否则返回-1
     */
    fun checkCalendarAccount(): Int {
        var userCursor =
            context.getContentResolver().query(Uri.parse(CALENDER_URL), null, null, null, null)
        try {
            if (userCursor == null) { //查询返回空值
                return -1
            }
            var count = userCursor.getCount()
            if (count > 0) { //存在现有账户，取第一个账户的id返回
                userCursor.moveToFirst()
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID))
            } else {
                return -1
            }
        } finally {
            if (userCursor != null) {
                userCursor.close()
            }
        }
    }

    /**
     * 添加日历账户，账户创建成功则返回账户id，否则返回-1
     */

    fun addCalendarAccount(): Long {
        var timeZone = TimeZone.getDefault()
        var value = ContentValues()
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME)
        value.put(CalendarContract.Calendars.VISIBLE, 1)
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE)
        value.put(
            CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
            CalendarContract.Calendars.CAL_ACCESS_OWNER
        )
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1)
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID())
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME)
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0)

        var calendarUri = Uri.parse(CALENDER_URL)
        calendarUri = calendarUri.buildUpon()
            .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
            .appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                CALENDARS_ACCOUNT_NAME
            )
            .appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CALENDARS_ACCOUNT_TYPE
            )
            .build()

        var result = context.getContentResolver().insert(calendarUri, value)
        var id =
            if (result == null) -1
            else ContentUris.parseId(result)
        return id
    }

    @SuppressLint("MissingPermission")
    fun insert(year:Int,month:Int,date:Int,hour:Int,minute:Int) {
        var calID: Long = 1
        var startMillis: Long = 0
        var endMillis: Long = 0
        var beginTime = Calendar.getInstance()
        //月从0开始
        beginTime.set(year,month,date,hour,minute)
        startMillis = beginTime.timeInMillis
        var endTime = Calendar.getInstance()
        endTime.set(year,month,date,hour+1,minute)
        endMillis = endTime.timeInMillis

        var timeZone = TimeZone.getDefault().id
        Log.d("tag", "timeZone -- > " + timeZone)

        var cr = context.contentResolver
        var values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.DTEND, endMillis)
        values.put(CalendarContract.Events.TITLE, "Jazzercise")
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout")
        values.put(CalendarContract.Events.CALENDAR_ID, calID)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone)
        values.put(CalendarContract.Events.EVENT_LOCATION, "福建")
        var resultUri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
        var eventId = resultUri?.lastPathSegment
        //var eventId = ContentUris.parseId(resultUri)

        var reminderValues = ContentValues()
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventId)
        reminderValues.put(CalendarContract.Reminders.MINUTES, 5)
        reminderValues.put(
            CalendarContract.Reminders.METHOD,
            CalendarContract.Reminders.METHOD_ALERT
        )

        var reminderUri = CalendarContract.Reminders.CONTENT_URI
        cr.insert(reminderUri, reminderValues)
    }
}