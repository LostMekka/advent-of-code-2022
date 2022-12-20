package day13

import util.PeekingIterator
import util.nextOrNull
import util.peekingIterator
import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe 13
    testInput.part2() shouldBe 140

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private sealed class Packet : Comparable<Packet> {
    val stringRepresentation: String by lazy {
        when (this) {
            is Leaf -> value.toString()
            is Node -> children.joinToString(",", "[", "]") { it.stringRepresentation }
        }
    }

    override fun compareTo(other: Packet) = comparePackets(this, other)
    override fun equals(other: Any?) = other is Packet && stringRepresentation == other.stringRepresentation
    override fun hashCode() = stringRepresentation.hashCode()
}

private data class Leaf(val value: Int) : Packet()
private data class Node(val children: List<Packet>) : Packet() {
    constructor(vararg children: Packet) : this(children.toList())
}

private class Input(
    val packets: List<Packet>,
)

private fun String.toPacket(): Packet = asIterable().peekingIterator().parsePacket()
private fun PeekingIterator<Char>.parsePacket(): Packet {
    val firstChar = current()
    when {
        firstChar.isDigit() -> {
            var n = firstChar.digitToInt()
            do {
                next().digitToIntOrNull()
                    ?.let { n = n * 10 + it }
                    ?: break
            } while (true)
            return Leaf(n)
        }

        firstChar == '[' -> {
            if (next() == ']') {
                nextOrNull()
                return Node(emptyList())
            }
            val packets = mutableListOf<Packet>()
            while (true) {
                packets += parsePacket()
                when (current()) {
                    ']' -> {
                        nextOrNull()
                        return Node(packets)
                    }

                    ',' -> next()
                    else -> error("unexpected char '${current()}'")
                }
            }
        }

        else -> error("unexpected char '$firstChar'")
    }
}

private fun List<String>.parseInput(): Input {
    val packets = mapNotNull { if (it.isBlank()) null else it.toPacket() }
    return Input(packets)
}

private fun comparePackets(a: Packet, b: Packet): Int =
    when {
        a is Leaf && b is Leaf -> a.value - b.value
        a is Leaf && b is Node -> compareChildren(listOf(a), b.children)
        a is Node && b is Leaf -> compareChildren(a.children, listOf(b))
        a is Node && b is Node -> compareChildren(a.children, b.children)
        else -> error("exhaustive boolean expressions, please?")
    }

private fun compareChildren(list1: List<Packet>, list2: List<Packet>): Int {
    val iterator1 = list1.iterator()
    val iterator2 = list2.iterator()
    while (true) {
        val a = iterator1.nextOrNull()
        val b = iterator2.nextOrNull()
        return when {
            a == null && b == null -> 0
            a == null && b != null -> -1
            a != null && b == null -> 1
            a != null && b != null ->
                when (val result = comparePackets(a, b)) {
                    0 -> continue
                    else -> result
                }
            else -> error("exhaustive boolean expressions, please?")
        }
    }
}

private fun Input.part1(): Int {
    return packets.asSequence()
        .chunked(2)
        .withIndex()
        .sumOf { (i, pair) ->
            val (a, b) = pair
            if (a <= b) i + 1 else 0
        }
}

private fun Input.part2(): Int {
    val packet1 = Node(Node(Leaf(2)))
    val packet2 = Node(Node(Leaf(6)))
    val sortedPackets = (packets + listOf(packet1, packet2)).sorted()
    val i1 = sortedPackets.indexOf(packet1) + 1
    val i2 = sortedPackets.indexOf(packet2) + 1
    return i1 * i2
}
