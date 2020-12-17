package day16

interface Expression {
    fun evaluate(value: Int): Boolean
}

class RangeExpression(
        private val lower: Int,
        private val upper: Int,
) : Expression {
    init {
        if (lower > upper) throw IllegalArgumentException("lower must not be greater than upper")
    }

    override fun evaluate(value: Int): Boolean {
        return value in lower..upper
    }
}

class OrExpression(
        private val lhs: Expression,
        private val rhs: Expression,
) : Expression {
    override fun evaluate(value: Int): Boolean {
        return lhs.evaluate(value) or rhs.evaluate(value)
    }

}

fun parseExpression(input: String): Expression {
    if (input.contains(" or ")) {
        val splitInput = input.split(" or ".toRegex(), 2)
        return OrExpression(parseExpression(splitInput[0]), parseExpression(splitInput[1]))
    }

    val splitInput = input.split("-")
    return RangeExpression(splitInput[0].toInt(), splitInput[1].toInt())
}

fun findInvalidFields(ticket: String, rules: List<Expression>): List<Int> =
        ticket.split(",")
                .map { it.toInt() }
                .asSequence()
                .filter {
                    rules.fold(true) { acc, rule ->
                        acc && !rule.evaluate(it)
                    }
                }
                .toList()

fun eliminatePossibilities(
        ruleName: String,
        position: Int,
        possibleFieldPositions: MutableMap<String, MutableList<Int>>,
        fieldPositions: MutableMap<String, Int>
) {
    fieldPositions[ruleName] = position
    for ((name, possiblePositions) in possibleFieldPositions) {
        if (name == ruleName) continue
        if (possiblePositions.size == 1) continue
        possiblePositions.remove(position)
        if (possiblePositions.size == 1) {
            eliminatePossibilities(name, possiblePositions.single(), possibleFieldPositions, fieldPositions)
        }
    }
}

fun mapFields(tickets: List<String>, rules: Map<String, Expression>): Map<String, Int> {
    var possibleFieldPositions = mutableMapOf<String, MutableList<Int>>()
    var fieldPositions = mutableMapOf<String, Int>()

    for (ruleName in rules.keys) possibleFieldPositions[ruleName] = (0 until rules.size).toMutableList()

    for (ticket in tickets) {
        val fieldValues = ticket.split(",")
                .map { it.toInt() }
                .withIndex()

        for ((fieldPosition, fieldValue) in fieldValues) {
            for ((name, rule) in rules) {
                val thisFieldsValidPositions = possibleFieldPositions.getValue(name)
                if (!rule.evaluate(fieldValue)) thisFieldsValidPositions.remove(fieldPosition)

                if (thisFieldsValidPositions.size == 1) {
                    eliminatePossibilities(
                            name,
                            thisFieldsValidPositions.single(),
                            possibleFieldPositions,
                            fieldPositions
                    )
                }
            }
        }
    }

    return fieldPositions
}

fun solvePuzzle1(input: List<String>) {
    var index = input.indexOf("")

    val rules = input.subList(0, index)
            .map { it.split(": ".toRegex())[1] }
            .map { parseExpression(it) }

    index += 2
    val inputWithoutRules = input.drop(index)

    val nearbyTickets = inputWithoutRules.drop(3)

    val invalidValues = nearbyTickets.map { findInvalidFields(it, rules) }.flatten()

    println(invalidValues.sum())
}

fun solvePuzzle2(input: List<String>) {
    var index = input.indexOf("")

    val rules = input.subList(0, index)
            .associateBy { it.split(": ".toRegex())[0] }
            .mapValues { it.value.split(": ".toRegex())[1] }
            .mapValues { parseExpression(it.value) }

    index += 2
    val inputWithoutRules = input.drop(index)

    val myTicket = inputWithoutRules[0].split(",").map { it.toInt() }

    val nearbyValidTickets = inputWithoutRules.drop(3).filter {
        findInvalidFields(it, rules.values.toList()).isEmpty()
    }

    val fieldPositions = mapFields(nearbyValidTickets, rules)

    val departureFieldPositions = fieldPositions.filter { it.key.startsWith("departure") }

    println(departureFieldPositions.values.fold(1L) { acc, i -> acc * myTicket[i].toLong() })
}
