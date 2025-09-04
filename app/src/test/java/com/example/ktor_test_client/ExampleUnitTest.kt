package com.example.ktor_test_client

import androidx.compose.ui.graphics.Color
import com.example.ktor_test_client.helpers.contrast
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun colorContrastTestDefault() {
        assertEquals(2.12f, Color(0xffff00ff).contrast(Color(0xff545c73)), .01f)
        assertEquals(10.5f, Color(0xffffdd00).contrast(Color(0xff2b2b2b)), .1f)
    }
}