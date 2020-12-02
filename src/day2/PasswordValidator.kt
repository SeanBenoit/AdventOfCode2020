package day2

class Password(
        val requiredCharacter: Char,
        val minOccurrences: Int,
        val maxOccurrences: Int,
        val passwordString: String
) {
    init {
        if (minOccurrences > maxOccurrences) {
            throw IllegalArgumentException("minOccurrences cannot be greater than max occurrences")
        }
    }

    fun isValid(): Boolean {
        var occurrences = 0
        for (c in passwordString) {
            if (c == requiredCharacter) {
                occurrences++
                if (occurrences > maxOccurrences) {
                    return false
                }
            }
        }
        if (occurrences < minOccurrences) {
            return false
        }

        return true
    }

    fun isTobogganCorpValid(): Boolean {
        return (passwordString[minOccurrences - 1] == requiredCharacter) xor
                (passwordString[maxOccurrences - 1] == requiredCharacter)
    }
}

fun String.toPassword(): Password {
    val passwordRegex = Regex("(\\d+)-(\\d+) (.): (.*)")
    val matchResult = passwordRegex.matchEntire(this)
            ?: throw IllegalArgumentException("Could not parse password from string: $this")
    var (minOccurrencesString, maxOccurrencesString, requiredChar, passwordString) = matchResult.destructured
    return Password(
            requiredChar.toCharArray()[0],
            minOccurrencesString.toInt(),
            maxOccurrencesString.toInt(),
            passwordString
    )
}

fun solvePuzzle1(input: List<String>) {
    val passwords = input.map { it.toPassword() }

    var numberOfValidPasswords = 0
    for (password in passwords) {
        if (password.isValid()) {
            numberOfValidPasswords++
        }
    }

    println(numberOfValidPasswords)
}

fun solvePuzzle2(input: List<String>) {
    val passwords = input.map { it.toPassword() }

    var numberOfValidPasswords = 0
    for (password in passwords) {
        if (password.isTobogganCorpValid()) {
            numberOfValidPasswords++
        }
    }

    println(numberOfValidPasswords)
}
