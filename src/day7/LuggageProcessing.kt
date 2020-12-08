package day7

class Bag(
        private val colour: String,
        private val holds: MutableSet<Pair<Int, Bag>>,
        private val canBeHeldBy: MutableSet<Bag>,
) {
    fun setHolds(input: String) {
        if (input == "no other bags") {
            return
        }

        val containedBagRegex = Regex("(\\d+) ([a-z ]+) bags?")
        for (containedBag in input.split(", ")) {
            val containedBagMatch = containedBagRegex.find(containedBag) ?:
                throw java.lang.IllegalArgumentException(
                    "Couldn't parse colour out of contained bag: $containedBag"
                )
            val containedQuantity = containedBagMatch.groupValues[1].toInt()
            val containedColour = containedBagMatch.groupValues[2]

            val containedBag = bagMap.getOrPut(containedColour) { Bag(containedColour, mutableSetOf(), mutableSetOf()) }

            holds.add(Pair(containedQuantity, containedBag))
            containedBag.canBeHeldBy.add(this)
        }
    }

    fun bagsThatCanContain() : Set<String> {
        val colourSet = mutableSetOf<String>()

        for (container in canBeHeldBy) {
            colourSet.add(container.colour)
            colourSet.addAll(container.bagsThatCanContain())
        }

        return colourSet
    }

    fun totalBagsContained() : Int {
        var count = 1

        for (bagQuantity in holds) {
            count += bagQuantity.first * bagQuantity.second.totalBagsContained()
        }

        return count
    }

    companion object {
        val bagMap: MutableMap<String, Bag> = mutableMapOf()
    }
}

fun buildBagMap(input: List<String>) {
    val bagRuleRegex = Regex(
            "([a-z ]+) bags contain (no other bags|\\d+ [a-z ]+ bags?(?:, \\d+ [a-z ]+ bags?)*)."
    )

    for (rule in input) {
        val matches = bagRuleRegex.find(rule) ?:
            throw IllegalArgumentException("Rule did not match regex: $rule")

        val containerColour = matches.groupValues[1]

        val bag = Bag.bagMap.getOrPut(containerColour) { Bag(containerColour, mutableSetOf(), mutableSetOf()) }
        bag.setHolds(matches.groupValues[2])
    }
}

fun solvePuzzle1(input: List<String>) {
    buildBagMap(input)
    val possibleContainers = Bag.bagMap.getValue("shiny gold").bagsThatCanContain()
    println(possibleContainers.size)
}

fun solvePuzzle2(input: List<String>) {
    buildBagMap(input)
    val shinyGoldBag = Bag.bagMap.getValue("shiny gold")
    // totalBagsContained counts the outer most bag to make the recursion simpler, so subtract it here.
    println(shinyGoldBag.totalBagsContained() - 1)
}
