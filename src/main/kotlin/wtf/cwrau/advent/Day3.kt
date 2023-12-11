package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

class Day3 : AdventOfCodeDay<Int> {
    private val objectRegex: Regex = Regex("""\d+|[^.\d]""")

    data class Coordinate(val x: Int, val y: Int)

    sealed class Schema(val coordinates: List<Coordinate>) {

        fun calculateNeighbours() = (coordinates.minOf { it.y } - 1..coordinates.maxOf { it.y } + 1).flatMap { y ->
            (coordinates.minOf { it.x } - 1..coordinates.maxOf { it.x } + 1).map { x ->
                Coordinate(x, y)
            }
        } - coordinates.toSet()

        class Gear(val number: Int, coordinates: List<Coordinate>) : Schema(coordinates)
        class Symbol(coordinates: List<Coordinate>) : Schema(coordinates)
    }


    override fun calculatePartOne(input: List<String>): Int {
        val schemas = parseSchemas(input)

        val (gears, symbols) = schemas.splitByType()

        val smallerList: List<Schema>
        val otherList: List<Schema>
        if (gears.size < symbols.size) {
            smallerList = gears
            otherList = symbols
        } else {
            smallerList = symbols
            otherList = gears
        }

        return smallerList.flatMap { schema ->
            val neighbourCoordinates = schema.calculateNeighbours().toSet()
            val neighbours = otherList.filter { it.coordinates.intersect(neighbourCoordinates).isNotEmpty() }
            if (neighbours.isNotEmpty()) {
                if (schema is Schema.Gear) {
                    listOf(schema)
                } else {
                    neighbours.filterIsInstance<Schema.Gear>()
                }
            } else {
                listOf()
            }
        }
            .distinct()
            .sumOf { it.number }
    }

    private fun parseSchemas(input: List<String>): List<Schema> {
        val schemas = input.flatMapIndexed { row, line ->
            val matches = objectRegex.findAll(line)
            val schema = matches.flatMap { it.groups }.filterNotNull().map {
                when {
                    it.value.toIntOrNull() != null -> {
                        Schema.Gear(it.value.toInt(), it.range.map { column -> Coordinate(column, row) })
                    }

                    else -> {
                        Schema.Symbol(it.range.map { column -> Coordinate(column, row) })
                    }
                }
            }.toList()
            schema
        }
        return schemas
    }

    override fun calculatePartTwo(input: List<String>): Int {
        val schemas = parseSchemas(input)

        val (gears, symbols) = schemas.splitByType()

        return symbols
            .map { symbol ->
                gears
                    .filter { gear ->
                        gear.calculateNeighbours().intersect(symbol.coordinates.toSet()).isNotEmpty()
                    }
            }
            .filter {
                it.size == 2
            }
            .map { (first, second) ->
                first.number * second.number
            }
            .sum()
    }

    private fun List<Schema>.splitByType(): Pair<List<Schema.Gear>, List<Schema.Symbol>> {
        val (gears, symbols) = partition { it is Schema.Gear }
        return gears.filterIsInstance<Schema.Gear>() to symbols.filterIsInstance<Schema.Symbol>()
    }
}
