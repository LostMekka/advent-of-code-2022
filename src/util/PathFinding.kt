package util

import java.util.PriorityQueue

data class PathFindingNode<T: PathFindingState<T>>(
    val usedCost: Long,
    val state: T,
    val parentNode: PathFindingNode<T>?,
) {
    val estimatedCostLeft = state.estimatedCostToGo()
    val predictedTotalCost = usedCost + estimatedCostLeft
}
data class PathFindingMove<T: PathFindingState<T>>(val cost: Long, val state: T)
data class PathFindingPath<T: PathFindingState<T>>(val nodes: List<PathFindingNode<T>>, val totalCost: Long)

interface PathFindingState<T: PathFindingState<T>> {
    fun nextMoves(): Sequence<PathFindingMove<T>>
    fun estimatedCostToGo(): Long
    fun isGoal(): Boolean
}

fun <T: PathFindingState<T>> findPath(startState: T): PathFindingPath<T>? {
    val startNode = PathFindingNode(0L, startState, null)
    if (startState.isGoal()) return PathFindingPath(listOf(startNode), 0L)
    val toExpand = PriorityQueue<PathFindingNode<T>>(100, compareBy { it.predictedTotalCost })
    toExpand += startNode
    val expanded = mutableSetOf<T>()
    while (toExpand.isNotEmpty()) {
        val node = toExpand.remove()
        if (node.state in expanded) continue
        expanded += node.state
        for ((newCost, newState) in node.state.nextMoves()) {
            val newNode = PathFindingNode(node.usedCost + newCost, newState, node)
            if (newState.isGoal()) {
                val nodesAsList = buildList {
                    var n: PathFindingNode<T>? = newNode
                    while (n != null) {
                        add(n)
                        n = n.parentNode
                    }
                }
                return PathFindingPath(nodesAsList.asReversed(), newNode.usedCost)
            } else {
                toExpand += newNode
            }
        }
    }
    return null
}
