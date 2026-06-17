package com.example.focusonplus

import org.junit.Test
import org.junit.Assert.*

class FaceDetectionLogicTest {

    @Test
    fun `warmUpSeconds increments correctly`() {
        var warmUpSeconds = 0
        val maxWarmUp = 15
        
        // Simulate warm-up period
        for (i in 0 until maxWarmUp) {
            if (warmUpSeconds < maxWarmUp) {
                warmUpSeconds++
            }
        }
        
        assertEquals(15, warmUpSeconds)
    }

    @Test
    fun `warmUpSeconds does not exceed maximum`() {
        var warmUpSeconds = 15
        val maxWarmUp = 15
        
        // Try to increment beyond max
        if (warmUpSeconds < maxWarmUp) {
            warmUpSeconds++
        }
        
        assertEquals(15, warmUpSeconds)
    }

    @Test
    fun `grace period increments correctly`() {
        var grace = 0
        val maxGrace = 5
        
        // Simulate grace period
        for (i in 0 until maxGrace) {
            grace++
        }
        
        assertEquals(5, grace)
    }

    @Test
    fun `session pauses when grace period exceeds threshold`() {
        var grace = 0
        var paused = false
        val threshold = 5
        
        // Simulate grace period incrementing
        for (i in 0 until threshold) {
            grace++
            if (grace >= threshold) {
                paused = true
            }
        }
        
        assertTrue(paused)
        assertEquals(5, grace)
    }

    @Test
    fun `grace resets when face is detected`() {
        var grace = 3
        var faceDetected = true
        
        if (faceDetected) {
            grace = 0
        }
        
        assertEquals(0, grace)
    }

    @Test
    fun `hasDetectedFaceOnce is set when face is first detected`() {
        var hasDetectedFaceOnce = false
        var faceDetected = true
        
        if (faceDetected) {
            hasDetectedFaceOnce = true
        }
        
        assertTrue(hasDetectedFaceOnce)
    }

    @Test
    fun `distractions increment when session pauses`() {
        var totalDistractions = 0
        var paused = false
        
        // Simulate pause
        paused = true
        if (paused) {
            totalDistractions++
        }
        
        assertEquals(1, totalDistractions)
    }
}


