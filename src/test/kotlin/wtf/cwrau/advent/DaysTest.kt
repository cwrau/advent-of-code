package wtf.cwrau.advent

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import wtf.cwrau.AdventOfCodeDay
import kotlin.test.assertEquals

class DaysTest {
    data class Solution<T>(
        val day: AdventOfCodeDay<T>,
        val partOne: T?,
        val partTwo: T?,
    )

    @TestFactory
    fun allDays() = listOf(
        Solution(Day01, 54708, 54087),
        Solution(Day02, 2727, 56580),
        Solution(Day03, 550934, 81997870),
        Solution(Day04, 18653, 5921508),
        Solution(Day05, 340994526, 52210644),
    ).flatMap { (day, partOne, partTwo) ->
        buildList {
            val testPrefix = "Day ${day.number} - ${day.name} - "
            if (day.partOneExamples.isNotEmpty()) {
                add(
                    DynamicTest.dynamicTest("${testPrefix}Part One - Example") {
                        day.partOneExamples.forEach { (example, solution) ->
                            assertEquals(solution, day.calculatePartOne(example))
                        }
                    }
                )
            }

            if (partOne != null) {
                add(
                    DynamicTest.dynamicTest("${testPrefix}Part One") {
                        assertEquals(partOne, day.calculatePartOne())
                    }
                )
            }

            if (day.partTwoExamples.isNotEmpty()) {
                add(
                    DynamicTest.dynamicTest("${testPrefix}Part Two - Example") {
                        day.partTwoExamples.forEach { (example, solution) ->
                            assertEquals(solution, day.calculatePartTwo(example))
                        }
                    }
                )
            }

            if (partTwo != null) {
                add(
                    DynamicTest.dynamicTest("${testPrefix}Part Two") {
                        assertEquals(partTwo, day.calculatePartTwo())
                    }
                )
            }
        }
    }
}
