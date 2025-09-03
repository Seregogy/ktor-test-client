package com.example.ktor_test_client

import androidx.compose.ui.graphics.Color
import com.example.ktor_test_client.helpers.contrast
import com.example.ktor_test_client.helpers.l
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(1.0, Color.White.l(), .0)
        assertEquals(0.0, Color.Black.l(), .0)

        println(Color.Yellow.contrast(Color.Black))
        println(Color.Black.contrast(Color.White))
        println(Color.White.contrast(Color.Black))
    }
}