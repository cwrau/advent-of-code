package wtf.cwrau.advent

import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class DayThreeTest {
    @Test
    fun `input 1`() {
        val input = """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...$.*....
            .664.598..
        """.trimIndent()

        assertEquals(4361, DayThree().calculate(input))
    }
}
