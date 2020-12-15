package day15

import java.util.*

fun playMemoryGame(startingNumbers: List<Int>, lastTurn: Int): Int {
    val numbersSpoken = mutableMapOf<Int, Int>()
    var currentTurn = 0
    var currentNumber = startingNumbers[0]

    for (nextNumber in startingNumbers.drop(1)) {
        numbersSpoken[currentNumber] = currentTurn
        currentNumber = nextNumber
        currentTurn++
    }


    for (turn in currentTurn until lastTurn - 1) {
        val nextNumber = turn - numbersSpoken.getOrDefault(currentNumber, turn)
        numbersSpoken[currentNumber] = turn
        currentNumber = nextNumber
    }

    return currentNumber
}

fun solvePuzzle1(input: List<String>) {
    val intInput = input[0].split(",").map { it.toInt() }

    println(playMemoryGame(intInput, 2020))
}

fun solvePuzzle2(input: List<String>) {
    val intInput = input[0].split(",").map { it.toInt() }

    println(playMemoryGame(intInput, 30000000))
}
