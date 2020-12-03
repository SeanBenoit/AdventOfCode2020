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
        var occurrences = passwordString.count { it == requiredCharacter }
        if (occurrences < minOccurrences || occurrences > maxOccurrences) {
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

    var numberOfValidPasswords = passwords.count { it.isValid() }

    println(numberOfValidPasswords)
}

fun solvePuzzle2(input: List<String>) {
    val passwords = input.map { it.toPassword() }

    var numberOfValidPasswords = passwords.count { it.isTobogganCorpValid() }

    println(numberOfValidPasswords)
}
