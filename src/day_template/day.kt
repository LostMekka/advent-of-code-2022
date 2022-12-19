package day_template

import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 1 // TODO
    testInput.part2() shouldBe 1 // TODO

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val lines: List<String>, // TODO
)

private fun List<String>.parseInput(): Input {
    return Input(this)
}

private fun Input.part1(): Int {
    return 1 // TODO
}

private fun Input.part2(): Int {
    return 1 // TODO
}
