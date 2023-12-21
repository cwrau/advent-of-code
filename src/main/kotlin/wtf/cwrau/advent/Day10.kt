package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day10 : AdventOfCodeDay<Int>(10, "Pipe Maze") {

    private class Field(private var tiles: List<List<Tile>>) {
        private lateinit var animalTile: FieldTile<Tile.Pipe>

        init {
            this.tiles = tiles
                .mapIndexed { y, row ->
                    row.mapIndexed { x, tile ->
                        if (tile is Tile.Animal) {
                            val neighboringPipes = neighborsOf(FieldTile(y, x, tile))
                            val animalTile = neighboringPipes
                                .entries
                                .fold(allPipes) { possiblePipes, (direction, neighboringFieldTile) ->
                                    when (val neighboringTile = neighboringFieldTile.tile) {
                                        is Tile.Pipe -> if (direction.opposite in neighboringTile.connections.toList()) {
                                            possiblePipes.filter {
                                                direction in it.connections.toList()
                                            }
                                        } else {
                                            possiblePipes
                                        }

                                        else -> possiblePipes
                                    }
                                }
                                .single()
                            this.animalTile = FieldTile(y, x, animalTile)
                            animalTile
                        } else {
                            tile
                        }
                    }
                }
        }

        operator fun get(y: Int, x: Int): Tile? {
            return tiles.getOrNull(y)?.getOrNull(x)
        }

        companion object {
            private val allTiles =
                Tile::class.sealedSubclasses.flatMap { if (it.isSealed) it.sealedSubclasses else listOf(it) }
                    .mapNotNull { it.objectInstance }
            private val allPipes = Tile.Pipe::class.sealedSubclasses.mapNotNull { it.objectInstance }
            private val allDirections = Direction::class.sealedSubclasses.flatMap { it.sealedSubclasses }
                .mapNotNull { it.objectInstance }

            fun parseField(input: List<String>): Field = Field(input
                .map { line ->
                    line.map { symbol ->
                        allTiles
                            .single { it.symbol == symbol }
                    }
                }
            )
        }

        data class FieldTile<T : Tile>(val y: Int, val x: Int, val tile: T)

        @Suppress("unused")
        sealed class Tile(val symbol: Char) {
            sealed class Pipe(
                symbol: Char,
            ) : Tile(symbol) {

                abstract val connections: Pair<Direction, Direction>
                abstract val sides: Pair<List<Quadrant>, List<Quadrant>>

                sealed class Quadrant {
                    abstract val connections: Map<Direction, Quadrant>

                    data object UpperLeft : Quadrant() {
                        override val connections by lazy {
                            mapOf(
                                Direction.Column.Up to LowerLeft,
                                Direction.Row.Left to UpperRight
                            )
                        }
                    }

                    data object LowerLeft : Quadrant() {
                        override val connections by lazy {
                            mapOf(
                                Direction.Column.Down to UpperLeft,
                                Direction.Row.Left to LowerRight
                            )
                        }
                    }

                    data object UpperRight : Quadrant() {
                        override val connections by lazy {
                            mapOf(
                                Direction.Column.Up to LowerRight,
                                Direction.Row.Right to UpperLeft
                            )
                        }
                    }

                    data object LowerRight : Quadrant() {
                        override val connections by lazy {
                            mapOf(
                                Direction.Column.Down to UpperRight,
                                Direction.Row.Right to LowerLeft
                            )
                        }
                    }
                }

                data object Vertical : Pipe('|') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Column.Down
                    }
                    override val sides by lazy {
                        listOf(Quadrant.UpperLeft, Quadrant.LowerLeft) to listOf(
                            Quadrant.UpperRight,
                            Quadrant.LowerRight
                        )
                    }
                }

                data object Horizontal : Pipe('-') {
                    override val connections by lazy {
                        Direction.Row.Left to Direction.Row.Right
                    }
                    override val sides by lazy {
                        listOf(Quadrant.UpperLeft, Quadrant.UpperRight) to listOf(
                            Quadrant.LowerLeft,
                            Quadrant.LowerRight
                        )
                    }
                }

                data object NorthEastBend : Pipe('L') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Row.Right
                    }
                    override val sides by lazy {
                        listOf(
                            Quadrant.UpperLeft,
                            Quadrant.LowerLeft,
                            Quadrant.LowerRight
                        ) to listOf(Quadrant.UpperRight)
                    }
                }

                data object NorthWestBend : Pipe('J') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Row.Left
                    }
                    override val sides by lazy {
                        listOf(Quadrant.UpperLeft) to listOf(
                            Quadrant.UpperRight,
                            Quadrant.LowerLeft,
                            Quadrant.LowerRight
                        )
                    }
                }

                data object SouthWestBend : Pipe('7') {
                    override val connections by lazy {
                        Direction.Column.Down to Direction.Row.Left
                    }
                    override val sides by lazy {
                        listOf(
                            Quadrant.UpperLeft,
                            Quadrant.UpperRight,
                            Quadrant.LowerRight
                        ) to listOf(Quadrant.LowerLeft)
                    }
                }

                data object SouthEastBend : Pipe('F') {
                    override val connections by lazy {
                        Direction.Column.Down to Direction.Row.Right
                    }
                    override val sides by lazy {
                        listOf(
                            Quadrant.UpperLeft,
                            Quadrant.UpperRight,
                            Quadrant.LowerLeft
                        ) to listOf(Quadrant.LowerRight)
                    }
                }
            }

            data object Ground : Tile('.')
            data object Animal : Tile('S')
        }

        sealed class Direction(val offset: Int) {
            abstract val opposite: Direction

            sealed class Row(offset: Int) : Direction(offset) {
                data object Left : Row(-1) {
                    override val opposite by lazy { Right }
                }

                data object Right : Row(1) {
                    override val opposite by lazy { Left }
                }
            }

            sealed class Column(offset: Int) : Direction(offset) {
                data object Up : Column(-1) {
                    override val opposite by lazy { Down }
                }

                data object Down : Column(1) {
                    override val opposite by lazy { Up }
                }
            }
        }

        fun <T : Tile> getNeighborOf(direction: Direction, tile: FieldTile<T>): FieldTile<Tile>? {
            val (yIndex, xIndex) = when (direction) {
                is Direction.Column -> tile.y + direction.offset to tile.x
                is Direction.Row -> tile.y to tile.x + direction.offset
            }

            val neighbor = this[yIndex, xIndex]

            return if (neighbor != null) {
                FieldTile(yIndex, xIndex, neighbor)
            } else {
                null
            }
        }

        fun <T : Tile> neighborsOf(fieldTile: FieldTile<T>): Map<Direction, FieldTile<Tile>> {
            return allDirections
                .asSequence()
                .mapNotNull { direction ->
                    getNeighborOf(direction, fieldTile)?.let {
                        direction to it
                    }
                }
                .toMap()
        }

        fun findLoop(): List<FieldTile<Tile.Pipe>> {
            return generateSequence(listOf(animalTile)) { loop ->
                val newConnectedPipes = loop.takeLast(2)
                    .flatMap { fieldTile ->
                        fieldTile.tile.connections.toList()
                            .mapNotNull {
                                getNeighborOf(it, fieldTile)
                            }
                            .filterIsInstance<FieldTile<Tile.Pipe>>()
                    }
                    .distinct()
                    .filter { it !in loop }
                if (newConnectedPipes.isNotEmpty()) {
                    loop + newConnectedPipes
                } else {
                    null
                }
            }.last()
        }
    }

    override fun calculatePartOne(input: List<String>): Int {
        val field = Field.parseField(input)

        val loop = field.findLoop()

        return loop.size / 2
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
