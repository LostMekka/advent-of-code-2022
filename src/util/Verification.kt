package util

infix fun <T> T.shouldBe(other: T) {
    check(this == other) { "check failed: $this != $other" }
    println("test OK: $this == $other")
}
