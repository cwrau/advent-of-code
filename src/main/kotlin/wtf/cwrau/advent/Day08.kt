package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day08 : AdventOfCodeDay<Long>(8, "Haunted Wasteland") {
    enum class Direction {
        L, R
    }

    override fun calculatePartOne(input: List<String>): Long {
        val infiniteDirections = input.createInfiniteDirections()
        val map = input.parseMap()

        return map.countMoves("AAA", infiniteDirections) { it == "ZZZ" }
    }

    private fun Map<String, Pair<String, String>>.countMoves(
        startNode: String,
        directions: Iterator<Direction>,
        targetReached: (String) -> Boolean,
    ): Long {
        val moves = generateSequence(startNode) { node ->
            val direction = directions.next()
            val nextDecision = this[node]!!
            val nextNode = when (direction) {
                Direction.L -> nextDecision.first
                Direction.R -> nextDecision.second
            }
            if (targetReached(nextNode)) {
                null
            } else {
                nextNode
            }
        }
        return moves.count().toLong()
    }

    override fun calculatePartTwo(input: List<String>): Long {
        val infiniteDirections = input.createInfiniteDirections()
        val map = input.parseMap()

        // Math magix, I got no idea 🤣
        return map.keys.filter { it.endsWith('A') }
            .map { map.countMoves(it, infiniteDirections) { it.endsWith('Z') } }
            .map { it.toBigInteger() }
            .reduce { acc, moves -> acc * moves / acc.gcd(moves) }
            .toLong()
    }

    // I tried it this way, doesn't finish 🤣
    @Suppress("unused")
    private fun tooSlow(
        map: Map<String, Pair<String, String>>,
        infiniteDirections: Iterator<Direction>,
    ) {
        var nodes = map.keys.filter { it.endsWith('A') }
        var moves = 0L
        for (direction in infiniteDirections) {
            moves++
            nodes = nodes.map {
                val (left, right) = map[it]!!
                when (direction) {
                    Direction.L -> left
                    else -> right
                }
            }
            if (nodes.all { it.endsWith('Z') }) {
                break
            }
        }
    }

    private fun List<String>.createInfiniteDirections(): Iterator<Direction> {
        val directions = take(1).single().map { Direction.valueOf(it.toString()) }

        val infiniteDirections: Iterator<Direction> = object : Iterator<Direction> {
            private var index = 0
            private val lastIndex = directions.lastIndex
            override fun hasNext() = true

            override fun next(): Direction {
                if (index > lastIndex) {
                    index = 0
                }
                return directions[index++]
            }
        }
        return infiniteDirections
    }

    private fun List<String>.parseMap() = drop(1)
        .filter { it.isNotEmpty() }
        .associate {
            val (name, branches) = it.split(" = ")
            val (left, right) = branches.trim('(', ')').split(',').map { it.trim() }
            name to (left to right)
        }

    override val partOneExamples: Map<List<String>, Long> = mapOf(
        """
            RL

            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent().lines() to 2,
        """
            LLR

            AAA = (BBB, BBB)
            BBB = (AAA, ZZZ)
            ZZZ = (ZZZ, ZZZ)
        """.trimIndent().lines() to 6
    )

    override val partTwoExamples: Map<List<String>, Long> = mapOf(
        """
            LR

            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
        """.trimIndent().lines() to 6
    )
}
