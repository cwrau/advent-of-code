package wtf.cwrau.advent

import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class Day3Test : AdventTests {
    private val input = """
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

    @Test
    override fun `test part one`() {
        assertEquals(
            4361, Day3().calculatePartOne(
                input.lines()
            )
        )
    }

    @Test
    override fun `test part two`() {
        assertEquals(467835, Day3().calculatePartTwo(input.lines()))
    }
}

