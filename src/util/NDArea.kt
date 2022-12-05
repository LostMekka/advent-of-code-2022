package util

interface NDArea<T : NDArea<T, TPoint>, TPoint> : Iterable<TPoint> {
    operator fun contains(p: TPoint): Boolean
    operator fun contains(other: T): Boolean
    fun size(): Long
    fun intersect(other: T): T?
}

//////// 1D Area ///////////
// reuses IntRange, but cannot implement NDArea (type classes, anyone?)
infix fun IntRange.intersect(other: IntRange): IntRange? {
    val min = maxOf(first, other.first)
    val max = minOf(last, other.last)
    return when {
        min > max -> null
        else -> min..max
    }
}

operator fun IntRange.contains(other: IntRange) = other.first in this && other.last in this

//////// 2D Area ///////////
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Rect(x1: Int, y1: Int, x2: Int, y2: Int) : NDArea<Rect, Point> {
    constructor(p1: Point, p2: Point) : this(p1.x, p1.y, p2.x, p2.y)
    constructor(xRange: IntRange, yRange: IntRange) : this(xRange.first, yRange.first, xRange.last, yRange.last)

    val minX = minOf(x1, x2)
    val maxX = maxOf(x1, x2)
    val minY = minOf(y1, y2)
    val maxY = maxOf(y1, y2)

    val width = maxX - minX + 1
    val height = maxY - minY + 1

    val minPos = Point(minX, minY)
    val maxPos = Point(maxX, maxY)

    val xRange get() = minX..maxX
    val yRange get() = minY..maxY

    override fun toString() = "Rect($minPos - $maxPos)"
    override fun iterator() = iterator { for (y in yRange) for (x in xRange) yield(Point(x, y)) }
    override operator fun contains(p: Point) = p.x in xRange && p.y in yRange
    override operator fun contains(other: Rect) = other.xRange in xRange && other.yRange in yRange
    override fun size() = width.toLong() * height
    override fun intersect(other: Rect): Rect? {
        val newXRange = xRange.intersect(other.xRange) ?: return null
        val newYRange = yRange.intersect(other.yRange) ?: return null
        return Rect(newXRange, newYRange)
    }

    fun expandedBy(size: Int) = Rect(minX - size, minY - size, maxX + size, maxY + size)
}

fun Iterable<Point>.boundingRect(): Rect {
    val first = first()
    var minX = first.x
    var maxX = first.x
    var minY = first.y
    var maxY = first.y
    for (p in this) {
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
    }
    return Rect(minX, minY, maxX, maxY)
}

//////// 3D Area ///////////
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Cuboid(x1: Int, y1: Int, z1: Int, x2: Int, y2: Int, z2: Int) : NDArea<Cuboid, Point3> {
    constructor(p1: Point3, p2: Point3) : this(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z)
    constructor(xRange: IntRange, yRange: IntRange, zRange: IntRange) : this(xRange.first,
        yRange.first,
        zRange.first,
        xRange.last,
        yRange.last,
        zRange.last)

    val minX = minOf(x1, x2)
    val maxX = maxOf(x1, x2)
    val minY = minOf(y1, y2)
    val maxY = maxOf(y1, y2)
    val minZ = minOf(z1, z2)
    val maxZ = maxOf(z1, z2)

    val width = maxX - minX + 1
    val height = maxY - minY + 1
    val depth = maxZ - minZ + 1

    val minPos = Point3(minX, minY, minZ)
    val maxPos = Point3(maxX, maxY, maxZ)

    val xRange get() = minX..maxX
    val yRange get() = minY..maxY
    val zRange get() = minZ..maxZ

    val volume = width.toLong() * height.toLong() * depth.toLong()

    override fun toString() = "Cuboid($minPos - $maxPos)"
    override fun iterator() = iterator { for (z in zRange) for (y in yRange) for (x in xRange) yield(Point3(x, y, z)) }
    override operator fun contains(p: Point3) = p.x in xRange && p.y in yRange && p.z in zRange
    override operator fun contains(other: Cuboid) = other.xRange in xRange && other.yRange in yRange && other.zRange in zRange
    override fun size() = volume
    override fun intersect(other: Cuboid): Cuboid? {
        val newXRange = xRange.intersect(other.xRange) ?: return null
        val newYRange = yRange.intersect(other.yRange) ?: return null
        val newZRange = zRange.intersect(other.zRange) ?: return null
        return Cuboid(newXRange, newYRange, newZRange)
    }
}

fun Iterable<Point3>.boundingCuboid(): Cuboid {
    val first = first()
    var minX = first.x
    var maxX = first.x
    var minY = first.y
    var maxY = first.y
    var minZ = first.z
    var maxZ = first.z
    for (p in this) {
        if (p.x < minX) minX = p.x
        if (p.x > maxX) maxX = p.x
        if (p.y < minY) minY = p.y
        if (p.y > maxY) maxY = p.y
        if (p.z < minZ) minZ = p.z
        if (p.z > maxZ) maxZ = p.z
    }
    return Cuboid(minX, minY, minZ, maxX, maxY, maxZ)
}
