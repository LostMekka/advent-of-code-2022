package util

import kotlin.math.abs

data class Point3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3) = Point3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: Point3) = Point3(x - other.x, y - other.y, z - other.z)
    infix fun manhattanDistanceTo(other: Point3) = abs(x - other.x) + abs(y - other.y) + abs(z - other.z)

    override fun toString() = "($x,$y,$z)"

    companion object {
        val Zero = Point3(0, 0, 0)
    }
}
