package day18

import util.Point3
import util.boundingCuboid
import util.readInput
import util.shouldBe
import java.util.LinkedList

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 64
    testInput.part2() shouldBe 58

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val points: List<Point3>,
) {
    val bounds = points.boundingCuboid()
}

private fun List<String>.parseInput(): Input {
    val points = map { line ->
        val (x, y, z) = line.split(",").map { it.toInt() }
        Point3(x, y, z)
    }
    return Input(points)
}

private fun Input.part1(): Int {
    var count = 0
    for (p in bounds) {
        if (p in points) {
            count += p.neighbors().count { it !in points }
        }
    }
    return count
}

private fun Input.part2(): Int {
    val outsideArea = floodFill()
    var count = 0
    for (p in bounds) {
        if (p in points) {
            count += p.neighbors().count { it !in points && it in outsideArea }
        }
    }
    return count
}

private fun Input.floodFill(): Set<Point3> {
    val extendedBounds = bounds.growBy(1)
    val expanded = mutableSetOf(extendedBounds.minPos)
    val toExpand = LinkedList(expanded)
    while (toExpand.isNotEmpty()) {
        for (p in toExpand.removeFirst().neighbors()) {
            if (p !in extendedBounds || p in expanded || p in points) continue
            expanded += p
            toExpand += p
        }
    }
    return expanded
}
