package day11

import util.readInput
import util.shouldBe
import java.util.LinkedList

fun main() {
    val day = 11
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 10605L
    part2(testInput) shouldBe 2713310158L

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private data class Monkey(
    val index: Int,
    val items: List<Int>,
    val operation: (Int) -> Int,
    val testDivisor: Int,
    val targetWhenDivisible: Int,
    val targetWhenNotDivisible: Int,
)

private class Input(
    val monkeys: List<Monkey>,
)

private fun List<String>.parseInput(): Input {
    fun op(op: (Int) -> Int) = op // jumping through some hoops to avoid double braces later on
    val monkeys = chunked(7).mapIndexed { index, lines ->
        val items = lines[1]
            .removePrefix("  Starting items: ")
            .split(", ")
            .mapTo(LinkedList()) { it.toInt() }
        val operation = lines[2]
            .removePrefix("  Operation: new = ")
            .let { formula ->
                val operand by lazy { formula.split(" ").last().toInt() }
                when {
                    formula == "old * old" -> op { it * it }
                    formula.startsWith("old * ") -> op { it * operand }
                    formula.startsWith("old + ") -> op { it + operand }
                    else -> error("unsupported operation '$formula'")
                }
            }
        val test = lines[3]
            .removePrefix("  Test: divisible by ")
            .toInt()
        val target1 = lines[4]
            .removePrefix("    If true: throw to monkey ")
            .toInt()
        val target2 = lines[5]
            .removePrefix("    If false: throw to monkey ")
            .toInt()
        require(index != target1 && index != target2) { "monkey $index tries to throw item to itself! how rude!!" }
        Monkey(
            index = index,
            items = items,
            operation = operation,
            testDivisor = test,
            targetWhenDivisible = target1,
            targetWhenNotDivisible = target2,
        )
    }
    return Input(monkeys)
}

private sealed interface Item {
    fun mutate(inspectingMonkey: Monkey)
    fun test(monkey: Monkey): Boolean
}

private class SimpleItem(var value: Int) : Item {
    override fun mutate(inspectingMonkey: Monkey) {
        value = inspectingMonkey.operation(value) / 3
    }

    override fun test(monkey: Monkey) = value % monkey.testDivisor == 0
}

private class ModuloItem(
    initialValue: Int,
    private val monkeys: List<Monkey>,
) : Item {
    val modValues = monkeys.mapTo(mutableListOf()) { initialValue % it.testDivisor }

    override fun mutate(inspectingMonkey: Monkey) {
        for ((i, moduloMonkey) in monkeys.withIndex()) {
            modValues[i] = inspectingMonkey.operation(modValues[i]) % moduloMonkey.testDivisor
        }
    }

    override fun test(monkey: Monkey) = modValues[monkey.index] == 0
}

private fun <T : Item> Input.simulate(numberOfRounds: Int, itemMapper: (Int) -> T): Long {
    val inventories = monkeys.map { it.items.mapTo(mutableListOf(), itemMapper) }
    val counts = LongArray(monkeys.size)
    repeat(numberOfRounds) {
        for (monkey in monkeys) {
            val inventory = inventories[monkey.index]
            for (item in inventory) {
                item.mutate(monkey)
                val targetIndex = when {
                    item.test(monkey) -> monkey.targetWhenDivisible
                    else -> monkey.targetWhenNotDivisible
                }
                inventories[targetIndex].add(item)
            }
            counts[monkey.index] += inventory.size.toLong()
            inventory.clear()
        }
    }
    return counts.sortedDescending().take(2).let { (a, b) -> a * b }
}

private fun part1(input: Input): Long {
    return input.simulate(20) { SimpleItem(it) }
}

private fun part2(input: Input): Long {
    return input.simulate(10_000) { ModuloItem(it, input.monkeys) }
}
