package day21

import day21.Operation.Add
import day21.Operation.Divide
import day21.Operation.Multiply
import day21.Operation.Subtract
import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 152
    testInput.part2() shouldBe 301

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private enum class Operation(private val op: (Long, Long) -> Long) {
    Add({ a, b -> a + b }),
    Subtract({ a, b -> a - b }),
    Multiply({ a, b -> a * b }),
    Divide({ a, b -> a / b }),
    ;

    operator fun invoke(a: Long, b: Long) = op(a, b)
}

private sealed interface Node {
    val result: Long
}

private class Leaf(
    override val result: Long,
) : Node

private class Branch(
    left: () -> Node,
    right: () -> Node,
    val operation: Operation,
) : Node {
    val left by lazy(left)
    val right by lazy(right)
    override val result by lazy { operation(this.left.result, this.right.result) }
}

private class Input(
    val root: Node,
    val human: Node,
)

private fun List<String>.parseInput(): Input {
    val monkeysByName = buildMap {
        for (line in this@parseInput) {
            val parts = line.split(" ")
            val name = parts.first().removeSuffix(":")
            val monkey = when (parts.size) {
                2 -> Leaf(parts.last().toLong())
                4 -> Branch(
                    left = { this.getValue(parts[1]) },
                    right = { this.getValue(parts[3]) },
                    operation = when (parts[2]) {
                        "+" -> Add
                        "-" -> Subtract
                        "*" -> Multiply
                        "/" -> Divide
                        else -> error("unknown operator in line: $line")
                    }
                )
                else -> error("could not parse line: $line")
            }
            put(name, monkey)
        }
    }
    return Input(
        root = monkeysByName["root"] ?: error("root monkey not found in input"),
        human = monkeysByName["humn"] ?: error("human not found in input"),
    )
}

private fun Input.part1(): Long {
    return root.result
}

private data class PathSegment(
    val operation: Operation,
    val humanIsLeft: Boolean,
    val otherValue: Long,
)

private fun Node.findPathTo(target: Node): List<PathSegment>? {
    if (this === target) return emptyList()
    when (this) {
        is Leaf -> return null
        is Branch -> {
            val leftResult = left.findPathTo(target)
            if (leftResult != null) return leftResult + PathSegment(operation, true, right.result)
            val rightResult = right.findPathTo(target)
            if (rightResult != null) return rightResult + PathSegment(operation, false, left.result)
            return null
        }
    }
}

private fun Input.part2(): Long {
    val path = root.findPathTo(human)?.toMutableList() ?: error("cannot reach human from root")
    var number = path.removeLast().otherValue
    while (path.isNotEmpty()) {
        val (operation, humanIsLeft, otherValue) = path.removeLast()
        when (operation) {
            Add -> number -= otherValue
            Subtract -> number = if (humanIsLeft) otherValue + number else otherValue - number
            Multiply -> number /= otherValue
            Divide -> number = if (humanIsLeft) otherValue * number else otherValue / number
        }
    }
    return number
}
