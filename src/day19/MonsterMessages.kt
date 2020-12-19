package day19

val rulesMap = mutableMapOf<Int, Rule>()

private var hackyProblem2 = false

class Rule {
    var requiredString = ""
    var lhs = mutableListOf<Int>()
    var rhs = mutableListOf<Int>()
    val subRulesList = mutableListOf<Int>()

    fun toRegexString(): String {
        if (hackyProblem2) {
            if (rulesMap[8] == this) {
                val rule42String = rulesMap[42]!!.toRegexString()
                return "("+rule42String+")+"
            }
            if (rulesMap[11] == this) {
                val rule31String = rulesMap[31]!!.toRegexString()
                val rule42String = rulesMap[42]!!.toRegexString()
                return "("+rule42String+")+("+rule31String+")+"
            }
        }
        if (subRulesList.isNotEmpty()) {
            return "(" + subRulesList.joinToString("") { rulesMap.getValue(it).toRegexString() } + ")"
        }
        if (lhs.isNotEmpty() && rhs.isNotEmpty()) {
            val lhsRegex = lhs.joinToString("") { rulesMap.getValue(it).toRegexString() }
            val rhsRegex = rhs.joinToString("") { rulesMap.getValue(it).toRegexString() }
            return "(("+lhsRegex+")|("+rhsRegex+"))"
        }
        return "("+requiredString+")"
    }
}

fun parseRules(input: List<String>) {
    for (line in input) {
        if (line == "") return
        val (idString, ruleString) = line.split(": ")
        val newRule = Rule()
        // Parse rules that match a character
        val requiredString = "\"(.*)\"".toRegex()
                .find(ruleString)
                ?.groupValues?.get(1)
        if (requiredString != null) {
            newRule.requiredString = requiredString
        } else if (!ruleString.contains("|")) {
            // Parse rules that match a list of subrules
            newRule.subRulesList.addAll(ruleString.trim().split(" ").map { it.toInt() })
        } else {
            // Parse rules with ORs
            val (lhsString, rhsString) = ruleString.split(" | ")
            newRule.lhs.addAll(lhsString.trim().split(" ").map { it.toInt() })
            newRule.rhs.addAll(rhsString.trim().split(" ").map { it.toInt() })
        }
        rulesMap[idString.toInt()] = newRule
    }
}

fun countValidLines(input: List<String>): Int {
    val rulesRegexString = rulesMap.getValue(0).toRegexString()
    val rulesRegex = "^$rulesRegexString$".toRegex()

    var count = 0
    for (line in input) {
        if (line.contains(":") || line.isEmpty()) continue
        if (rulesRegex.matches(line)) count++
    }
    return count
}

fun solvePuzzle1(input: List<String>) {
    parseRules(input)

    hackyProblem2 = false

    println(countValidLines(input))
}

fun solvePuzzle2(input: List<String>) {
    parseRules(input)

    hackyProblem2 = true

    println(countValidLines(input))
}
