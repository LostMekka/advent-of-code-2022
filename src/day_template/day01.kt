package day_template

import util.readInput
import util.shouldBe

fun main() {
    val day = 0 // TODO
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 1 // TODO
    part2(testInput) shouldBe 1 // TODO

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val lines: List<String>, // TODO
)

private fun List<String>.parseInput(): Input {
    return Input(this)
}

private fun part1(input: Input): Int {
    return 1 // TODO
}

private fun part2(input: Input): Int {
    return 1 // TODO
}
