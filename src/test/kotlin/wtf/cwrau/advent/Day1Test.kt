package wtf.cwrau.advent

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class Day1Test : AdventTests {
    @Test
    override fun `test part one`() {
        val input = """
            1abc2
            pqr3stu8vwx
            a1b2c3d4e5f
            treb7uchet
        """.trimIndent()

        assertEquals(142, Day1().calculatePartOne(
            input.lines()
        ))
    }

    @Test
    override fun `test part two`() {
        val input = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()

        assertEquals(281, Day1().calculatePartOne(
            input.lines()
        ))
    }

}
