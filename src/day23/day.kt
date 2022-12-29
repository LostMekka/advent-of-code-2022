package day23

import util.Point
import util.boundingRect
import util.readInput
import util.shouldBe
import java.util.LinkedList

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 110
    testInput.part2() shouldBe 20

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val startingPositions: Set<Point>,
)

private fun List<String>.parseInput(): Input {
    val elves = buildSet {
        for ((y, line) in this@parseInput.withIndex()) {
            for ((x, char) in line.withIndex()) {
                if (char == '#') add(Point(x, y))
            }
        }
    }
    return Input(elves)
}

private data class State(
    var positions: Set<Point>,
    val strategies: LinkedList<(List<Point>, Set<Point>) -> Point?>,
    var isMoving: Boolean = true,
)

private fun Input.startingState() = State(
    startingPositions,
    LinkedList<(List<Point>, Set<Point>) -> Point?>().apply {
        add { n, p -> n[2].takeIf { n[1] !in p && n[2] !in p && n[3] !in p } }
        add { n, p -> n[6].takeIf { n[5] !in p && n[6] !in p && n[7] !in p } }
        add { n, p -> n[4].takeIf { n[3] !in p && n[4] !in p && n[5] !in p } }
        add { n, p -> n[0].takeIf { n[7] !in p && n[0] !in p && n[1] !in p } }
    },
)

private fun State.simulateRound() {
    val proposalsByTarget = positions.groupBy { currPos ->
        val neighbours = currPos.neighboursIncludingDiagonals()
        when {
            neighbours.none { it in positions } -> null
            else -> strategies
                .asSequence()
                .mapNotNull { it(neighbours, positions) }
                .firstOrNull()
        }
    }
    isMoving = false
    positions = buildSet {
        for ((target, sources) in proposalsByTarget) {
            when  {
                sources.size > 1 || target == null -> addAll(sources)
                else -> {
                    add(target)
                    isMoving = true
                }
            }
        }
    }
    strategies += strategies.removeFirst()
}

private fun Input.part1(): Long {
    val state = startingState()
    repeat(10) { state.simulateRound() }
    return state.positions.boundingRect().size() - state.positions.size
}

private fun Input.part2(): Int {
    val state = startingState()
    var i = 0
    while (state.isMoving) {
        state.simulateRound()
        i++
    }
    return i
}
