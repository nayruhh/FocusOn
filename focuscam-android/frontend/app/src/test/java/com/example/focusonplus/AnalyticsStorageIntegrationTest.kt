package com.example.focusonplus

import android.content.Context
import android.content.SharedPreferences
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class AnalyticsStorageIntegrationTest {

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
    fun `saveSession and getSessionHistory work together correctly`() {
        // Given
        val record1 = SessionRecord(
            minutes = 15,
            distractions = 1,
            accuracy = 95,
            dayOfWeek = 1
        )
        val record2 = SessionRecord(
            minutes = 30,
            distractions = 2,
            accuracy = 90,
            dayOfWeek = 2
        )
        
        // Setup mock to return empty initially, then return saved data
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":15,"distractions":1,"accuracy":95,"day":1}]""")
            .thenReturn("""[{"minutes":15,"distractions":1,"accuracy":95,"day":1},{"minutes":30,"distractions":2,"accuracy":90,"day":2}]""")

        // When
        AnalyticsStorage.saveSession(mockContext, record1)
        AnalyticsStorage.saveSession(mockContext, record2)
        val history = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertEquals(2, history.size)
        assertEquals(15, history[0].minutes)
        assertEquals(1, history[0].distractions)
        assertEquals(95, history[0].accuracy)
        assertEquals(1, history[0].dayOfWeek)

        assertEquals(30, history[1].minutes)
        assertEquals(2, history[1].distractions)
        assertEquals(90, history[1].accuracy)
        assertEquals(2, history[1].dayOfWeek)
    }

    @Test
    fun `multiple sessions are saved in order`() {
        // Given
        val records = listOf(
            SessionRecord(minutes = 10, distractions = 0, accuracy = 100, dayOfWeek = 1),
            SessionRecord(minutes = 20, distractions = 1, accuracy = 95, dayOfWeek = 2),
            SessionRecord(minutes = 30, distractions = 2, accuracy = 90, dayOfWeek = 3)
        )
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":10,"distractions":0,"accuracy":100,"day":1}]""")
            .thenReturn("""[{"minutes":10,"distractions":0,"accuracy":100,"day":1},{"minutes":20,"distractions":1,"accuracy":95,"day":2}]""")
            .thenReturn("""[{"minutes":10,"distractions":0,"accuracy":100,"day":1},{"minutes":20,"distractions":1,"accuracy":95,"day":2},{"minutes":30,"distractions":2,"accuracy":90,"day":3}]""")

        // When
        records.forEach { AnalyticsStorage.saveSession(mockContext, it) }
        val history = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertEquals(3, history.size)
        assertEquals(10, history[0].minutes)
        assertEquals(20, history[1].minutes)
        assertEquals(30, history[2].minutes)
    }

    @Test
    fun `getSessionHistory returns empty list when no sessions exist`() {
        // Given
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")

        // When
        val history = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertTrue(history.isEmpty())
    }

    @Test
    fun `session with zero minutes is saved correctly`() {
        // Given
        val record = SessionRecord(
            minutes = 0,
            distractions = 0,
            accuracy = 100,
            dayOfWeek = 1
        )
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":0,"distractions":0,"accuracy":100,"day":1}]""")

        // When
        AnalyticsStorage.saveSession(mockContext, record)
        val history = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertEquals(1, history.size)
        assertEquals(0, history[0].minutes)
    }

    @Test
    fun `session with high distraction count is saved correctly`() {
        // Given
        val record = SessionRecord(
            minutes = 60,
            distractions = 10,
            accuracy = 50,
            dayOfWeek = 5
        )
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":60,"distractions":10,"accuracy":50,"day":5}]""")

        // When
        AnalyticsStorage.saveSession(mockContext, record)
        val history = AnalyticsStorage.getSessionHistory(mockContext)

        // Then
        assertEquals(1, history.size)
        assertEquals(60, history[0].minutes)
        assertEquals(10, history[0].distractions)
        assertEquals(50, history[0].accuracy)
    }
}


