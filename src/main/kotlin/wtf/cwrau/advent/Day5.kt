package wtf.cwrau.advent

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import wtf.cwrau.AdventOfCodeDay

class Day5 : AdventOfCodeDay<Long> {
    private val sourceTargetMapping = Regex("-to-")

    data class Mapping(val source: String, val target: String)

    override fun calculatePartOne(input: List<String>): Long {
        val seeds = input.first().removePrefix("seeds:").trim().split(" ").map { it.toLong() }

        val (mappings: Map<Mapping, Map<LongRange, Long>>, mappingList: List<Mapping>) = input.parseMappings()
        return mappings.calculateClosestSeed(seeds, mappingList)
    }

    override fun calculatePartTwo(input: List<String>): Long {
        val seeds = input.first().removePrefix("seeds:").trim().split(" ")
            .windowed(2, 2) { (start, size) ->
                val startNumber = start.toLong()
                startNumber.rangeTo((startNumber + size.toLong()))
            }
        val (mappings: Map<Mapping, Map<LongRange, Long>>, mappingList: List<Mapping>) = input.parseMappings()
        return runBlocking(Dispatchers.IO) {
            seeds
                .map { async { mappings.calculateClosestSeed(it, mappingList) } }
                .minOf {
                    it.await()
                }
        }
    }

    private fun Map<Mapping, Map<LongRange, Long>>.calculateClosestSeed(
        seeds: Iterable<Long>,
        mappingList: List<Mapping>,
    ): Long {
        return seeds
            .minOf { seed ->
                mappingList
                    .fold(seed) { item, currentMapping ->
                        val (sourceRanges, targetRanges) = getValue(currentMapping)
                            .let { it.keys to it.values.toList() }
                        val sourceRangeMapping = sourceRanges
                            .withIndex()
                            .singleOrNull { item in it.value }
                            ?.let { it.index to item - it.value.first }
                        if (sourceRangeMapping != null) {
                            val (sourceRangeIndex, sourceIndex) = sourceRangeMapping
                            targetRanges[sourceRangeIndex] + sourceIndex
                        } else {
                            item
                        }
                    }
            }
    }

    private fun List<String>.parseMappings(): Pair<Map<Mapping, Map<LongRange, Long>>, MutableList<Mapping>> {
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
                    .associate { mappingRange ->
                        val (destinationRangeStart, sourceRangeStart, rangeLength) = mappingRange.split(" ")
                            .map { it.toLong() }
                        sourceRangeStart..<(sourceRangeStart + rangeLength) to destinationRangeStart
                    }
            }

        val firstMapping = mappings.keys.single { it.source == "seed" }
        val mappingList = mutableListOf(firstMapping)
        while (mappingList.size != mappings.keys.size) {
            mappings.keys.forEach { mapping ->
                if (mappingList.last().target == mapping.source) {
                    mappingList.add(mapping)
                }
            }
        }
        return Pair(mappings, mappingList)
    }
}
