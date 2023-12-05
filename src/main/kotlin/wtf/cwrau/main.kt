package wtf.cwrau

import java.util.*

fun main() {
    ServiceLoader.load(AdventOfCodeDay::class.java)
        .associateBy {
            it::class.java.simpleName
        }
        .mapValues { (name, advent) ->
            advent.calculate(getInput(name))
        }
        .forEach { (name, result) ->
            println("$name: $result")
        }
}

fun getInput(name: String): String {
    return ClassLoader.getSystemResource(name).readText()
}
