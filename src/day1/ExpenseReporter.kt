package day1

fun solvePuzzle1(input: List<String>) {
    val intInput = input.map { it.toInt() }
    val answer = twoSum(intInput, 2020)
    if (answer == null) {
        println("No answer!")
        return
    }
    val product = answer.first * answer.second
    println(product)
}

fun solvePuzzle2(input: List<String>) {
    val intInput = input.map { it.toInt() }
    val answer = threeSum(intInput, 2020)
    if (answer == null) {
        println("No answer!")
        return
    }
    val product = answer.first * answer.second * answer.third
    println(product)
}

fun twoSum(input: List<Int>, target: Int): Pair<Int, Int>? {
    var previousValues = mutableSetOf<Int>()

    for (value in input) {
        previousValues.add(value)
        val difference = target - value
        if (previousValues.contains(difference)) {
            return Pair(value, difference)
        }
    }
    return null
}

fun threeSum(input: List<Int>, target: Int): Triple<Int, Int, Int>? {
    for (value in input) {
        val difference = target - value
        val differencePair = twoSum(input, difference)
        if (differencePair != null) {
            return Triple(differencePair.first, differencePair.second, value)
        }
    }
    return null
}
