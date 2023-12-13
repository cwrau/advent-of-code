package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day02 : AdventOfCodeDay<Int>(2, "Cube Conundrum") {
    enum class Color {
        blue, red, green
    }

    override fun calculatePartOne(input: List<String>): Int {
        val games = parseGames(input)
        val filter = mapOf(
            Color.blue to 14,
            Color.red to 12,
            Color.green to 13
        )
        return games
            .filterValues { game ->
                !game.any { draws ->
                    draws.any { (color, amount) ->
                        filter.getValue(color) < amount
                    }
                }
            }
            .keys
            .sum()
    }

    private fun parseGames(input: List<String>): Map<Int, List<Map<Color, Int>>> {
        val games = input
            .filter { it.isNotEmpty() }
            .associate { gameLine ->
                val id = gameLine.removePrefix("Game ")
                    .takeWhile { it.isDigit() }
                    .toInt()
                val draws = gameLine.split(":")[1]
                    .trim()
                    .split(";")
                    .map { drawStrings ->
                        drawStrings
                            .trim()
                            .split(",")
                            .associate { drawString ->
                                val (count, color) = drawString.trim()
                                    .split(" ")
                                Color.valueOf(color) to count.toInt()
                            }
                    }
                id to draws
            }
        return games
    }

    override fun calculatePartTwo(input: List<String>): Int {
        val games = parseGames(input)

        return games
            .map { (_, games) ->
                games
                    .fold(mapOf<Color, Int>()) { acc, game ->
                        Color.entries
                            .associateWith {
                                maxOf(acc[it] ?: 0, game[it] ?: 0)
                            }
                    }
            }.sumOf {
                it.values.reduce(Int::times)
            }
    }

    override val partOneExamples: Map<List<String>, Int> = mapOf(
        """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
        """.trimIndent().lines() to 8
    )

    override val partTwoExamples: Map<List<String>, Int> = mapOf(
        partOneExamples.keys.first() to 2286
    )
}
