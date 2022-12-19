package day09

import util.Direction2NonDiagonal
import util.Direction2NonDiagonal.*
import util.Point
import util.plus
import util.readInput
import util.shouldBe
import kotlin.math.abs
import kotlin.math.sign

fun main() {
    val day = 9
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 88
    part2(testInput) shouldBe 36

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private data class Move(
    val direction: Direction2NonDiagonal,
    val amount: Int,
)

private class Input(
    val moves: List<Move>,
)

private fun List<String>.parseInput(): Input {
    val moves = map {
        val (d, n) = it.split(" ")
        Move(
            direction = when (d) {
                "R" -> Right
                "U" -> Up
                "L" -> Left
                "D" -> Down
                else -> error("unknown direction '$d'")
            },
            amount = n.toInt()
        )
    }
    return Input(moves)
}

private fun part1(input: Input): Int {
    return solve(input, 2)
}

private fun part2(input: Input): Int {
    return solve(input, 10)
}

private fun solve(input: Input, ropeLength: Int): Int {
    val positions = Array(ropeLength) { Point(0, 0) }
    val visitedPoints = mutableSetOf(positions.last())
    for ((direction, amount) in input.moves) {
        repeat(amount) {
            positions[0] = positions[0] + direction
            for (i in 0..ropeLength - 2) {
                val a = positions[i]
                val b = positions[i + 1]
                val dx = a.x - b.x
                val dy = a.y - b.y
                if (maxOf(abs(dx), abs(dy)) > 1) {
                    positions[i + 1] = Point(b.x + dx.sign, b.y + dy.sign)
                }
            }
            visitedPoints += positions.last()
        }
    }
    return visitedPoints.size
}
