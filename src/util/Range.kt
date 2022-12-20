package util

infix fun IntRange.without(other: IntRange): List<IntRange> {
    if (last < other.first || first > other.last) return listOf(this)
    return buildList {
        if (first < other.first) this += first until other.first
        if (last > other.last) this += other.last+1..last
    }
}
