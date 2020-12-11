package day10

fun sortIntoHashSet(input: List<String>): HashSet<Int> {
    val sortedSet = hashSetOf<Int>()
    for (value in input) {
        sortedSet.add(value.toInt())
    }
    return sortedSet
}

fun countGaps(adapters: HashSet<Int>): Triple<Int, Int, Int> {
    if (adapters.isEmpty()) return Triple(0, 0, 1)
    // Third gap count starts at one because of implicit ending gap
    var gapCounts = mutableListOf(0, 0, 1)

    var lastAdapter = 0
    for (adapter in 1..adapters.max()!!) {
        if (!adapters.contains(adapter)) continue
        val gap = adapter - lastAdapter
        gapCounts[gap - 1]++
        lastAdapter = adapter
    }

    return Triple(gapCounts[0], gapCounts[1], gapCounts[2])
}

fun splitAtRequiredAdapters(adapters: HashSet<Int>): List<List<Int>> {
    var adapterChunks = mutableListOf<MutableList<Int>>()
    val maxAdapter = adapters.max()!!

    var lastAdapter = 0
    var secondLastAdapter = 0
    var currentAdapter = 0
    while (currentAdapter < maxAdapter) {
        val adapterChunk = mutableListOf<Int>()
        while (currentAdapter < maxAdapter) {
            val lastGap = currentAdapter - lastAdapter
            val secondLastGap = lastAdapter - secondLastAdapter

            // secondLastGap can never be 3 because the lastGap == 3 case (below) updates
            // secondLastAdapter and lastAdapter to the same value.
            if (lastGap == 3) {
                // Current and last adapters are required and are the ends of two separate chunks
                secondLastAdapter = currentAdapter
                lastAdapter = currentAdapter
                break
            } else if ((lastGap == 2) && (secondLastGap == 2)) {
                // Last two gaps are both 2, last adapter is required and is end of both chunks
                secondLastAdapter = lastAdapter
                currentAdapter = lastAdapter
                break
            }
            // Last two gaps total 3 or less, this is fine.
            adapterChunk.add(currentAdapter)
            secondLastAdapter = lastAdapter
            lastAdapter = currentAdapter
            do {
                currentAdapter++
            } while (!adapters.contains(currentAdapter))
        }
        adapterChunks.add(adapterChunk)
    }

    val adapterChunk = adapterChunks.last()
    val lastGap = currentAdapter - lastAdapter
    val secondLastGap = lastAdapter - secondLastAdapter
    if (lastGap == 3) {
        // Current and last adapters are required and are the ends of two separate chunks
        adapterChunks.add(mutableListOf(currentAdapter))
    } else if ((lastGap == 2) && (secondLastGap == 2)) {
        // Last two gaps are both 2, last adapter is required and is end of both chunks
        adapterChunks.add(mutableListOf(lastAdapter, currentAdapter))
    } else {
        // Last two gaps total 3 or less, this is fine.
        adapterChunk.add(currentAdapter)
    }

    return adapterChunks
}

fun convertToGapList(adapters: List<Int>): List<Int> {
    var gapList = mutableListOf<Int>()

    var lastAdapter = adapters[0]
    for (adapter in adapters.drop(1)) {
        gapList.add(adapter - lastAdapter)
        lastAdapter = adapter
    }

    return gapList
}

fun countChunkConfigurations(gapList: List<Int>): Int {
    // base cases: the list has one or two elements
    if (gapList.size == 1) return 1
    if (gapList.size == 2) {
        // if the two elements total less than three, we can choose to combine them or not
        if (gapList.sum() <= 3) return 2
        // otherwise, we can't combine them
        return 1
    }

    var count = 1

    for (i in 1 until gapList.size) {
        if (gapList[i - 1] + gapList[i] <= 3) {

        }
    }

    return count
}

fun countTotalValidConfigurations(gapLists: List<List<Int>>): Int {
    var count = 1

    val gapListsSeen = hashMapOf<List<Int>, Int>()
    for (gapList in gapLists) {
        if (gapListsSeen.containsKey(gapList)) {
            count *= gapListsSeen.getValue(gapList)
            continue
        }
        val numberOfValidConfigurations = countChunkConfigurations(gapList)
        gapListsSeen[gapList] = numberOfValidConfigurations
        count *= numberOfValidConfigurations
    }

    return count
}

fun solvePuzzle1(input: List<String>) {
    val sortedInput = sortIntoHashSet(input)
    val gapCounts = countGaps(sortedInput)
    println(gapCounts.first * gapCounts.third)
}

fun solvePuzzle2(input: List<String>) {
    val sortedInput = sortIntoHashSet(input)
    val adapterChunks = splitAtRequiredAdapters(sortedInput)
    // Any chunk with less than 2 gaps (i.e. less than 3 adapters) has only 1 valid configuration
    // so it doesn't matter.
    val gapLists = adapterChunks.map { convertToGapList(it) }.filter { it.size > 1 }
    println(gapLists)
}
