package day24

import util.Direction2NonDiagonal
import util.Direction2NonDiagonal.*
import util.PathFindingMove
import util.PathFindingState
import util.Point
import util.Rect
import util.boundingRect
import util.findPath
import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 18
    testInput.part2() shouldBe 54

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val blizzards: Map<Point, Direction2NonDiagonal>,
    val bounds: Rect,
) {
    val start = Point(0, -1)
    val end = Point(bounds.maxX, bounds.maxY + 1)
}

private fun <T> List<T>.shrunkBy(border: Int) = subList(border, size - border)

private fun List<String>.parseInput(): Input {
    val obstacles = buildMap {
        for ((y, line) in this@parseInput.shrunkBy(1).withIndex()) {
            for ((x, char) in line.trim('#').withIndex()) {
                when (char) {
                    '>' -> put(Point(x, y), Right)
                    '^' -> put(Point(x, y), Up)
                    '<' -> put(Point(x, y), Left)
                    'v' -> put(Point(x, y), Down)
                }
            }
        }
    }
    val bounds = obstacles.keys.boundingRect()
    return Input(obstacles, bounds)
}

private fun Input.create3dSearchSpace(): List<Set<Point>> {
    val space = List(bounds.size().toInt()) { bounds.toMutableSet().apply { add(start); add(end) } }
    for ((p, d) in blizzards) {
        for (t in space.indices) {
            space[t] -= Point((p.x + t * d.dx).mod(bounds.width), (p.y + t * d.dy).mod(bounds.height))
        }
    }
    return space
}

private data class State(
    val pos: Point,
    val t: Int,
    val searchSpace: List<Set<Point>>,
    val target: Point,
) : PathFindingState<State> {
    override fun nextMoves() = sequence {
        val nextT = t + 1
        val nextSlice = searchSpace[nextT % searchSpace.size]
        for (n in pos.neighbours()) if (n in nextSlice) yield(PathFindingMove(1, copy(pos = n, t = nextT)))
        if (pos in nextSlice) yield(PathFindingMove(1, copy(t = nextT)))
    }

    override fun estimatedCostToGo() = pos.manhattanDistanceTo(target).toLong()
    override fun isGoal() = pos == target

    override fun equals(other: Any?) = other is State && other.pos == pos && other.t == t
    override fun hashCode() = 31 * pos.hashCode() + t
}

private fun Input.part1(): Int {
    val space = create3dSearchSpace()
    val path = findPath(State(start, 0, space, end)) ?: error("no path found")
    return path.nodes.size - 1
}

private fun Input.part2(): Int {
    val space = create3dSearchSpace()
    val path1 = findPath(State(start, 0, space, end)) ?: error("no path found")
    val path2 = findPath(path1.nodes.last().state.copy(target = start)) ?: error("no path found")
    val path3 = findPath(path2.nodes.last().state.copy(target = end)) ?: error("no path found")
    return path1.nodes.size + path2.nodes.size + path3.nodes.size - 3
}
