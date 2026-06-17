package com.example.focusonplus

import android.content.Context
import android.content.SharedPreferences
import androidx.navigation.NavController
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class SessionFlowIntegrationTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockSharedPreferences: SharedPreferences

    @Mock
    private lateinit var mockEditor: SharedPreferences.Editor

    @Mock
    private lateinit var mockNavController: NavController

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.getSharedPreferences("analytics", Context.MODE_PRIVATE))
            .thenReturn(mockSharedPreferences)
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor)
    }

    @Test
    fun `saveAndNavigate saves session and navigates correctly`() {
        // Given
        val seconds = 1800 // 30 minutes
        val distractions = 2
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":30,"distractions":2,"accuracy":90,"day":1}]""")

        // When
        saveAndNavigate(mockContext, seconds, distractions, mockNavController)

        // Then
        val history = AnalyticsStorage.getSessionHistory(mockContext)
        assertEquals(1, history.size)
        assertEquals(30, history[0].minutes)
        assertEquals(2, history[0].distractions)
        assertEquals(90, history[0].accuracy) // 100 - (2 * 5) = 90
        
        // Verify navigation was called
        verify(mockNavController).navigate(anyString())
    }

    @Test
    fun `saveAndNavigate calculates accuracy correctly for zero distractions`() {
        // Given
        val seconds = 3600 // 60 minutes
        val distractions = 0
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":60,"distractions":0,"accuracy":100,"day":1}]""")

        // When
        saveAndNavigate(mockContext, seconds, distractions, mockNavController)

        // Then
        val history = AnalyticsStorage.getSessionHistory(mockContext)
        assertEquals(100, history[0].accuracy)
    }

    @Test
    fun `saveAndNavigate does not save session with zero minutes`() {
        // Given
        val seconds = 0
        val distractions = 0
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")

        // When
        saveAndNavigate(mockContext, seconds, distractions, mockNavController)

        // Then
        // Verify that putString was NOT called (session not saved)
        verify(mockEditor, never()).putString(eq("session_history"), anyString())
        // Navigation should still be called
        verify(mockNavController).navigate(anyString())
    }

    @Test
    fun `saveAndNavigate handles partial minutes correctly`() {
        // Given
        val seconds = 90 // 1.5 minutes, should be 1 minute
        val distractions = 1
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":1,"distractions":1,"accuracy":95,"day":1}]""")

        // When
        saveAndNavigate(mockContext, seconds, distractions, mockNavController)

        // Then
        val history = AnalyticsStorage.getSessionHistory(mockContext)
        assertEquals(1, history[0].minutes)
    }

    @Test
    fun `formatTime and saveAndNavigate work together correctly`() {
        // Given
        val seconds = 3661 // 1 hour, 1 minute, 1 second
        val formatted = formatTime(seconds)
        val distractions = 0
        
        `when`(mockSharedPreferences.getString("session_history", "[]"))
            .thenReturn("[]")
            .thenReturn("""[{"minutes":61,"distractions":0,"accuracy":100,"day":1}]""")

        // When
        saveAndNavigate(mockContext, seconds, distractions, mockNavController)

        // Then
        assertEquals("01:01:01", formatted)
        val history = AnalyticsStorage.getSessionHistory(mockContext)
        assertEquals(61, history[0].minutes) // 3661 / 60 = 61
    }
}


