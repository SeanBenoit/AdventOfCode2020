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
        println("$this $type $value $leftChild $rightChild")
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
    // Surround the whole expression in parens to make building the parse tree easier
//    val tokens = mutableListOf("(")
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

//    tokens.add(")")

    return tokens
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

fun parseExpression(tokens: List<String>): Expression {
    var i = 0
    var currentExpression = Expression()
    while (i < tokens.size) {
        when (tokens[i]) {
            "(" -> {
                val matchingParenIndex = findRightParen(tokens, i)
                println("$i $matchingParenIndex")
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

}
