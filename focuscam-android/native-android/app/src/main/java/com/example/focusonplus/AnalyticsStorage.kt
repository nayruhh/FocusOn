package com.example.focusonplus

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class SessionRecord(
    val minutes: Int,
    val distractions: Int,
    val accuracy: Int,
    val dayOfWeek: Int  // 1 = Monday, 7 = Sunday
)

object AnalyticsStorage {

    private const val KEY_SESSIONS = "session_history"

    fun saveSession(context: Context, record: SessionRecord) {
        val prefs = context.getSharedPreferences("analytics", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SESSIONS, "[]")
        val array = JSONArray(json)

        val obj = JSONObject()
        obj.put("minutes", record.minutes)
        obj.put("distractions", record.distractions)
        obj.put("accuracy", record.accuracy)
        obj.put("day", record.dayOfWeek)

        array.put(obj)

        prefs.edit().putString(KEY_SESSIONS, array.toString()).apply()
    }

    fun getSessionHistory(context: Context): List<SessionRecord> {
        val prefs = context.getSharedPreferences("analytics", Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_SESSIONS, "[]")
        val array = JSONArray(json)

        val list = mutableListOf<SessionRecord>()

        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            list.add(
                SessionRecord(
                    minutes = obj.getInt("minutes"),
                    distractions = obj.getInt("distractions"),
                    accuracy = obj.getInt("accuracy"),
                    dayOfWeek = obj.getInt("day")
                )
            )
        }

        return list
    }
}
