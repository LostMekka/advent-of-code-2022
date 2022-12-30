package day25

import util.readInput
import util.shouldBe

fun main() {
    val testInput = readInput(Input::class, testInput = true).parseInput()
    testInput.part1() shouldBe "2=-1=0"
    testInput.part2() shouldBe Unit

    val input = readInput(Input::class).parseInput()
    println("output for part1: ${input.part1()}")
    println("output for part2: ${input.part2()}")
}

private class Input(
    val snafuNumbers: List<String>,
)

private fun List<String>.parseInput(): Input {
    return Input(this)
}

private fun Char.snafuDigitToInt() = when (this) {
    '2' -> 2
    '1' -> 1
    '0' -> 0
    '-' -> -1
    '=' -> -2
    else -> error("unknown digit: $this")
}

private fun Int.digitToSnafu() = when (this) {
     2 -> '2'
     1 -> '1'
     0 -> '0'
     -1 -> '-'
     -2 -> '='
    else -> error("int $this is not a valid snafu digit")
}

private fun List<String>.sumSnafu(): String {
    var i = 1
    var carry = 0
    var result = ""
    do {
        val relevantDigits = mapNotNull { it.getOrNull(it.length - i)?.snafuDigitToInt() }
        var currDigit = carry + relevantDigits.sum()
        carry = 0
        while (currDigit > 2) {
            currDigit -= 5
            carry++
        }
        while (currDigit < -2) {
            currDigit += 5
            carry--
        }
        result += currDigit.digitToSnafu()
        i++
    } while (carry != 0 || relevantDigits.isNotEmpty())
    return result.reversed().trimStart('0')
}

private fun Input.part1(): String {
    return snafuNumbers.sumSnafu()
}

private fun Input.part2(): Unit {
    return Unit // sorry :D
}
