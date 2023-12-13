package wtf.cwrau.advent

import wtf.cwrau.AdventOfCodeDay

object Day05 : AdventOfCodeDay<Long>(5, "If You Give A Seed A Fertilizer") {
    private val sourceTargetMapping = Regex("-to-")

    data class Mapping(val source: String, val target: String)
    data class MappingEntry(
        private val sourceStart: Long,
        private val destinationStart: Long,
        private val rangeLength: Long,
    ) {
        val sourceRange by lazy { sourceStart..<sourceStart + rangeLength }
        fun destinationForSource(source: Long) = destinationStart + (source - sourceStart)
    }

    override fun calculatePartOne(input: List<String>): Long {
        val seeds = input.first().removePrefix("seeds:").trim().split(" ").map { it.toLong() }

        val mappings = input.parseMappings(true)
        return mappings.calculateClosestSeed(listOf(seeds), true)
    }

    override fun calculatePartTwo(input: List<String>): Long {
        val seeds = input.first().removePrefix("seeds:").trim().split(" ").map { it.toLong() }
            .chunked(2) { (start, size) ->
                start.rangeTo(start + size)
            }
        val mappings = input.parseMappings()
        return mappings.calculateClosestSeed(seeds)
    }

    private fun Map<Mapping, List<MappingEntry>>.calculateClosestSeed(
        seedLists: Iterable<Iterable<Long>>,
        reversed: Boolean = true,
    ): Long {
        fun convertThroughMapping(item: Long, mappings: List<MappingEntry>): Long {
            val find = mappings
                .find { item in it.sourceRange }
            val destinationForSource = find
                ?.destinationForSource(item)
            return destinationForSource
                ?: item
        }

        val mappingList = if (reversed) {
            keys.reversed()
        } else {
            keys
        }.map { getValue(it) }

        return if (reversed) {
            generateSequence(0, Long::inc)
                .first { location ->
                    val seed = mappingList.fold(location, ::convertThroughMapping)
                    seedLists.any {
                        if (it is LongRange) {
                            it.first <= seed && seed <= it.last
                        } else {
                            seed in it
                        }
                    }
                }
        } else {
            seedLists
                .minOf { seeds ->
                    seeds.minOf { seed ->
                        mappingList.fold(seed, ::convertThroughMapping)
                    }
                }
        }
    }

    private fun List<String>.parseMappings(reversed: Boolean = true): Map<Mapping, List<MappingEntry>> {
        val mappingsInput = drop(2)
        val mappings = mappingsInput
            .flatMapIndexed { index: Int, line: String ->
                when {
                    index == 0 || index == mappingsInput.lastIndex -> listOf(index)
                    line.isEmpty() -> listOf(index - 1, index + 1)
                    else -> emptyList()
                }
            }
            .windowed(2, 2) { (from, to) -> mappingsInput.slice(from..to) }
            .associate { map ->
                val (source, target) = map.first().removeSuffix("map:").trim().split(sourceTargetMapping)
                Mapping(source, target) to map.drop(1).filter { it.isNotEmpty() }
                    .map { mappingRange ->
                        val (destinationRangeStart, sourceRangeStart, rangeLength) = mappingRange.split(" ")
                            .map { it.toLong() }
                        if (reversed) {
                            MappingEntry(destinationRangeStart, sourceRangeStart, rangeLength)
                        } else {
                            MappingEntry(sourceRangeStart, destinationRangeStart, rangeLength)
                        }
                    }
            }

        return mappings
    }

    override val partOneExamples: Map<List<String>, Long> = mapOf(
        """
        seeds: 79 14 55 13

        seed-to-soil map:
        50 98 2
        52 50 48

        soil-to-fertilizer map:
        0 15 37
        37 52 2
        39 0 15

        fertilizer-to-water map:
        49 53 8
        0 11 42
        42 0 7
        57 7 4

        water-to-light map:
        88 18 7
        18 25 70

        light-to-temperature map:
        45 77 23
        81 45 19
        68 64 13

        temperature-to-humidity map:
        0 69 1
        1 0 69

        humidity-to-location map:
        60 56 37
        56 93 4
    """.trimIndent().lines() to 35
    )

    override val partTwoExamples: Map<List<String>, Long> = mapOf(
        partOneExamples.keys.first() to 46
    )
}
