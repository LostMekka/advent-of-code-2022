package day16

import util.extractStringGroups
import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 1651
    testInput.part2() shouldBe 1707

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

class Valve(
    val name: String,
    val rate: Int,
    connections: () -> Map<Valve, Int>,
) {
    val connections by lazy(connections)
    override fun equals(other: Any?) = this === other // instances are unique
    private val hash = name.hashCode()
    override fun hashCode() = hash
}

private val lineRegex = Regex("""^Valve (..) has flow rate=(\d+); tunnels? leads? to valves? (.+)$""")

private class Input(
    val valves: Map<String, Valve>,
) {
    val start = valves.getValue("AA")
}

private fun List<String>.parseInput(): Input {
    val fullConnections = mutableMapOf<String, MutableMap<String, Int>>()
    val valves = mutableMapOf<String, Valve>()
    for (line in this) {
        val (name, rate, connections) = line.extractStringGroups(lineRegex)
        fullConnections[name] = connections
            .split(", ")
            .associateWith { 1 }
            .toMutableMap()
            .apply { put(name, 0) }
        valves[name] = Valve(name, rate.toInt()) { fullConnections.getValue(name).mapKeys { valves.getValue(it.key) } }
    }
    do {
        var done = true
        for ((_, targets) in fullConnections) {
            val newTargets = mutableMapOf<String, Int>()
            for ((target1, cost1) in targets) {
                for ((target2, cost2) in fullConnections.getValue(target1)) {
                    if (target2 in targets) continue
                    newTargets[target2] = cost1 + cost2
                    done = false
                }
            }
            targets += newTargets
        }
    } while (!done)
    return Input(valves)
}

private data class State(
    val time: Int = 1,
    val pressureReleased: Int = 0,
    val pressureRate: Int = 0,
    val interestingValves: Set<Valve>,
    val currIdle: List<Valve>,
    val currMoving: Map<Valve, Int> = emptyMap(),
    val currActivating: Set<Valve> = emptySet(),
    val lastState: State? = null,
)

// wow, this turned out to be uglier than i thought :D
// halfway through implementing this i realized there's a more efficient way of doing this,
// but at that point i didn't want to start over again ^^
private fun State.search(maxTime: Int): State {
    if (time == maxTime) return this

    var bestResultState: State? = null
    fun callRecursive(newState: State) {
        val result = newState.search(maxTime)
        val best = bestResultState
        if (best == null || best.pressureReleased < result.pressureReleased) {
            bestResultState = result
        }
    }

    if (currIdle.isNotEmpty()) {
        for (source in currIdle) {
            var foundTarget = false
            for (target in interestingValves) {
                val walkCost = source.connections.getValue(target)
                if (time + walkCost >= maxTime) continue
                foundTarget = true
                callRecursive(copy(
                    interestingValves = interestingValves - target,
                    currIdle = currIdle - source,
                    currMoving = currMoving + (target to time + walkCost),
                    lastState = this,
                ))
            }
            if (!foundTarget) {
                callRecursive(copy(
                    currIdle = currIdle - source,
                    lastState = this,
                ))
            }
        }
    } else if (currActivating.isNotEmpty()) {
        val newPressureRate = pressureRate + currActivating.sumOf { it.rate }
        callRecursive(copy(
            time = time + 1,
            pressureReleased = pressureReleased + newPressureRate,
            pressureRate = newPressureRate,
            interestingValves = interestingValves - currActivating,
            currIdle = currIdle + currActivating,
            currActivating = emptySet(),
            lastState = this,
        ))
    } else if (currMoving.isNotEmpty()) {
        val arrivalTime = currMoving.minOf { it.value }
        val elapsedTime = arrivalTime - time
        val allTargets = currMoving.filter { it.value == arrivalTime }.keys
        callRecursive(copy(
            time = arrivalTime,
            pressureReleased = pressureReleased + elapsedTime * pressureRate,
            currMoving = currMoving - allTargets,
            currActivating = currActivating + allTargets,
            lastState = this,
        ))
    }

    return bestResultState
        ?: copy(
            time = maxTime,
            pressureReleased = pressureReleased + (maxTime - time) * pressureRate,
            lastState = this,
        )
}

private fun printPlan(best: State) {
    val list = mutableListOf(best)
    while (true) list.last().lastState?.let { list += it } ?: break
    list.asReversed().forEach { state ->
        val idles = state.currIdle.joinToString { it.name }
        val activating = state.currActivating.joinToString { it.name }
        val moving = state.currMoving.entries.joinToString { it.key.name + "/" + it.value }
        println("${state.time} -- idle: $idles | activating: $activating | moving: $moving")
    }
}

private fun Input.part1(): Int {
    val start = State(
        currIdle = listOf(start),
        interestingValves = valves.values.filter { it.rate > 0 }.toSet()
    )
    val best = start.search(30)
    printPlan(best)
    return best.pressureReleased
}

private fun Input.part2(): Int {
    val start = State(
        currIdle = listOf(start, start),
        interestingValves = valves.values.filter { it.rate > 0 }.toSet()
    )
    val best = start.search(26)
    printPlan(best)
    return best.pressureReleased
}
