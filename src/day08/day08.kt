package day08

import util.Grid
import util.readInput
import util.shouldBe
import util.toGrid

fun main() {
    val day = 8
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 21
    part2(testInput) shouldBe 8

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private class Tree(
    val height: Int,
    var isVisible: Boolean = false,
    var visibilityScore: Int = 1,
)

private class Input(
    val grid: Grid<Tree>,
)

private fun List<String>.parseInput(): Input {
    return Input(toGrid { Tree(it.digitToInt()) })
}

private fun computeVisibility(trees: List<Tree>) {
    var maxHeight = -1
    for (tree in trees) {
        if (tree.height > maxHeight) {
            maxHeight = tree.height
            tree.isVisible = true
        }
    }
}

private fun part1(input: Input): Int {
    for (column in input.grid.columns()) {
        computeVisibility(column)
        computeVisibility(column.asReversed())
    }
    for (row in input.grid.rows()) {
        computeVisibility(row)
        computeVisibility(row.asReversed())
    }
    return input.grid.values().count { it.isVisible }
}

private fun computeVisibilityScore(trees: List<Tree>) {
    val distances = IntArray(10)
    for (tree in trees) {
        tree.visibilityScore *= distances[tree.height]
        for (i in 0..9) distances[i] = if (tree.height >= i) 1 else distances[i] + 1
    }
}

private fun part2(input: Input): Int {
    for (column in input.grid.columns()) {
        computeVisibilityScore(column)
        computeVisibilityScore(column.asReversed())
    }
    for (row in input.grid.rows()) {
        computeVisibilityScore(row)
        computeVisibilityScore(row.asReversed())
    }
    return input.grid.values().maxOf { it.visibilityScore }
}
