package day12

import util.Grid
import util.PathFindingMove
import util.PathFindingState
import util.Point
import util.findPath
import util.readInput
import util.shouldBe
import util.toGrid

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 31
    testInput.part2() shouldBe 29

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val grid: Grid<Int>,
    val start: Point,
    val end: Point,
)

private fun List<String>.parseInput(): Input {
    var start: Point? = null
    var end: Point? = null
    val grid = toGrid { x, y, char ->
        when (char) {
            'S' -> {
                start = Point(x, y)
                'a'.code
            }
            'E' -> {
                end = Point(x, y)
                'z'.code
            }
            else -> char.code
        }
    }
    return Input(
        grid = grid,
        start = start ?: error("no start point found"),
        end = end ?: error("no end point found"),
    )
}

class StateForPart1(
    val grid: Grid<Int>,
    val currPos: Point,
    val end: Point,
) : PathFindingState<StateForPart1> {
    override fun nextMoves(): Sequence<PathFindingMove<StateForPart1>> =
        currPos.neighbours()
            .asSequence()
            .filter { it in grid && grid[it] <= grid[currPos] + 1 }
            .map { PathFindingMove(1, StateForPart1(grid, it, end)) }

    override fun estimatedCostToGo(): Long =
        currPos.manhattanDistanceTo(end).toLong()

    override fun isGoal(): Boolean = currPos == end

    override fun equals(other: Any?) = other is StateForPart1 && currPos == other.currPos
    override fun hashCode() = currPos.hashCode()
}

private fun Input.part1(): Int {
    val path = findPath(StateForPart1(grid, start, end)) ?: error("no path found")
    return path.nodes.size - 1
}

private class StateForPart2(
    val grid: Grid<Int>,
    val currPos: Point,
) : PathFindingState<StateForPart2> {
    override fun nextMoves(): Sequence<PathFindingMove<StateForPart2>> =
        currPos.neighbours()
            .asSequence()
            .filter { it in grid && grid[it] >= grid[currPos] - 1 }
            .map { PathFindingMove(1, StateForPart2(grid, it)) }

    override fun estimatedCostToGo(): Long =
        grid.positions()
            .filter { grid[it] == 'a'.code }
            .minOf { currPos manhattanDistanceTo it }
            .toLong()

    override fun isGoal(): Boolean = grid[currPos] == 'a'.code

    override fun equals(other: Any?) = other is StateForPart2 && currPos == other.currPos
    override fun hashCode() = currPos.hashCode()
}

private fun Input.part2(): Int {
    val startState = StateForPart2(grid, end)
    val path = findPath(startState) ?: error("no path found")
    return path.nodes.size - 1
}
