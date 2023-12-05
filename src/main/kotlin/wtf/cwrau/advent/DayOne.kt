package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

val digits = listOf(
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

class DayOne : AdventOfCodeDay<Int> {
    override fun calculate(input: String): Int {
        return input.lines()
            .filter { it.isNotEmpty() }
            .map {
                val parseDigits = it.parseDigits()
                it to parseDigits
            }
            .sumOf { (line, numbers) ->
                val number = "${numbers.first()}${numbers.last()}"
                number.toInt()
            }
    }

    private fun String.parseDigits(): List<Int> = indices
        .fold(listOf()) { numbers, index ->
            val subString = substring(index)
            val number = digits.withIndex().firstOrNull { (_, digit) ->
                subString.startsWith(digit)
            }?.index ?: subString.first().digitToIntOrNull()
            numbers + listOfNotNull(number)
        }
}
