package day17

import day17.MoveDirection.*
import util.readInput
import util.repeatingIterator
import util.shouldBe
import kotlin.math.max

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 3068
    testInput.part2() shouldBe 1514285714288L

    val input = readInput(Input::class).parseInput()

    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private data class Point(val x: Int, val y: Long) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    companion object {
        val down = Point(0, -1)
    }
}

private class Shape(
    val points: Set<Point>,
    val width: Int = points.maxOf { it.x },
) {
    constructor(vararg points: Point) : this(points.toSet())
    fun withOffset(offset: Point) = Shape(points.mapTo(mutableSetOf()) { it + offset }, width)
}

private val allShapes = listOf(
    Shape(
        Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0),
    ),
    Shape(
        /*              */ Point(1, 2), /*              */
        Point(0, 1), Point(1, 1), Point(2, 1),
        /*              */ Point(1, 0), /*              */
    ),
    Shape(
        /*              */ /*              */ Point(2, 2),
        /*              */ /*              */ Point(2, 1),
        Point(0, 0), Point(1, 0), Point(2, 0),
    ),
    Shape(
        Point(0, 3),
        Point(0, 2),
        Point(0, 1),
        Point(0, 0),
    ),
    Shape(
        Point(0, 1), Point(1, 1),
        Point(0, 0), Point(1, 0),
    ),
)

private class Board {
    val width = 7
    val points = mutableSetOf<Point>()
    var height = 0L
        private set
    var stones = 0L
        private set
    infix fun intersects(shape: Shape) = shape.points.any { it.y <= 0 || it.x < 0 || it.x >= width || it in points }
    operator fun plusAssign(shape: Shape) {
        points += shape.points
        height = max(height, shape.points.maxOf { it.y })
        stones++
    }
}

private enum class MoveDirection(val dx: Int) { Left(-1), Right(1) }

private class Input(
    val directions: List<MoveDirection>,
)

private fun List<String>.parseInput(): Input {
    val directions = first().map { if (it == '<') Left else Right }
    return Input(directions)
}

private fun Board.tryMove(shape: Shape, offset: Point): Shape? {
    return shape.withOffset(offset).takeUnless { this intersects it }
}

private fun simulate(
    board: Board,
    rockCount: Int,
    directions: Iterator<MoveDirection>,
    rocks: Iterator<Shape>,
): Int {
    var pushCount = 0
    repeat(rockCount) {
        var currRock = rocks.next().withOffset(Point(2, board.height + 4))
        while (true) {
            pushCount++
            val dx = directions.next().dx
            val afterMove = board.tryMove(currRock, Point(dx, 0))
            if (afterMove != null) currRock = afterMove

            val afterFall = board.tryMove(currRock, Point.down)
            if (afterFall != null) {
                currRock = afterFall
            } else {
                board += currRock
                break
            }
        }
    }
    return pushCount
}

private fun Input.part1(): Long {
    val directions = directions.repeatingIterator()
    val rocks = allShapes.repeatingIterator()
    val board = Board()
    simulate(board, 2022, directions, rocks)
    return board.height
}

private fun Input.part2(): Long {
    val directions = directions.repeatingIterator()
    val rocks = allShapes.repeatingIterator()
    val board = Board()
    val maxPeriods = 1_000_000_000_000L / 5
    var periodsSimulated = 0L


    var lastRun = emptyList<Log>()
    while (true) {
        val currRun = mutableListOf<Log>()
        val seenMoveIndices = mutableSetOf(directions.nextIndex())
        do {
            val h1 = board.height
            simulate(board, 5, directions, rocks)
            periodsSimulated++
            val h2 = board.height
            val i2 = directions.nextIndex()
            val cycleEnded = i2 in seenMoveIndices
            seenMoveIndices += i2
            currRun += Log(i2, (h2 - h1).toInt())
        } while (!cycleEnded)
        if (currRun deepEquals lastRun) break
        lastRun = currRun
    }
    val cycleHeight = lastRun.sumOf { it.dy }
    val cycleSize = lastRun.size

    val periodsToGo = maxPeriods - periodsSimulated
    val completeCyclesHeight = periodsToGo / cycleSize * cycleHeight
    val lastPartialCycleHeight = lastRun.take((periodsToGo % cycleSize).toInt()).sumOf { it.dy }

    return board.height + completeCyclesHeight + lastPartialCycleHeight
}

private data class Log(val i: Int, val dy: Int)
private infix fun List<Log>.deepEquals(other: List<Log>): Boolean {
    if (size != other.size) return false
    for ((a, b) in asSequence().zip(other.asSequence())) {
        if (a != b) return false
    }
    return true
}
