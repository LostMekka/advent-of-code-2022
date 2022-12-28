package day20

import util.readInput
import util.shouldBe
import util.skipEvery

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 3
    testInput.part2() shouldBe 1623178306

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val numbers: List<Long>,
)

private fun List<String>.parseInput(): Input {
    return Input(map { it.toLong() })
}

private class Cell(val number: Long) {
    lateinit var leftCell: Cell
    lateinit var rightCell: Cell

    fun sequence() = sequence {
        var c = this@Cell
        while (true) {
            c = c.rightCell
            yield(c)
        }
    }

    companion object {
        fun link(left: Cell, right: Cell) {
            left.rightCell = right
            right.leftCell = left
        }
    }
}

private class LinkedRing(
    val cells: List<Cell>,
) {
    val zero = cells.single { it.number == 0L }
}

private fun Input.createLinkedRing(multiplier: Long): LinkedRing {
    val cells = numbers.map { Cell(it * multiplier) }
    for ((a, b) in cells.windowed(2)) Cell.link(a, b)
    Cell.link(cells.last(), cells.first())
    return LinkedRing(cells)
}

private fun LinkedRing.mix() {
    for (cell in cells) {
        val n = cell.number.mod(cells.size - 1)
        if (n == 0) continue

        // cut the cell out of the ring
        val oldLeft = cell.leftCell
        val oldRight = cell.rightCell
        Cell.link(oldLeft, oldRight)

        // insert the cell at the new position
        var newLeft = cell
        repeat(n) { newLeft = newLeft.rightCell }
        val newRight = newLeft.rightCell
        Cell.link(newLeft, cell)
        Cell.link(cell, newRight)
    }
}

private fun LinkedRing.extractAnswer() =
    zero.sequence()
        .skipEvery(999)
        .take(3)
        .sumOf { it.number }

private fun Input.part1(): Long {
    val ring = createLinkedRing(1)
    ring.mix()
    return ring.extractAnswer()
}

private fun Input.part2(): Long {
    val ring = createLinkedRing(811589153)
    repeat(10) { ring.mix() }
    return ring.extractAnswer()
}
