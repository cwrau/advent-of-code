package wtf.cwrau.advent

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class DayOneTest {
    @Test
    fun `calculate example input 1`() {
        val input = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()

        assertEquals(142, DayOne().calculate(input))
    }
}
