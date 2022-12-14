package day07

import day07.Command.CdBackward
import day07.Command.CdForward
import day07.Command.CdRoot
import day07.Command.Ls
import day07.Command.LsDiscoverDir
import day07.Command.LsDiscoverFile
import day07.Node.*
import util.readInput
import util.shouldBe

fun main() {
    val day = 7
    val testInput = readInput(day, testInput = true).parseInput()
    part1(testInput) shouldBe 95437L
    part2(testInput) shouldBe 24933642L

    val input = readInput(day).parseInput()
    println("output for part1: ${part1(input)}")
    println("output for part2: ${part2(input)}")
}

private sealed interface Command {
    object CdRoot: Command
    object CdBackward: Command
    data class CdForward(val dirName: String): Command
    object Ls: Command
    data class LsDiscoverFile(val fileName: String, val fileSize: Long): Command
    data class LsDiscoverDir(val dirName: String): Command
}

private class Input(
    val commands: List<Command>,
)

private val cdForwardRegex = Regex("""^\$ cd (.*)$""")
private val lsFileRegex = Regex("""^(\d+) (.*)$""")
private val lsDirRegex = Regex("""^dir (.*)$""")
private fun List<String>.parseInput(): Input {
    return Input(
        map { line ->
            when (line) {
                "$ cd /" -> CdRoot
                "$ cd .." -> CdBackward
                "$ ls" -> Ls
                else -> line.asCdForwardOrNull()
                    ?: line.asLsFileOrNull()
                    ?: line.asLsDirOrNull()
                    ?: error("unexpected line: '$line'")
            }
        }
    )
}

private fun String.asCdForwardOrNull() =
    cdForwardRegex
        .matchEntire(this)
        ?.groupValues
        ?.let { (_, name) -> CdForward(name) }

private fun String.asLsFileOrNull() =
    lsFileRegex
        .matchEntire(this)
        ?.groupValues
        ?.let { (_, size, name) -> LsDiscoverFile(name, size.toLong()) }

private fun String.asLsDirOrNull() =
    lsDirRegex
        .matchEntire(this)
        ?.groupValues
        ?.let { (_, name) -> LsDiscoverDir(name) }


private sealed interface Node {
    val name: String
    class Directory(override val name: String, val parentOrNull: Directory?) : Node {
        var totalSize = 0L
        var localSize = 0L
        val childDirs = mutableMapOf<String, Directory>()
        val childFiles = mutableMapOf<String, File>()
    }
    data class File(override val name: String, val size: Long) : Node
}

private fun Directory.visitFile(name: String, size: Long): File {
    val oldFileSize = childFiles[name]?.size
    require(oldFileSize == null || oldFileSize == size) { "rediscovered file, but it is different now" }
    val file = File(name, size)
    childFiles[name] = file
    localSize += size
    return file
}

private fun Directory.visitDir(name: String): Directory {
    return childDirs.getOrPut(name) { Directory(name, this) }
}

private fun Directory.updateTotalSizes(): Long {
    totalSize = localSize + childDirs.values.sumOf { it.updateTotalSizes() }
    return totalSize
}

private fun Directory.walkDirs(): Sequence<Directory> = sequence {
    yield(this@walkDirs)
    for (childDir in childDirs.values) yieldAll(childDir.walkDirs())
}

private fun Node.print(indent: String = "", isLast: Boolean = true, isRoot: Boolean = true) {
    val ownIndent = when {
        isRoot -> ""
        isLast -> "└─ "
        else -> "├─ "
    }
    when (this) {
        is File -> println("$indent$ownIndent$name $size")
        is Directory -> {
            val followupIndent = when {
                isRoot -> ""
                isLast -> "   "
                else -> "│  "
            }
            println("$indent$ownIndent$name ($localSize | $totalSize)")
            val nodes = (childFiles.values + childDirs.values).sortedBy { it.name }
            for ((i, node) in nodes.withIndex()) {
                node.print(indent + followupIndent, i == nodes.size - 1, false)
            }
        }
    }
}

private fun createTree(commands: List<Command>): Directory {
    val root = Directory("/", null)
    var currDir = root
    for (command in commands) {
        when (command) {
            CdBackward -> currDir = currDir.parentOrNull ?: error("whoops, navigated into nowhere!")
            is CdForward -> currDir = currDir.visitDir(command.dirName)
            CdRoot -> currDir = root
            Ls -> {}
            is LsDiscoverDir -> currDir.visitDir(command.dirName)
            is LsDiscoverFile -> currDir.visitFile(command.fileName, command.fileSize)
        }
    }
    root.updateTotalSizes()
    return root
}

private fun part1(input: Input): Long {
    return createTree(input.commands)
        .walkDirs()
        .filter { it.totalSize <= 100_000 }
        .sumOf { it.totalSize }
}

private fun part2(input: Input): Long {
    val root = createTree(input.commands)
    val totalSpace = 70_000_000L
    val neededSpace = 30_000_000L
    val minDirSize = root.totalSize - totalSpace + neededSpace
    return root
        .walkDirs()
        .map { it.totalSize }
        .filter { it >= minDirSize }
        .min()
}
