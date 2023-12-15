package wtf.cwrau

import wtf.cwrau.advent.Day01
import wtf.cwrau.advent.Day02
import wtf.cwrau.advent.Day03
import wtf.cwrau.advent.Day04
import wtf.cwrau.advent.Day05
import wtf.cwrau.advent.Day06
import wtf.cwrau.advent.Day07
import wtf.cwrau.advent.Day08
import wtf.cwrau.advent.Day09

fun main(args: Array<String>) {
    val days = listOf(
        Day01,
        Day02,
        Day03,
        Day04,
        Day05, // Skipped for speed
        Day06,
        Day07,
        Day08,
        Day09,
    )
    if (args.isNotEmpty()) {
        if (args.singleOrNull() == "all") {
            days
        } else {
            args.map { it.toInt() }.map { days[it] }
        }
    } else {
        listOf(days.last())
    }
        .forEach { advent ->
            calculateAndPrintDay(advent)
        }
}

private fun calculateAndPrintDay(advent: AdventOfCodeDay<*>) {
    val input = getInput(advent.number).lines()
    val results =
        runCatching { advent.calculatePartOne(input) } to runCatching { advent.calculatePartTwo(input) }
    val (result, resultTwo) = results
    println(buildString {
        appendLine("${advent.name}:")
        append("    Part One: ")
        if (result.isSuccess) {
            appendLine(result.getOrNull())
        } else {
            appendLine(result.exceptionOrNull()!!.message)
        }
        append("    Part Two: ")
        if (resultTwo.isSuccess) {
            appendLine(resultTwo.getOrNull())
        } else {
            appendLine(resultTwo.exceptionOrNull()!!.message)
        }
    })
    System.out.flush()
}

fun getInput(number: Int): String {
    return ClassLoader.getSystemResource("Day${number.toString().padStart(2, '0')}").readText()
}
