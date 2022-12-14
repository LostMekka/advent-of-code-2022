package day04

import util.readInput
import util.shouldBe

fun main() {
    val day = 4
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 2
    part2(testInput) shouldBe 4

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val pairs: List<Pair<IntRange, IntRange>>,
)

private val inputRegex = Regex("""^(\d+)-(\d+),(\d+)-(\d+)$""")
private fun List<String>.parseInput(): Input {
    return Input(
        map { line ->
            val (a, b, c, d) = inputRegex
                .matchEntire(line)!!
                .groupValues
                .drop(1)
                .map { it.toInt() }
            a..b to c..d
        }
    )
}

private operator fun IntRange.contains(other: IntRange) = other.first in this && other.last in this
private infix fun IntRange.overlapsWith(other: IntRange) = first in other || last in other || other in this

private fun part1(input: Input): Int {
    return input.pairs.count { (a, b) -> a in b || b in a }
}

private fun part2(input: Input): Int {
    return input.pairs.count { (a, b) -> a overlapsWith b }
}
