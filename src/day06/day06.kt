package day06

import util.readInput
import util.shouldBe

fun main() {
    val day = 6
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 7
    part2(testInput) shouldBe 19

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private fun List<String>.parseInput(): String {
    return first()
}

private fun part1(input: String): Int {
    return input.windowed(4).indexOfFirst { it.toSet().size == 4 } + 4
}

private fun part2(input: String): Int {
    return input.windowed(14).indexOfFirst { it.toSet().size == 14 } + 14
}
