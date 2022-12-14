package util

fun String.extractIntGroups(regex: Regex) =
    extractStringGroups(regex)
        .map { it.toInt() }

fun String.extractStringGroups(regex: Regex) = regex
    .matchEntire(this)!!
    .groupValues
    .drop(1)
