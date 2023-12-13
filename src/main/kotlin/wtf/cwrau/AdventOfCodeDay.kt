package wtf.cwrau

abstract class AdventOfCodeDay<R>(val number: Int, val name: String) {
    private val inputList by lazy {
        ClassLoader.getSystemResource("Day${number.toString().padStart(2, '0')}").readText().lines()
    }

    abstract fun calculatePartOne(input: List<String> = inputList): R
    open fun calculatePartTwo(input: List<String> = inputList): R = calculatePartOne(input)

    abstract val partOneExamples: Map<List<String>, R>
    abstract val partTwoExamples: Map<List<String>, R>
}
