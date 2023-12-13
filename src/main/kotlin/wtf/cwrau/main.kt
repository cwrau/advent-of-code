package wtf.cwrau

fun main() {
    ClassLoader.getSystemClassLoader()
        .let { loader ->
            (1..24).map {
                runCatching { loader.loadClass("wtf.cwrau.advent.Day${it.toString().padStart(2, '0')}") }
            }
        }
        .mapNotNull { it.getOrNull() }
        .filterIsInstance<Class<AdventOfCodeDay<*>>>()
        .mapNotNull { it.kotlin.objectInstance }
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
