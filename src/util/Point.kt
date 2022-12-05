package util

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
    infix fun manhattanDistanceTo(other: Point) = abs(x - other.x) + abs(y - other.y)

    override fun toString() = "($x, $y)"

    fun neighbours() =
        listOf(
            Point(x + 1, y),
            Point(x, y - 1),
            Point(x - 1, y),
            Point(x, y + 1),
        )

    fun neighboursIncludingDiagonals() =
        listOf(
            Point(x + 1, y),
            Point(x + 1, y - 1),
            Point(x, y - 1),
            Point(x - 1, y - 1),
            Point(x - 1, y),
            Point(x - 1, y + 1),
            Point(x, y + 1),
            Point(x + 1, y + 1),
        )

    companion object {
        val Zero = Point(0, 0)
    }
}
