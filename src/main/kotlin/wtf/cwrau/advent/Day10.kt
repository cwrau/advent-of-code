package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day10 : AdventOfCodeDay<Int>(10, "Pipe Maze") {
    @Suppress("unused")
    private sealed class Tile(val symbol: Char) {
        sealed class Pipe(symbol: Char, val connections: Pair<Direction, Direction>) : Tile(symbol) {
            data object Vertical : Pipe('|', Direction.Column.Up to Direction.Column.Down)
            data object Horizontal : Pipe('-', Direction.Row.Left to Direction.Row.Right)
            data object NorthEastBend : Pipe('L', Direction.Column.Up to Direction.Row.Right)
            data object NorthWestBend : Pipe('J', Direction.Column.Up to Direction.Row.Left)
            data object SouthWestBend : Pipe('7', Direction.Column.Down to Direction.Row.Left)
            data object SouthEastBend : Pipe('F', Direction.Column.Down to Direction.Row.Right)
        }

        data object Ground : Tile('.')
        data object Animal : Tile('S')
    }

    private val allTiles =
        Tile::class.sealedSubclasses.flatMap { if (it.isSealed) it.sealedSubclasses else listOf(it) }
            .mapNotNull { it.objectInstance }
    private val allPipes = Tile.Pipe::class.sealedSubclasses.mapNotNull { it.objectInstance }

    private sealed class Direction(val offset: Int, val opposite: Direction) {
        sealed class Row(offset: Int, opposite: Direction) : Direction(offset, opposite) {
            data object Left : Row(-1, Right)
            data object Right : Row(1, Left)
        }

        sealed class Column(offset: Int, opposite: Direction) : Direction(offset, opposite) {
            data object Up : Column(-1, Down)
            data object Down : Column(1, Up)
        }
    }

    private val allDirections = Direction::class.sealedSubclasses.flatMap { it.sealedSubclasses }
        .mapNotNull { it.objectInstance }

    override fun calculatePartOne(input: List<String>): Int {
        val field = input
            .map { line ->
                line.map { symbol ->
                    allTiles
                        .single { it.symbol == symbol }
                }
            }
        val (y, x) = field.withIndex().find { it.value.contains(Tile.Animal) }!!
            .let { it.index to it.value.indexOf(Tile.Animal) }
        val neighboringPipes = field.neighborsOf(y, x)
        val animalTile = neighboringPipes
            .entries
            .fold(allPipes) { possiblePipes, (direction, tile) ->
                val pipe = tile.second
                if (direction.opposite in pipe.connections.toList()) {
                    possiblePipes.filterByDirection(direction)
                } else {
                    possiblePipes
                }
            }
            .single()

        val loop = generateSequence(listOf((y to x) to animalTile)) { loop ->
            val newConnectedPipes = loop.takeLast(2)
                .flatMap { (coordinates, tile) ->
                    val (y, x) = coordinates
                    tile.connections.toList()
                        .mapNotNull {
                            field.getNeighborOf(it, y, x)
                        }
                        .filterIsInstance<Pair<Pair<Int, Int>, Tile.Pipe>>()
                }
                .distinct()
                .filter { it.first !in loop.map { it.first } }
            if (newConnectedPipes.isNotEmpty()) {
                loop + newConnectedPipes
            } else {
                null
            }
        }

        return loop.last().size / 2
    }

    private fun List<Tile.Pipe>.filterByDirection(direction: Direction): List<Tile.Pipe> {
        return filter {
            direction in it.connections.toList()
        }
    }

    private fun List<List<Tile>>.getNeighborOf(direction: Direction, y: Int, x: Int): Pair<Pair<Int, Int>, Tile>? {
        val (yIndex, xIndex) = when (direction) {
            is Direction.Column -> y + direction.offset to x
            is Direction.Row -> y to x + direction.offset
        }

        if (yIndex in indices) {
            val row = this[yIndex]
            if (xIndex in row.indices) {
                return (yIndex to xIndex) to row[xIndex]
            }
        }
        return null
    }

    private fun List<List<Tile>>.neighborsOf(y: Int, x: Int): Map<Direction, Pair<Pair<Int, Int>, Tile.Pipe>> {
        return allDirections
            .asSequence()
            .mapNotNull { direction ->
                getNeighborOf(direction, y, x)?.let {
                    direction to it
                }
            }
            .filter { it.second.second is Tile.Pipe }
            .toList()
            .filterIsInstance<Pair<Direction, Pair<Pair<Int, Int>, Tile.Pipe>>>()
            .toMap()
    }


    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            -L|F7
            7S-7|
            L|7||
            -L-J|
            L|-JF
        """.trimIndent().lines() to 4,
        """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
        """.trimIndent().lines() to 8
    )
}
