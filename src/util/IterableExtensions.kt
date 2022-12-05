package util

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]
operator fun <T> List<T>.component9() = this[8]
operator fun <T> List<T>.component10() = this[9]

fun <T> List<T>.mutate(block: (MutableList<T>) -> Unit): List<T> =
    toMutableList().also(block)

operator fun <T> T.plus(tail: List<T>): List<T> {
    val result = ArrayList<T>(tail.size + 1)
    result.add(this)
    result.addAll(tail)
    return result
}

fun <T> List<T>.split(
    includeSplittingItem: Boolean = false,
    ignoreFirstEmptyChunk: Boolean = true,
    predicate: (T) -> Boolean,
): List<List<T>> {
    if (isEmpty()) return emptyList()
    val outerList = mutableListOf<List<T>>()
    var innerList = mutableListOf<T>()
    outerList += innerList
    for (element in this) {
        if (predicate(element)) {
            if (!ignoreFirstEmptyChunk || innerList.isNotEmpty() || outerList.isNotEmpty()) innerList = mutableListOf()
            if (includeSplittingItem) innerList += element
            outerList += innerList
        } else {
            innerList += element
        }
    }
    return outerList
}

inline fun <T1, T2, R> Iterable<T1>.crossProductWith(
    other: Iterable<T2>,
    crossinline transform: (T1, T2) -> R,
) = crossProductOf(this, other, transform)

inline fun <T1, T2, R> crossProductOf(
    iterable1: Iterable<T1>,
    iterable2: Iterable<T2>,
    crossinline transform: (T1, T2) -> R,
) = sequence {
    for (element1 in iterable1) {
        for (element2 in iterable2) {
            this.yield(transform(element1, element2))
        }
    }
}

inline fun <T1, T2, T3, R> Iterable<T1>.crossProductWith(
    other1: Iterable<T2>,
    other2: Iterable<T3>,
    crossinline transform: (T1, T2, T3) -> R,
) = crossProductOf(this, other1, other2, transform)

inline fun <T1, T2, T3, R> crossProductOf(
    iterable1: Iterable<T1>,
    iterable2: Iterable<T2>,
    iterable3: Iterable<T3>,
    crossinline transform: (T1, T2, T3) -> R,
) = sequence {
    for (element1 in iterable1) {
        for (element2 in iterable2) {
            for (element3 in iterable3) {
                yield(transform(element1, element2, element3))
            }
        }
    }
}
