import java.io.File

fun main() {
    println("Hello, world!")
    val input = File("src/day1/input").readLines()
    day1.CalculateFuel(input)
}