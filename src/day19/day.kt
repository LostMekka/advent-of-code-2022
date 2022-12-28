package day19

import util.extractIntGroups
import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 33
    testInput.part2() shouldBe 56 * 62

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private operator fun IntArray.contains(other: IntArray): Boolean {
    for (i in 0..3) if (this[i] < other[i]) return false
    return true
}

private inline fun combineResourceArray(f: (Int) -> Int) = IntArray(4) { f(it) }

private class Blueprint(
    val id: Int,
    val robotCosts: Array<IntArray>,
) {
    val maxIncomes: IntArray = combineResourceArray { resource ->
        if (resource == 3) 999999 else (0..3).maxOf { botType -> robotCosts[botType][resource] }
    }
}

private val lineRegex =
    Regex("""Blueprint (\d+): Each ore robot costs (\d+) ore\. Each clay robot costs (\d+) ore\. Each obsidian robot costs (\d+) ore and (\d+) clay\. Each geode robot costs (\d+) ore and (\d+) obsidian\.""")

private class Input(
    val blueprints: List<Blueprint>,
)

private fun List<String>.parseInput(): Input {
    val blueprints = map { line ->
        val values = line.extractIntGroups(lineRegex).iterator()
        Blueprint(
            id = values.next(),
            robotCosts = arrayOf(
                intArrayOf(values.next(), 0, 0, 0),
                intArrayOf(values.next(), 0, 0, 0),
                intArrayOf(values.next(), values.next(), 0, 0),
                intArrayOf(values.next(), 0, values.next(), 0),
            ),
        )
    }
    return Input(blueprints)
}

private data class State(
    val maxTime: Int,
    val blueprint: Blueprint,
    var minute: Int,
    val inventory: IntArray,
    val income: IntArray,
    var maxGeodes: Int = 0,
) {
    constructor(maxTime: Int, blueprint: Blueprint) : this(
        maxTime = maxTime,
        blueprint = blueprint,
        minute = 1,
        inventory = intArrayOf(0, 0, 0, 0),
        income = intArrayOf(1, 0, 0, 0),
    )
}

private inline fun IntArray.mutate(f: (Int) -> Int) {
    for (i in 0..3) this[i] = f(i)
}

// thats weird:
// when i split this function into two (one for nextRobot==null and one for nextRobot!= null)
// i would expect to get either a very minor performance boost, or none at all.
// but instead i get a performance LOSS of a factor of 2... jvm optimizations are weird sometimes ^^
private fun State.search(nextRobot: Int?): Int {
    if (minute > maxTime) return inventory.last()

    if (nextRobot == null) {
        // i would have liked to use some form of maxOf {...} here, but this makes it up to 6 times slower...
        var best = 0
        for (nextRobot in 0..3) {
            if (income[nextRobot] < blueprint.maxIncomes[nextRobot]) {
                val score = search(nextRobot)
                if (score > best) best = score
            }
        }
        return best
    }

    val cost = blueprint.robotCosts[nextRobot]
    return if (cost in inventory) {
        inventory.mutate { inventory[it] + (income[it] - cost[it]) }
        income[nextRobot]++
        minute++
        val result = search(null)
        minute--
        income[nextRobot]--
        inventory.mutate { inventory[it] - (income[it] - cost[it]) }
        result
    } else {
        inventory.mutate { inventory[it] + income[it] }
        minute++
        val result = search(nextRobot)
        minute--
        inventory.mutate { inventory[it] - income[it] }
        result
    }
}

private fun findBestScore(blueprint: Blueprint, maxTime: Int): Int {
    val t1 = System.currentTimeMillis()
    val score = State(maxTime, blueprint).search(null)
    println("blueprint ${blueprint.id} has best score $score ($maxTime step sim took ${System.currentTimeMillis() - t1}ms)")
    return score
}

private fun Input.part1(): Int {
    return blueprints.sumOf { it.id * findBestScore(it, 24) }
}

private fun Input.part2(): Int {
    // i am probably missing some way to reduce branching in the search,
    // since the test run took 5.5 minutes and the real run took 1 minute.
    // but i am too lazy to look for more optimizations :D
    return blueprints
        .take(3)
        .map { findBestScore(it, 32) }
        .reduce { a, b -> a * b }
}
