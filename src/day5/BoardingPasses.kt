package day5

fun getRow(input: String) : Int {
    var minRow = 0
    var currentRange = 128
    for (char in input) {
        currentRange /= 2
        if (char == 'B') {
            minRow += currentRange
        }
    }
    return minRow
}

fun getColumn(input: String) : Int {
    var minCol = 0
    var currentRange = 8
    for (char in input) {
        currentRange /= 2
        if (char == 'R') {
            minCol += currentRange
        }
    }
    return minCol
}

fun getSeatId(input: String) : Int {
    val row = getRow(input.substring(0, 7))
    val col = getColumn(input.substring(7))
    return row * 8 + col
}

fun solvePuzzle1(input: List<String>) {
    val maxSeatId = input.map { getSeatId(it) }
            .max()

    println(maxSeatId)
}

fun solvePuzzle2(input: List<String>) {
    val seatIds = input.map { getSeatId(it) }.sorted()
    val numSeatIds = seatIds.count()
    val adjacentSeatIds = seatIds.filterIndexed { i, seatId ->
        i > 0 && i < numSeatIds - 1 &&
                (
                        seatId - 1 != seatIds[i - 1] ||
                                seatId + 1 != seatIds[i + 1]
                        )
    }
    val mySeatId = adjacentSeatIds[0] + 1

    println(mySeatId)
}
