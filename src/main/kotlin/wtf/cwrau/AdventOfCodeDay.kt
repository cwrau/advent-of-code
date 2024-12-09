package wtf.cwrau

abstract class AdventOfCodeDay(val number: Int, val name: String) {
    abstract fun calculatePartOne(input: List<String>): Long
    open fun calculatePartTwo(input: List<String>): Long = calculatePartOne(input)

    abstract val partOneExamples: Map<List<String>, Long>
    open val partTwoExamples: Map<List<String>, Long> = mapOf()
}
