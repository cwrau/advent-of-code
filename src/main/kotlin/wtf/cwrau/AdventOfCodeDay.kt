package wtf.cwrau

fun interface AdventOfCodeDay<R> {
    fun calculate(input: List<String>): R
    fun calculate(input: String) = calculate(
        input.lines()
            .filter { it.isNotEmpty() }
    )
}
