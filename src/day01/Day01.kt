package day01

import util.readInput
import util.shouldBe

fun main() {
    val day = 1
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 24000
    part2(testInput) shouldBe 45000

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val calories: List<List<Int>>,
)

private fun List<String>.parseInput(): Input {
    return Input(buildList {
        var currInventory = mutableListOf<Int>()
        for (line in this@parseInput) {
            if (line.isEmpty()) {
                add(currInventory)
                currInventory = mutableListOf()
            } else {
                currInventory += line.toInt()
            }
        }
        if (currInventory.isNotEmpty()) add(currInventory)
    })
}

private fun part1(input: Input): Int {
    return input.calories.maxOf { it.sum() }
}

private fun part2(input: Input): Int {
    return input.calories
        .map { it.sum() }
        .sortedDescending()
        .take(3)
        .sum()
}
