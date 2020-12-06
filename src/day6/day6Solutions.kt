package day6

fun getGroups(input: List<String>) : List<String> {
    val rawGroups = mutableListOf<String>()
    var stringBuffer = ""
    for (line in input) {
        if (line != "") {
            stringBuffer += "\n$line"
        } else {
            rawGroups.add(stringBuffer)
            stringBuffer = ""
        }
    }
    rawGroups.add(stringBuffer)
    return rawGroups
}

fun countNumberOfUniqueAnswers(groupAnswers: String) : Int {
    return groupAnswers.replace("\n", "")
            .toHashSet()
            .distinct()
            .count()
}

fun countNumberOfAllYes(groupAnswers: String) : Int {
    val numberOfResponders = groupAnswers.count { it == '\n' }

    var answerMap = mutableMapOf<Char, Int>()
    for (answer in groupAnswers) {
        if (answer != '\n') {
            answerMap[answer] = 1 + (answerMap[answer] ?: 0)
        }
    }

    return answerMap.count { it.value == numberOfResponders }
}

fun solvePuzzle1(input: List<String>) {
    val groups = getGroups(input)
    val numberOfUniqueAnswers = groups.map { countNumberOfUniqueAnswers(it) }
            .sum()

    println(numberOfUniqueAnswers)
}

fun solvePuzzle2(input: List<String>) {
    val groups = getGroups(input)
    val numberOfAllYes = groups.map { countNumberOfAllYes(it) }
            .sum()

    println(numberOfAllYes)
}
