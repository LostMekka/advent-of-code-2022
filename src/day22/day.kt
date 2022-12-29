package day22

import day22.Tile.*
import util.Direction2NonDiagonal
import util.Grid
import util.Point
import util.minus
import util.mutate
import util.pathSequence
import util.plus
import util.readInput
import util.shouldBe
import util.toGrid
import java.util.LinkedList

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 6032
    testInput.part2() shouldBe 5031

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private enum class Tile { Outside, Empty, Wall }
private sealed interface Instruction
private data class Turn(val left: Boolean) : Instruction
private data class Walk(val distance: Int) : Instruction

private class Input(
    val grid: Grid<Tile>,
    val instructions: List<Instruction>,
)

private fun <T> List<T>.intersperseAlternating(provider: (Int) -> T) =
    buildList {
        for ((i, v) in this@intersperseAlternating.withIndex()) {
            if (i > 0) add(provider(i))
            add(v)
        }
    }

private fun List<String>.parseInput(): Input {
    val instructions = last()
        .split("R")
        .map { subLine ->
            subLine.split("L")
                .map { Walk(it.toInt()) }
                .intersperseAlternating { Turn(true) }
        }
        .intersperseAlternating { listOf(Turn(false)) }
        .flatten()
    val rawGridLines = subList(0, size - 2)
    val gridWidth = rawGridLines.maxOf { it.length } + 2
    val teleportRow = " ".repeat(gridWidth)
    val grid = rawGridLines
        .map { " $it".padEnd(gridWidth, ' ') }
        .mutate { it.add(0, teleportRow); it.add(teleportRow) }
        .toGrid { _, _, char ->
            when (char) {
                ' ' -> Outside
                '.' -> Empty
                '#' -> Wall
                else -> error("unknown tile char '$char'")
            }
        }
    return Input(grid, instructions)
}

private data class State(
    val pos: Point,
    val heading: Direction2NonDiagonal,
)

private fun State.performInstruction(
    grid: Grid<Tile>,
    instruction: Instruction,
    teleportMethod: (pos: Point, heading: Direction2NonDiagonal) -> State,
): State {
    return when (instruction) {
        is Turn -> when (instruction.left) {
            true -> copy(heading = heading.rotatedLeft())
            false -> copy(heading = heading.rotatedRight())
        }
        is Walk -> {
            var pos = pos
            var heading = heading
            for(n in 1..instruction.distance) {
                val nextPos = pos + heading
                when (grid[nextPos]) {
                    Empty -> pos = nextPos
                    Wall -> break
                    Outside -> {
                        val teleportedState = teleportMethod(pos, heading)
                        when (grid[teleportedState.pos]) {
                            Empty -> {
                                pos = teleportedState.pos
                                heading = teleportedState.heading
                            }
                            Wall -> break
                            Outside -> error("teleport code teleported us outside ")
                        }
                    }
                }
            }
            State(pos, heading)
        }
    }
}

private fun teleportOnFlatMap(
    grid: Grid<Tile>,
    pos: Point,
    heading: Direction2NonDiagonal,
): State {
    var otherSide = pos
    val opposite = heading.opposite()
    do {
        otherSide += opposite
    } while (grid[otherSide] != Outside)
    return State(otherSide + heading, heading)
}

private fun teleportOnCube(
    grid: Grid<Tile>,
    pos: Point,
    heading: Direction2NonDiagonal,
    areaSize: Int,
): State {
    fun Point.areaPos() = Point((x - 1).floorDiv(areaSize), (y - 1).floorDiv(areaSize))
    val stack = LinkedList<State>().also { it += State(pos, heading) }
    while (true) {
        val (p1, d1) = stack.last
        val area = p1.areaPos()
        val d2 = d1.rotatedLeft()
        val p2 = p1.pathSequence(d2).first { it.areaPos() != area }
        val p3 = p1.rotateLeftAround(p2)
        when {
            p3 in grid && grid[p3] != Outside -> {
                if (stack.size == 1) return State(p3, d2)
                stack.removeLast()
                stack.replaceAll {
                    State(
                        pos = it.pos.rotateLeftAround(p2) - d2,
                        heading = it.heading.rotatedLeft(),
                    )
                }
            }
            p2 in grid && grid[p2] != Outside -> stack += State(p2, d1)
            else -> stack += State(p2 - d2, d2)
        }
    }
}

private fun Input.solve(teleportMethod: (pos: Point, heading: Direction2NonDiagonal) -> State): Int {
    var state = State(
        pos = Point(
            grid.row(1).withIndex().first { it.value == Empty }.index,
            1,
        ),
        heading = Direction2NonDiagonal.Right,
    )
    for (instruction in instructions) {
        state = state.performInstruction(grid, instruction, teleportMethod)
    }
    return state.toResultHash()
}

private fun State.toResultHash(): Int {
    val (x, y) = pos
    val r = when (heading) {
        Direction2NonDiagonal.Right -> 0
        Direction2NonDiagonal.Up -> 3
        Direction2NonDiagonal.Left -> 2
        Direction2NonDiagonal.Down -> 1
    }
    return 1000 * y + 4 * x + r
}

private fun Input.part1(): Int {
    return solve { pos, heading ->
        teleportOnFlatMap(grid, pos, heading)
    }
}

private fun Input.part2(): Int {
    val areaSize = if (grid.width > 20) 50 else 4
    return solve { pos, heading ->
        teleportOnCube(grid, pos, heading, areaSize)
    }
}
