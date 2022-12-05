package util

class Grid<T>(val width: Int, val height: Int, init: (Point) -> T) {
    private val items = MutableList(width * height) { init(point(it)) }
    private fun index(x: Int, y: Int) = x + y * width
    private fun index(p: Point) = p.x + p.y * width
    private fun point(i: Int) = Point(i % width, i / width)
    operator fun get(x: Int, y: Int) = items[index(x, y)]
    operator fun get(p: Point) = items[index(p)]
    operator fun set(x: Int, y: Int, value: T) {
        items[index(x, y)] = value
    }
    operator fun set(p: Point, value: T) {
        items[index(p)] = value
    }

    operator fun contains(p: Point) =
        p.x in 0 until width && p.y in 0 until height

    fun positions() = Iterable {
        iterator {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    yield(Point(x, y))
                }
            }
        }
    }

    fun values() = Iterable {
        iterator {
            for (x in 0 until width) {
                for (y in 0 until height) {
                    yield(items[index(x, y)])
                }
            }
        }
    }

    fun row(y: Int) = (0 until width).map { x -> get(x, y) }
    fun rows() = Iterable {
        iterator {
            for (y in 0 until height) {
                yield(row(y))
            }
        }
    }

    fun column(x: Int) = (0 until height).map { y -> get(x, y) }
    fun columns() = Iterable {
        iterator {
            for (x in 0 until width) {
                yield(column(x))
            }
        }
    }

    fun debugString(cellConverter: (T) -> String) =
        rows().joinToString("\n") {
            it.joinToString("", transform = cellConverter)
        }
}

fun <T> List<String>.toGrid(cellTransform: (Char) -> T) =
    Grid(first().length, size) { (x, y) -> cellTransform(this[y][x]) }
