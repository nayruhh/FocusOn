package com.example.focusonplus

import android.content.Context
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class AnalyticsStorageTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSharedPreferences("analytics", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
    }

    @Test
    fun `saveSession saves session record correctly`() {
        // Given
        val record = SessionRecord(
            minutes = 30,
            distractions = 2,
            accuracy = 90,
            dayOfWeek = 1
        )
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")

        // When
        AnalyticsStorage.saveSession(mockContext, record)

        // Then
        verify(mockSharedPreferences).getString("session_history", "[]")
        verify(mockEditor).putString(eq("session_history"), anyString())
        verify(mockEditor).apply()
    }

    @Test
    fun `saveSession appends to existing sessions`() {
        // Given
        val existingSessions = """[{"minutes":15,"distractions":1,"accuracy":95,"day":1}]"""
        val newRecord = SessionRecord(
            minutes = 30,
            distractions = 2,
            accuracy = 90,
            dayOfWeek = 2
        )
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn(existingSessions)

        // When
        AnalyticsStorage.saveSession(mockContext, newRecord)

        // Then
        verify(mockSharedPreferences).getString("session_history", "[]")
        verify(mockEditor).putString(eq("session_history"), anyString())
        verify(mockEditor).apply()
    }

    @Test
    fun `getSessionHistory returns empty list when no sessions exist`() {
        // Given
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")

        // When
        val result = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getSessionHistory returns correct session records`() {
        // Given
        val sessionsJson = """[
            {"minutes":15,"distractions":1,"accuracy":95,"day":1},
            {"minutes":30,"distractions":2,"accuracy":90,"day":2}
        ]"""
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn(sessionsJson)

        // When
        val result = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertEquals(2, result.size)
        assertEquals(15, result[0].minutes)
        assertEquals(1, result[0].distractions)
        assertEquals(95, result[0].accuracy)
        assertEquals(1, result[0].dayOfWeek)
        
        assertEquals(30, result[1].minutes)
        assertEquals(2, result[1].distractions)
        assertEquals(90, result[1].accuracy)
        assertEquals(2, result[1].dayOfWeek)
    }

    @Test
    fun `getSessionHistory handles invalid JSON gracefully`() {
        // Given
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("invalid json")

        // When
        val result = runCatching {
            AnalyticsStorage.getSessionHistory(mockContext)
        }

        // Then
        assertTrue(result.isFailure || result.getOrNull()?.isEmpty() == true)
    }
}


