package day10

import util.readInput
import util.shouldBe
import kotlin.math.abs

fun main() {
    val day = 10
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 13140
    part2(testInput) shouldBe 1

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private sealed class Instruction(val executionTime: Int)
private object Noop : Instruction(1)
private data class AddX(val amount: Int) : Instruction(2)

private class Input(
    val instructions: List<Instruction>,
)

private fun List<String>.parseInput(): Input {
    val instructions = map {
        when (it) {
            "noop" -> Noop
            else -> AddX(it.split(" ").last().toInt())
        }
    }
    return Input(instructions)
}

private fun List<Instruction>.execute(onStateChange: (time: Int, x: Int) -> Unit) {
    var t = 0
    var x = 1
    for (instruction in this) {
        repeat(instruction.executionTime) {
            onStateChange(++t, x)
        }
        if (instruction is AddX) x += instruction.amount
    }
}

private fun part1(input: Input): Int {
    var sum = 0
    input.instructions.execute { time, x ->
        if (time % 40 == 20) sum += time * x
    }
    return sum
}

private fun part2(input: Input): Int {
    input.instructions.execute { time, x ->
        val crtPos = (time - 1) % 40
        if (abs(crtPos - x) <= 1) print("#") else print(".")
        if (crtPos == 39) println()
    }
    // solution printed:
    // ####.####..##..####.###..#..#.###..####.
    // #....#....#..#.#....#..#.#..#.#..#.#....
    // ###..###..#....###..#..#.#..#.#..#.###..
    // #....#....#.##.#....###..#..#.###..#....
    // #....#....#..#.#....#.#..#..#.#.#..#....
    // ####.#.....###.####.#..#..##..#..#.####.
    return 1
}
