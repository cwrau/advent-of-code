package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

class DayTwo : AdventOfCodeDay<Int> {
    enum class Color {
        blue, red, green
    }

    override fun calculate(input: List<String>): Int {
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
}
