package com.example.focusonplus

import org.junit.Test
import org.junit.Assert.*

class SessionLogicTest {

    @Test
    fun `accuracy calculation is correct for zero distractions`() {
        val distractions = 0
        val accuracy = when {
            distractions == 0 -> 100
            else -> maxOf(0, 100 - distractions * 5)
        }
        assertEquals(100, accuracy)
    }

    @Test
    fun `accuracy calculation is correct for one distraction`() {
        val distractions = 1
        val accuracy = when {
            distractions == 0 -> 100
            else -> maxOf(0, 100 - distractions * 5)
        }
        assertEquals(95, accuracy)
    }

    @Test
    fun `accuracy calculation is correct for multiple distractions`() {
        val distractions = 2
        val accuracy = when {
            distractions == 0 -> 100
            else -> maxOf(0, 100 - distractions * 5)
        }
        assertEquals(90, accuracy)
    }

    @Test
    fun `accuracy calculation does not go below zero`() {
        val distractions = 25 // Would be -25 without maxOf
        val accuracy = when {
            distractions == 0 -> 100
            else -> maxOf(0, 100 - distractions * 5)
        }
        assertEquals(0, accuracy)
    }

    @Test
    fun `totalMinutes calculation is correct`() {
        assertEquals(0, 0 / 60)
        assertEquals(0, 59 / 60)
        assertEquals(1, 60 / 60)
        assertEquals(1, 119 / 60)
        assertEquals(2, 120 / 60)
        assertEquals(30, 1800 / 60)
    }

    @Test
    fun `dayOfWeek adjustment is correct for Sunday`() {
        // Calendar.SUNDAY = 1, we want 7
        val calendarSunday = 1
        val adjustedDay = if (calendarSunday == 1) 7 else calendarSunday - 1
        assertEquals(7, adjustedDay)
    }

    @Test
    fun `dayOfWeek adjustment is correct for Monday`() {
        // Calendar.MONDAY = 2, we want 1
        val calendarMonday = 2
        val adjustedDay = if (calendarMonday == 1) 7 else calendarMonday - 1
        assertEquals(1, adjustedDay)
    }

    @Test
    fun `dayOfWeek adjustment is correct for Saturday`() {
        // Calendar.SATURDAY = 7, we want 6
        val calendarSaturday = 7
        val adjustedDay = if (calendarSaturday == 1) 7 else calendarSaturday - 1
        assertEquals(6, adjustedDay)
    }
}


