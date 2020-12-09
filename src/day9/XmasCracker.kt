package day9

import kotlin.math.max
import kotlin.math.min

class FixedSizeQueue<T>(private val size: Int) {
    private val contents = MutableList<T?>(size) { null }
    private var startPosition = 0
    private var endPosition = 0

    fun getElement(index: Int): T {
        if (index >= size) throw IllegalArgumentException("Index $index is out of bounds")
        return contents[(startPosition + index) % size]
                ?: throw IllegalArgumentException("Nothing stored in position $index")
    }

    fun size(): Int {
        return size
    }

    fun push(element: T) {
        if (contents[endPosition] != null) {
            throw Exception("Queue is full")
        }
        contents[endPosition] = element
        endPosition = (endPosition + 1) % size
    }

    fun pop(): T {
        val temp = contents[startPosition] ?: throw Exception("Queue is empty")
        contents[startPosition] = null
        startPosition = (startPosition + 1) % size
        return temp
    }

    // For debugging
    fun print() {
        var output = contents[startPosition].toString()
        for (i in 1 until size) {
            output += ", " + contents[(startPosition + i) % size]
        }
        println(output)
    }
}

fun twoSum(input: FixedSizeQueue<Long>, target: Long): Boolean {
    var previousValues = mutableSetOf<Long>()

    for (i in 0 until input.size()) {
        val value = input.getElement(i)
        val difference = target - value
        if (value != difference && previousValues.contains(difference)) {
            return true
        }
        previousValues.add(value)
    }
    return false
}

fun findInvalidNumber(input: List<Long>, preambleSize: Int): Long {
    val preamble = FixedSizeQueue<Long>(preambleSize)
    for (i in 0 until preambleSize) {
        preamble.push(input[i])
    }

    for (i in preambleSize until input.size) {
        if (!twoSum(preamble, input[i])) {
            return input[i]
        }
        preamble.pop()
        preamble.push(input[i])
    }
    throw IllegalStateException("Input does not contain an invalid number")
}

fun findChainSum(input: List<Long>, target: Long): Pair<Long, Long> {
    for (i in 0 until input.size - 1) {
        var currentSum = input[i]
        var minInSum = input[i]
        var maxInSum = input[i]
        for (j in i + 1 until input.size) {
            currentSum += input[j]
            minInSum = min(minInSum, input[j])
            maxInSum = max(maxInSum, input[j])
            when {
                currentSum == target -> return Pair(minInSum, maxInSum)
                currentSum > target -> break
            }
        }
    }

    throw IllegalStateException("Input does not contain a contiguous set that sums to $target")
}

fun solvePuzzle1(input: List<String>) {
    val longInput = input.map { it.toLong() }
    println(findInvalidNumber(longInput, 25))
}

fun solvePuzzle2(input: List<String>) {
    val longInput = input.map { it.toLong() }
    val invalidNumber = findInvalidNumber(longInput, 25)
    val (first, last) = findChainSum(longInput, invalidNumber)
    println(first + last)
}
