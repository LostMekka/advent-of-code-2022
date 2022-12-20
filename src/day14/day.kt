package day14

import day14.Tile.Empty
import day14.Tile.Sand
import day14.Tile.Wall
import util.Grid
import util.Point
import util.readInput
import util.shouldBe
import kotlin.math.sign

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 24
    testInput.part2() shouldBe 93

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val minX: Int,
    val maxX: Int,
    val minY: Int,
    val maxY: Int,
    val lines: List<List<Point>>,
)

private fun List<String>.parseInput(): Input {
    var minX = Int.MAX_VALUE
    var maxX = Int.MIN_VALUE
    val minY = 0
    var maxY = Int.MIN_VALUE
    val lines = map { line ->
        line.split(" -> ")
            .map { point ->
                val (x, y) = point.split(",").map { it.toInt() }
                if (x < minX) minX = x
                if (x > maxX) maxX = x
                if (y > maxY) maxY = y
                Point(x, y)
            }
    }
    maxY += 2
    minX = minOf(minX, 500 - maxY)
    maxX = maxOf(maxX, 500 + maxY)
    return Input(
        minX,
        maxX,
        minY,
        maxY,
        lines,
    )
}

private enum class Tile { Empty, Wall, Sand }

private fun Input.createGrid(): Grid<Tile> {
    val grid = Grid(maxX - minX + 1, maxY - minY + 1) { Empty }
    for (line in lines) {
        var x = -1
        var y = -1
        var isFirstSegment = true
        for ((a, b) in line.windowed(2)) {
            val dx = (b.x - a.x).sign
            val dy = (b.y - a.y).sign
            if (dx != 0 && dy != 0) error("found diagonal wall")
            if (isFirstSegment) {
                isFirstSegment = false
                x = a.x
                y = a.y
                grid[x - minX, y - minY] = Wall
            }
            while (x != b.x || y != b.y) {
                x += dx
                y += dy
                grid[x - minX, y - minY] = Wall
            }
        }
    }
    return grid
}

private fun Input.simulateSand(grid: Grid<Tile>): Int {
    val startPoint = Point(500 - minX, 0)
    var sandCount = 0
    outer@ while (true) {
        var (x, y) = startPoint
        while (true) {
            when {
                y == maxY -> break@outer
                grid[x, y + 1] == Empty -> y++
                x == 0 -> break@outer
                grid[x - 1, y + 1] == Empty -> {
                    x--; y++
                }
                x == grid.width - 1 -> break@outer
                grid[x + 1, y + 1] == Empty -> {
                    x++; y++
                }
                else -> {
                    grid[x, y] = Sand
                    sandCount++
                    if (x == startPoint.x && y == startPoint.y) break@outer
                    break
                }
            }
        }
    }
    return sandCount
}

private fun Input.part1(): Int {
    val grid = createGrid()
    return simulateSand(grid)
}

private fun Input.part2(): Int {
    val grid = createGrid()
    for (x in grid.xRange) grid[x, grid.height - 1] = Wall
    return simulateSand(grid)
}
