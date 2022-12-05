package util

fun String.with8BitColor(fg: Int, bg: Int? = null): String {
    val bgCode = bg?.let { "\u001B[48;5;${it}m" } ?: ""
    return "\u001b[38;5;${fg}m$bgCode$this\u001b[0m"
}
