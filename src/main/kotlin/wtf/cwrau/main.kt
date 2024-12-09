package wtf.cwrau

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.cache.storage.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import wtf.cwrau.advent.Day01
import java.nio.file.Files
import java.nio.file.Paths

const val year = 2024

val client = HttpClient(CIO) {
    expectSuccess = true
    install(HttpCache) {
        publicStorage(FileStorage(Files.createDirectories(Paths.get("aoc/cache")).toFile()))
    }
}

suspend fun main(args: Array<String>) {
    val days = listOf(
        Day01,
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

private suspend fun calculateAndPrintDay(advent: AdventOfCodeDay) {
    val input = getInput(advent.number)
    val results =
        runCatching { advent.calculatePartOne(input) } to runCatching { advent.calculatePartTwo(input) }
    val (result, resultTwo) = results
    println(buildString {
        appendLine("${advent.name}:")
        append("    Part One: ")
        if (result.isSuccess) {
            val output = result.getOrNull()!!
            appendLine(output)
            if (validateDay(advent, 1, output)) {
                appendLine("✅")
            } else {
                appendLine("❎")
            }
        } else {
            appendLine(result.exceptionOrNull()!!.message)
        }
        append("    Part Two: ")
        if (resultTwo.isSuccess) {
            val output = resultTwo.getOrNull()!!
            appendLine(output)
            validateDay(advent, 2, output)
        } else {
            appendLine(resultTwo.exceptionOrNull()!!.message)
        }
    })
    System.out.flush()
}

suspend fun getInput(number: Int) = client.get("https://adventofcode.com/$year/day/$number/input").bodyAsText().lines()

suspend fun validateDay(advent: AdventOfCodeDay, part: Int, output: Long) =
    client.post("https://adventofcode.com/solve/$year/${advent.number}/$part") {
        setBody(getInput(advent.number))
    }.bodyAsText().toLong() == output
