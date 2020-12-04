package day4

fun isValidPassport(str: String): Boolean {
    return str.contains("byr") &&
            str.contains("iyr") &&
            str.contains("eyr") &&
            str.contains("hgt") &&
            str.contains("hcl") &&
            str.contains("ecl") &&
            str.contains("pid")
}

fun isActuallyValidPassport(str: String): Boolean {
    val byr = "byr:(\\d{4})\\s".toRegex().find(str) ?: return false
    if (byr.groupValues.count() != 2) return false
    val byrInt = byr.groupValues[1].toInt()
    if (byrInt < 1920 || byrInt > 2002) return false

    val iyr = "iyr:(\\d{4})\\s".toRegex().find(str) ?: return false
    if (iyr.groupValues.count() != 2) return false
    val iyrInt = iyr.groupValues[1].toInt()
    if (iyrInt < 2010 || iyrInt > 2020) return false

    val eyr = "eyr:(\\d{4})\\s".toRegex().find(str) ?: return false
    if (eyr.groupValues.count() != 2) return false
    val eyrInt = eyr.groupValues[1].toInt()
    if (eyrInt < 2020 || eyrInt > 2030) return false

    val hgt = "hgt:(\\d+)(cm|in)\\s".toRegex().find(str) ?: return false
    if (hgt.groupValues.count() != 3) return false
    val hgtInt = hgt.groupValues[1].toInt()
    val hgtUnits = hgt.groupValues[2]
    if (
            (hgtUnits == "cm" &&
                (hgtInt < 150 || hgtInt > 193)
                ) ||
            (hgtUnits == "in" &&
                (hgtInt < 59 || hgtInt > 76)
                )
    ) return false

    "hcl:#[\\da-f]{6}\\s".toRegex().find(str) ?: return false

    "ecl:(amb|blu|brn|gry|grn|hzl|oth)\\s".toRegex().find(str) ?: return false

    "pid:[\\d]{9}\\s".toRegex().find(str) ?: return false

    return true
}

fun separatePassports(input: List<String>): List<String> {
    val rawPassports = mutableListOf<String>()
    var stringBuffer = ""
    for (line in input) {
        if (line != "") {
            stringBuffer += "\n$line"
        } else {
            rawPassports.add("$stringBuffer ")
            stringBuffer = ""
        }
    }
    rawPassports.add("$stringBuffer ")
    return rawPassports
}

fun solvePuzzle1(input: List<String>) {
    val passports = separatePassports(input)

    println(passports.count { isValidPassport(it) })
}

fun solvePuzzle2(input: List<String>) {
    val passports = separatePassports(input)

    println(passports.count { isActuallyValidPassport(it) })
}
