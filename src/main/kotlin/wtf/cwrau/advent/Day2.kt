package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

class Day2 : AdventOfCodeDay<Int> {
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
}
