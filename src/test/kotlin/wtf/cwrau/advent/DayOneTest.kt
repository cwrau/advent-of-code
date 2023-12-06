package wtf.cwrau.advent

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class DayOneTest {
    @Test
    fun `input 1`() {
        val input = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()

        assertEquals(142, DayOne().calculate(input))
    }

    @Test
    fun `input 2`() {
        val input = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()

        assertEquals(281, DayOne().calculate(input))
    }

}
