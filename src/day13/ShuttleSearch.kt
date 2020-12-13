package day13

import java.util.*

fun findEarliestBus(earliestDeparture: Int, busIds: List<Int>): Pair<Int, Int> {
    var departureTime = earliestDeparture
    while (true) {
        for (bus in busIds) {
            if (departureTime % bus == 0) {
                return Pair(bus, departureTime - earliestDeparture)
            }
        }
        departureTime++
    }
}

fun bruteForce(buses: Map<Int, Int>): Long {
    val (maxBusOffset, maxBusId) = buses.maxBy { it.value }!!

    var t = -1L * maxBusOffset
    outer@ while (true) {
        t += maxBusId
        for ((offset, busId) in buses) {
            if ((t + offset) % busId != 0L) continue@outer
        }
        return t
    }
}

fun solvePuzzle1(input: List<String>) {
    val earliestDeparture = input[0].toInt()
    val busIds = input[1].split(",")
            .filter { it != "x" }
            .map { it.toInt() }
    val (earliestBus, waitTime) = findEarliestBus(earliestDeparture, busIds)
    println(earliestBus * waitTime)
}

fun solvePuzzle2(input: List<String>) {
    val startTime = Date()
    println("Started at: $startTime")
    val busesWithOffset = input[1].split(",")
            .withIndex()
            .associateBy { it.index } // Map<Int, <IndexedValue<String>>
            .mapValues { it.value.value } // Map<Int, String>
            .filter { it.value != "x" }
            .mapValues { it.value.toInt() } // Map<Int, Int>
    val firstValidTimestamp = bruteForce(busesWithOffset)
    val endTime = Date()
    println("Answer is: $firstValidTimestamp")
    val runTime = endTime.time - startTime.time
    println("Found in: $runTime") // this is in milliseconds
    println("Finished at: $endTime")
}
