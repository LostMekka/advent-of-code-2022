package day03

import util.readInput
import util.shouldBe

fun main() {
    val day = 3
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 157
    part2(testInput) shouldBe 70

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val lines: List<String>,
)

private fun List<String>.parseInput(): Input {
    return Input(this)
}

private val Char.priority
    get() = when {
        isLowerCase() -> code - 96
        else -> code - 38
    }

private fun part1(input: Input): Int {
    return input.lines.sumOf { line ->
        val (a, b) = line.toList()
            .chunked(line.length / 2)
            .map { it.toSet() }
        val type = a.intersect(b).single()
        type.priority
    }
}

private fun part2(input: Input): Int {
    return input.lines
        .chunked(3)
        .sumOf { group ->
            val type = group
                .map { it.toSet() }
                .reduce { a, b -> a intersect b }
                .single()
            type.priority
        }
}
