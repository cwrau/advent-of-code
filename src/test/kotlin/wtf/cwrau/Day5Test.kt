package wtf.cwrau

import org.junit.jupiter.api.Assertions.assertEquals
import wtf.cwrau.advent.AdventTests
import wtf.cwrau.advent.Day5
import kotlin.test.Test

class Day5Test : AdventTests {
    private val input = """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent()

    @Test
    override fun `test part one`() {
        assertEquals(35, Day5().calculatePartOne(input.lines()))
    }

    @Test
    override fun `test part two`() {
        assertEquals(46, Day5().calculatePartTwo(input.lines()))
    }
}
