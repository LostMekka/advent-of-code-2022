package day05

import util.extractIntGroups
import util.readInput
import util.shouldBe
import java.util.LinkedList

fun main() {
    val day = 5
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe "CMZ"
    part2(testInput) shouldBe "MCD"

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Input(
    val stacks: List<List<Char>>,
    val rules: List<Rule>,
)

private data class Rule(
    val amount: Int,
    val source: Int,
    val target: Int,
)

private val stackNumberRegex = Regex("""\d+""")
private val ruleRegex = Regex("""^move (\d+) from (\d+) to (\d+)$""")
private fun List<String>.parseInput(): Input {
    val (stackLineCount, stackNumberLine) = withIndex().first { '[' !in it.value }
    val stackCount = stackNumberRegex.findAll(stackNumberLine).count()
    val stacks = List<MutableList<Char>>(stackCount) { mutableListOf() }
    for (line in subList(0, stackLineCount)) {
        for ((i, block) in line.chunked(4).withIndex()) {
            val letter = block[1]
            if (letter != ' ') stacks[i] += letter
        }
    }

    val rules = subList(stackLineCount + 2, size).map { line ->
        val (a, b, c) = line.extractIntGroups(ruleRegex)
        Rule(
            amount = a,
            source = b - 1,
            target = c - 1,
        )
    }

    return Input(stacks, rules)
}

private fun part1(input: Input): String {
    val state = input.stacks.map { LinkedList(it.reversed()) }
    for ((amount, source, target) in input.rules) {
        repeat(amount) {
            state[target] += state[source].removeLast()
        }
    }
    return state.joinToString("") { it.last.toString() }
}

private fun part2(input: Input): String {
    val state = input.stacks.map { LinkedList(it.reversed()) }
    for ((amount, source, target) in input.rules) {
        state[target] += state[source].takeLast(amount)
        repeat(amount) {
            state[source].removeLast()
        }
    }
    return state.joinToString("") { it.last.toString() }
}
