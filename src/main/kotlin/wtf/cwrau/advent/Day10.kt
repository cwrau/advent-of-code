package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay
import wtf.cwrau.advent.Day10.Field.Tile.Ground.sides

object Day10 : AdventOfCodeDay<Int>(10, "Pipe Maze") {

    private class Field(private var tiles: List<List<Tile>>) {
        private lateinit var animalTile: FieldTile<Tile.Pipe>
        val indices = tiles.indices.flatMap { y -> tiles[y].indices.map { x -> y to x } }

        init {
            this.tiles = tiles.mapIndexed { y, row ->
                row.mapIndexed { x, tile ->
                    if (tile is Tile.Animal) {
                        val neighboringPipes = neighborsOf(FieldTile(y, x, tile))
                        val animalTile =
                            neighboringPipes.entries.fold(allPipes) { possiblePipes, (direction, neighboringFieldTile) ->
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
                            }.single()
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
            private val allDirections =
                Direction::class.sealedSubclasses.flatMap { it.sealedSubclasses }.mapNotNull { it.objectInstance }
            private val allQuadrants = Tile.Quadrant::class.sealedSubclasses.mapNotNull { it.objectInstance }
            private val allSides = Tile.Side::class.sealedSubclasses.mapNotNull { it.objectInstance }

            fun parse(input: List<String>): Field = Field(input.map { line ->
                line.map { symbol ->
                    allTiles.single { it.symbol == symbol }
                }
            })
        }

        data class FieldTile<T : Tile>(val y: Int, val x: Int, val tile: T)

        @Suppress("unused")
        sealed class Tile(val symbol: Char) {
            sealed class Side {
                data object SideA : Side()
                data object SideB : Side()
            }

            sealed class Pipe(
                symbol: Char,
            ) : Tile(symbol) {

                abstract val connections: Pair<Direction, Direction>
                abstract val sides: Map<Side, List<Quadrant>>

                data object Vertical : Pipe('|') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Column.Down
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(Quadrant.UpperLeft, Quadrant.LowerLeft), Side.SideB to listOf(
                                Quadrant.UpperRight, Quadrant.LowerRight
                            )
                        )
                    }
                }

                data object Horizontal : Pipe('-') {
                    override val connections by lazy {
                        Direction.Row.Left to Direction.Row.Right
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(Quadrant.UpperLeft, Quadrant.UpperRight), Side.SideB to listOf(
                                Quadrant.LowerLeft, Quadrant.LowerRight
                            )
                        )
                    }
                }

                data object NorthEastBend : Pipe('L') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Row.Right
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(
                                Quadrant.UpperLeft, Quadrant.LowerLeft, Quadrant.LowerRight
                            ), Side.SideB to listOf(Quadrant.UpperRight)
                        )
                    }
                }

                data object NorthWestBend : Pipe('J') {
                    override val connections by lazy {
                        Direction.Column.Up to Direction.Row.Left
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(Quadrant.UpperLeft), Side.SideB to listOf(
                                Quadrant.UpperRight, Quadrant.LowerLeft, Quadrant.LowerRight
                            )
                        )
                    }
                }

                data object SouthWestBend : Pipe('7') {
                    override val connections by lazy {
                        Direction.Column.Down to Direction.Row.Left
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(
                                Quadrant.UpperLeft, Quadrant.UpperRight, Quadrant.LowerRight
                            ), Side.SideB to listOf(Quadrant.LowerLeft)
                        )
                    }
                }

                data object SouthEastBend : Pipe('F') {
                    override val connections by lazy {
                        Direction.Column.Down to Direction.Row.Right
                    }
                    override val sides by lazy {
                        mapOf(
                            Side.SideA to listOf(
                                Quadrant.UpperLeft, Quadrant.UpperRight, Quadrant.LowerLeft
                            ), Side.SideB to listOf(Quadrant.LowerRight)
                        )
                    }
                }
            }

            data object Ground : Tile('.') {
                val sides by lazy { mapOf(Side.SideA to allQuadrants, Side.SideB to allQuadrants) }
            }

            data object Animal : Tile('S')
            sealed class Quadrant {
                abstract val connections: Map<Direction, Quadrant>

                data object UpperLeft : Quadrant() {
                    override val connections by lazy {
                        mapOf(
                            Direction.Column.Up to LowerLeft, Direction.Row.Left to UpperRight
                        )
                    }
                }

                data object LowerLeft : Quadrant() {
                    override val connections by lazy {
                        mapOf(
                            Direction.Column.Down to UpperLeft, Direction.Row.Left to LowerRight
                        )
                    }
                }

                data object UpperRight : Quadrant() {
                    override val connections by lazy {
                        mapOf(
                            Direction.Column.Up to LowerRight, Direction.Row.Right to UpperLeft
                        )
                    }
                }

                data object LowerRight : Quadrant() {
                    override val connections by lazy {
                        mapOf(
                            Direction.Column.Down to UpperRight, Direction.Row.Right to LowerLeft
                        )
                    }
                }
            }

            inline val tileSides
                get() = when (this) {
                    is Ground -> sides
                    is Pipe -> sides
                    else -> error("Should not happen")
                }
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
            return allDirections.mapNotNull { direction ->
                getNeighborOf(direction, fieldTile)?.let {
                    direction to it
                }
            }.toMap()
        }

        fun findLoop(): List<FieldTile<Tile.Pipe>> {
            return generateSequence(listOf(animalTile)) { loop ->
                val newConnectedPipes = loop.takeLast(2).flatMap { fieldTile ->
                    fieldTile.tile.connections.toList().mapNotNull {
                        getNeighborOf(it, fieldTile)
                    }.filterIsInstance<FieldTile<Tile.Pipe>>()
                }.distinct().filter { it !in loop }
                if (newConnectedPipes.isNotEmpty()) {
                    loop + newConnectedPipes
                } else {
                    null
                }
            }.last()
        }

        fun <T : Tile> connectedNeighborsOf(fieldTile: FieldTile<T>): Map<Tile.Side, List<Pair<FieldTile<Tile>, Tile.Side>>> {
            val neighbors = neighborsOf(fieldTile)
            val sides = when (val tile = fieldTile.tile) {
                is Tile.Ground -> tile.tileSides
                is Tile.Pipe -> tile.tileSides
                else -> error("Should not happen")
            }.mapValues { (_, quadrants) ->
                neighbors.flatMap { (direction, neighboringFieldTile) ->
                    neighboringFieldTile.tile.tileSides.map { (side, _) ->
                        neighboringFieldTile to side
                    }.filter { (neighboringFieldTile, side) ->
                        neighboringFieldTile.tile.tileSides
                            .getValue(side)
                            .any { it.connections[direction.opposite] in quadrants }
                    }
                }
//                        .filter { (direction, neighboringFieldTile) ->
//                            when (val neighboringTile = neighboringFieldTile.tile) {
//                                Tile.Animal -> error("Should not happen")
//                                Tile.Ground -> true
//                                is Tile.Pipe -> neighboringTile.sides
//                                    .any { (_, neighboringTileSideQuadrants) ->
//                                        neighboringTileSideQuadrants.any { it.connections[direction.opposite] in quadrants }
//                                    }
//                            }
//                        }
            }
//                .map { it.values }
//                .map { it.toList() }

            return sides
        }
    }

    override fun calculatePartOne(input: List<String>): Int {
        val field = Field.parse(input)

        val loop = field.findLoop()

        return loop.size / 2
    }

    override fun calculatePartTwo(input: List<String>): Int {
        val field = Field.parse(input)
        val loop = field.findLoop()

        val sides =
            field.indices.fold(listOf<List<Pair<Field.FieldTile<Field.Tile>, Field.Tile.Side>>>()) { areas, (y, x) ->
                val tile = field[y, x]!!
                if (tile is Field.Tile.Animal) {
                    error("Shouldn't happen")
                }
                val fieldTile = Field.FieldTile(y, x, tile)
                val connectedNeighborSides = field.connectedNeighborsOf(fieldTile)
//            val (connectedAreas, disconnectedAreas) = areas.partition {
//                it.any { possibleNeighboringFieldTile ->
//                    possibleNeighboringFieldTile in connectedNeighbors.flatten()
//                }
//            }
                val connectedAreasSideA = areas.filter { area ->
                    connectedNeighborSides.getValue(Field.Tile.Side.SideA).any { it in area }
                }.plus(element = listOf(fieldTile to Field.Tile.Side.SideA)).toSet()
                val connectedAreasSideB = areas.filter { area ->
                    connectedNeighborSides.getValue(Field.Tile.Side.SideB).any { it in area }
                }.plus(element = listOf(fieldTile to Field.Tile.Side.SideB)).toSet()

                val disconnectedAreas = areas - connectedAreasSideA - connectedAreasSideB
                val connectedAreas = if (connectedAreasSideA.intersect(connectedAreasSideB).isNotEmpty()) {
                    listOf(connectedAreasSideA + connectedAreasSideB)
                } else {
                    listOf(connectedAreasSideA, connectedAreasSideB)
                }
                    .map { it.flatten().distinct() }
                (connectedAreas + disconnectedAreas)
                    .filter { it.isNotEmpty() }
            }

        val outerSide = sides.first {
            it.any { (fieldTile, side) ->
                val neighbors = field.neighborsOf(fieldTile)
                neighbors.size < 4 &&
                        fieldTile.tile.tileSides.getValue(side).any { quadrant ->
                            !neighbors.keys.containsAll(quadrant.connections.keys)
                        }
            }
        }
        val innerSide = sides.first { it != outerSide }
        return (
                innerSide
                    .map { it.first }
                    .distinct() - loop.toSet()
                )
            .size
    }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            -L|F7
            7S-7|
            L|7||
            -L-J|
            L|-JF
        """.trimIndent().lines() to 4, """
            ..F7.
            .FJ|.
            SJ.L7
            |F--J
            LJ...
        """.trimIndent().lines() to 8
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        """
            ...........
            .S-------7.
            .|F-----7|.
            .||.....||.
            .||.....||.
            .|L-7.F-J|.
            .|..|.|..|.
            .L--J.L--J.
            ...........
        """.trimIndent().lines() to 4, """
            ..........
            .S------7.
            .|F----7|.
            .||....||.
            .||....||.
            .|L-7F-J|.
            .|..||..|.
            .L--JL--J.
            ..........
        """.trimIndent().lines() to 4, """
            .F----7F7F7F7F-7....
            .|F--7||||||||FJ....
            .||.FJ||||||||L7....
            FJL7L7LJLJ||LJ.L-7..
            L--J.L7...LJS7F-7L7.
            ....F-J..F7FJ|L7L7L7
            ....L7.F7||L7|.L7L7|
            .....|FJLJ|FJ|F7|.LJ
            ....FJL-7.||.||||...
            ....L---J.LJ.LJLJ...
        """.trimIndent().lines() to 8, """
            FF7FSF7F7F7F7F7F---7
            L|LJ||||||||||||F--J
            FL-7LJLJ||||||LJL-77
            F--JF--7||LJLJ7F7FJ-
            L---JF-JLJ.||-FJLJJ7
            |F|F-JF---7F7-L7L|7|
            |FFJF7L7F-JF7|JL---7
            7-L-JL7||F7|L7F-7F7|
            L.L7LFJ|||||FJL7||LJ
            L7JLJL-JLJLJL--JLJ.L
        """.trimIndent().lines() to 10
    )
}
