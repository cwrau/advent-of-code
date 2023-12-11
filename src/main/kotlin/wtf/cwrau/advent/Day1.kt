package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

class Day1 : AdventOfCodeDay<Int> {
    private val digits = listOf(
        "zero",
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine",
    )

    override fun calculatePartOne(input: List<String>): Int {
        return input
            .map {
                val parseDigits = it.parseDigits()
                it to parseDigits
            }
            .sumOf { (line, numbers) ->
                val number = "${numbers.first()}${numbers.last()}"
                number.toInt()
            }
    }

    override fun calculatePartTwo(input: List<String>) = calculatePartOne(input)

    private fun String.parseDigits(): List<Int> = indices
        .fold(listOf()) { numbers, index ->
            val subString = substring(index)
            val number = digits.withIndex().firstOrNull { (_, digit) ->
                subString.startsWith(digit)
            }?.index ?: subString.first().digitToIntOrNull()
            numbers + listOfNotNull(number)
        }
}
