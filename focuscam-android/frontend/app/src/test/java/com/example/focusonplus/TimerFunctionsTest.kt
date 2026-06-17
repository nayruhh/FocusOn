package com.example.focusonplus

import org.junit.Test
import org.junit.Assert.*

class TimerFunctionsTest {

    @Test
    fun `formatTime formats seconds correctly for less than an hour`() {
        // Test various second values
        assertEquals("00:00", formatTime(0))
        assertEquals("00:01", formatTime(1))
        assertEquals("00:59", formatTime(59))
        assertEquals("01:00", formatTime(60))
        assertEquals("01:30", formatTime(90))
        assertEquals("05:30", formatTime(330))
        assertEquals("59:59", formatTime(3599))
    }

    @Test
    fun `formatTime formats seconds correctly for one hour or more`() {
        assertEquals("01:00:00", formatTime(3600))
        assertEquals("01:00:01", formatTime(3601))
        assertEquals("01:30:00", formatTime(5400))
        assertEquals("02:15:30", formatTime(8130))
        assertEquals("10:00:00", formatTime(36000))
    }

    @Test
    fun `formatTime handles edge cases`() {
        assertEquals("00:00", formatTime(0))
        assertEquals("00:01", formatTime(1))
        assertEquals("01:00:00", formatTime(3600))
        assertEquals("01:00:01", formatTime(3601))
    }

    @Test
    fun `formatTime handles large values`() {
        assertEquals("24:00:00", formatTime(86400))
        assertEquals("100:00:00", formatTime(360000))
    }
}


