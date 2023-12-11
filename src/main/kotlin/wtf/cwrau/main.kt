package wtf.cwrau

import java.util.*

fun main() {
    ServiceLoader.load(AdventOfCodeDay::class.java)
        .associateBy {
            it::class.java.simpleName
        }
        .mapValues { (name, advent) ->
            val input = getInput(name).lines()
            runCatching { advent.calculatePartOne(input) } to runCatching { advent.calculatePartTwo(input) }
        }
        .forEach { (name, results) ->
            val (result, resultTwo) = results
            println(buildString {
                appendLine("$name:")
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
}

fun getInput(name: String): String {
    return ClassLoader.getSystemResource(name).readText()
}
