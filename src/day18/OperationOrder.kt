package day18

import day18.Expression.Type.*

class Expression(var parent: Expression? = null) {
    var value: Long? = null
    var leftChild: Expression? = null
    var rightChild: Expression? = null
    var type = NONE

    enum class Type {
        NONE,
        NUMBER,
        ADDITION,
        MULTIPLICATION,
        ;
    }

    fun evaluate(): Long {
        return when (type) {
            NONE -> {
                if (leftChild != null) leftChild!!.evaluate()
                else rightChild!!.evaluate()
            }
            NUMBER -> value!!
            ADDITION -> leftChild!!.evaluate() + rightChild!!.evaluate()
            MULTIPLICATION -> leftChild!!.evaluate() * rightChild!!.evaluate()
        }
    }
}

fun findEndOfNumber(expressionString: String, startIndex: Int): Int {
    if (startIndex == expressionString.lastIndex) return startIndex
    for (i in startIndex until expressionString.length) {
        if (!expressionString[i].isDigit()) return i - 1
    }
    return -1
}

fun tokenize(expressionString: String): List<String> {
    val tokens = mutableListOf<String>()

    var i = 0
    while (i < expressionString.length) {
        when {
            expressionString[i] == ' ' -> {
            }
            expressionString[i].isDigit() -> {
                val endOfNumber = findEndOfNumber(expressionString, i)
                tokens.add(expressionString.substring(i, endOfNumber + 1))
                i = endOfNumber
            }
            else -> tokens.add(expressionString[i].toString())
        }
        i++
    }

    return tokens
}

fun findLeftParen(tokens: List<String>, rightParenIndex: Int): Int {
    var openParens = 1
    var index = rightParenIndex
    while (openParens > 0) {
        index--
        if (index < 0) break
        when (tokens[index]) {
            "(" -> openParens--
            ")" -> openParens++
        }
    }

    return index
}

fun findRightParen(tokens: List<String>, leftParenIndex: Int): Int {
    var openParens = 1
    var index = leftParenIndex
    while (openParens > 0) {
        index++
        if (index > tokens.lastIndex) break
        when (tokens[index]) {
            "(" -> openParens++
            ")" -> openParens--
        }
    }

    return index
}

fun forceOperatorPrecedence(
        tokens: MutableList<String>,
        startIndex: Int,
        endIndex: Int
): Pair<Int, Int> {
    var parensAdded = 0
    var i = startIndex
    var newEndIndex = endIndex
    var startOfLeftOperand = startIndex
    while (i < newEndIndex) {
        when (tokens[i]) {
            "*" -> Unit
            "+" -> {
                tokens.add(startOfLeftOperand, "(")
                parensAdded++
                i++
                newEndIndex++
                val endOfRightOperand = if (tokens[i + 1] == "(") {
                    findRightParen(tokens, i + 1)
                } else {
                    i + 1
                }
                tokens.add(endOfRightOperand + 1, ")")
                parensAdded++
                newEndIndex++
            }
            "(" -> {
                startOfLeftOperand = i
                val resultsPair = forceOperatorPrecedence(
                        tokens,
                        startOfLeftOperand + 1,
                        findRightParen(tokens, i)
                )
                i = resultsPair.first
                newEndIndex += resultsPair.second
                parensAdded += resultsPair.second
            }
            ")" -> {
                startOfLeftOperand = findLeftParen(tokens, i)
            }
            else -> startOfLeftOperand = i
        }

        i++
    }

    return Pair(newEndIndex, parensAdded)
}

fun parseExpression(tokens: List<String>): Expression {
    var i = 0
    var currentExpression = Expression()
    while (i < tokens.size) {
        when (tokens[i]) {
            "(" -> {
                val matchingParenIndex = findRightParen(tokens, i)
                val subExpression = parseExpression(tokens.subList(i + 1, matchingParenIndex))
                currentExpression.value = subExpression.value
                currentExpression.leftChild = subExpression.leftChild
                subExpression.leftChild?.parent = currentExpression
                currentExpression.rightChild = subExpression.rightChild
                subExpression.rightChild?.parent = currentExpression
                currentExpression.type = subExpression.type
                i = matchingParenIndex - 1
            }
            ")" -> {
                if (i == tokens.lastIndex) break
                while (currentExpression.type != NONE) {
                    if (currentExpression.parent == null) {
                        val newExpression = Expression()
                        newExpression.leftChild = currentExpression
                        currentExpression.parent = newExpression
                    }
                    currentExpression = currentExpression.parent!!
                }
            }
            "+" -> {
                currentExpression.type = ADDITION
                currentExpression.rightChild = Expression(currentExpression)
                currentExpression = currentExpression.rightChild!!
            }
            "*" -> {
                currentExpression.type = MULTIPLICATION
                currentExpression.rightChild = Expression(currentExpression)
                currentExpression = currentExpression.rightChild!!
            }
            else -> {
                currentExpression.type = NUMBER
                currentExpression.value = tokens[i].toLong()
                if (i == tokens.lastIndex) break
                while (currentExpression.type != NONE) {
                    if (currentExpression.parent == null) {
                        val newExpression = Expression()
                        newExpression.leftChild = currentExpression
                        currentExpression.parent = newExpression
                    }
                    currentExpression = currentExpression.parent!!
                }
            }
        }
        i++
    }

    while (currentExpression.parent != null) {
        currentExpression = currentExpression.parent!!
    }

    return currentExpression
}

fun solvePuzzle1(input: List<String>) {
    val tokenLists = input.map { tokenize(it) }
    val expressions = tokenLists.map { parseExpression(it) }

    val sum = expressions.map { it.evaluate() }
            .sum()

    println(sum)
}

fun solvePuzzle2(input: List<String>) {
    val tokenLists = input.map { tokenize(it).toMutableList() }
    tokenLists.forEach { forceOperatorPrecedence(it, 0, it.size) }

    val expressions = tokenLists.map { parseExpression(it) }

    val sum = expressions.map { it.evaluate() }
            .sum()

    println(sum)
}
