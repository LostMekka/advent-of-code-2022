package util

import java.io.File
import kotlin.reflect.KClass

fun readInput(name: String) = File("src/$name.txt").readLines()
fun readInput(dayNumber: Int, testInput: Boolean = false) =
    dayNumber.toString()
        .padStart(2, '0')
        .let { if (testInput) "day${it}/test" else "day$it/input" }
        .let { readInput(it) }
fun readInput(dayNumber: KClass<*>, testInput: Boolean = false) =
    readInput(dayNumber.qualifiedName!!.split(".").first().removePrefix("day").toInt(), testInput)

fun <T> readInput(dayNumber: Int, testInput: Boolean = false, transform: (String) -> T) =
    readInput(dayNumber, testInput).map(transform)
