package day02

import day02.Hand.Paper
import day02.Hand.Rock
import day02.Hand.Scissors
import util.readInput
import util.shouldBe

fun main() {
    val day = 2
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 15
    part2(testInput) shouldBe 12

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private enum class Hand(val score: Int) {
    Rock(1),
    Paper(2),
    Scissors(3),
}

private val Hand.weakness
    get() = when (this) {
        Rock -> Paper
        Paper -> Scissors
        Scissors -> Rock
    }

private val Hand.strength
    get() = when (this) {
        Rock -> Scissors
        Paper -> Rock
        Scissors -> Paper
    }

private infix fun Hand.scoreAgainst(other: Hand) =
    when (other) {
        this -> 3
        this.weakness -> 0
        else -> 6
    }

private class Input(
    val lines: List<Pair<String, String>>,
)

private fun List<String>.parseInput(): Input {
    return Input(
        map { line ->
            val (letter1, letter2) = line.split(" ")
            letter1 to letter2
        }
    )
}

private fun String.toHand() =
    when (this) {
        "A" -> Rock
        "B" -> Paper
        "C" -> Scissors
        "X" -> Rock
        "Y" -> Paper
        "Z" -> Scissors
        else -> error("oops")
    }

private fun part1(input: Input): Int {
    return input.lines.sumOf { (letter1, letter2) ->
        val opponentHand = letter1.toHand()
        val ownHand = letter2.toHand()
        ownHand.score + ownHand.scoreAgainst(opponentHand)
    }
}

private fun part2(input: Input): Int {
    return input.lines.sumOf { (letter1, letter2) ->
        val opponentHand = letter1.toHand()
        val ownHand = when (letter2) {
            "X" -> opponentHand.strength
            "Y" -> opponentHand
            "Z" -> opponentHand.weakness
            else -> error("oops")
        }
        ownHand.score + ownHand.scoreAgainst(opponentHand)
    }
}
