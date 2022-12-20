package day15

import util.Point
import util.extractIntGroups
import util.readInput
import util.shouldBe
import util.without
import kotlin.math.abs

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1(10) shouldBe 26
    testInput.part2(20) shouldBe 56000011

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1(2000000)}")
    println("output for part2: ${input.part2(4000000)}")
}

private data class Sensor(
    val pos: Point,
    val beaconPos: Point,
)

private class Input(
    val sensors: List<Sensor>,
)

private val inputRegex = Regex("""^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)""")
private fun List<String>.parseInput(): Input {
    val sensors = map {
        val (sx, sy, bx, by) = it.extractIntGroups(inputRegex)
        Sensor(Point(sx, sy), Point(bx, by))
    }
    return Input(sensors)
}

private fun Input.part1(row: Int): Int {
    val covered = mutableSetOf<Int>()
    for (sensor in sensors) {
        val range = sensor.pos manhattanDistanceTo sensor.beaconPos
        val dy = abs(row - sensor.pos.y)
        if (dy > range) continue
        val dx = range - dy
        for (x in sensor.pos.x-dx..sensor.pos.x+dx) covered += x
    }
    for (sensor in sensors) {
        if (sensor.pos.y == row) covered -= sensor.pos.x
        if (sensor.beaconPos.y == row) covered -= sensor.beaconPos.x
    }
    return covered.size
}

private fun Input.part2(size: Int): Long {
    for (row in 0..size) {
        var free = listOf(0..size)
        for (sensor in sensors) {
            val range = sensor.pos manhattanDistanceTo sensor.beaconPos
            val dy = abs(row - sensor.pos.y)
            if (dy > range) continue
            val dx = range - dy
            val coveredRange = sensor.pos.x - dx..sensor.pos.x + dx
            free = free.flatMap { it without coveredRange }
        }
        if (free.isNotEmpty()) {
            return free.single().first * 4000000L + row
        }
    }
    error("nothing found")
}
