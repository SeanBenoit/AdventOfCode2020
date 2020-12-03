package day3

fun countTrees(treeMap: List<String>, run: Int, fall: Int): Int {
    return treeMap.asSequence().withIndex()
            .filter { it.index % fall == 0 }
            .map { it.value }
            .withIndex()
            .count { '#' == it.value[it.index * run % it.value.length] }
}

fun solvePuzzle1(input: List<String>) {
    val treeCount = countTrees(input, 3, 1)

    println(treeCount)
}

fun solvePuzzle2(input: List<String>) {
    val treeCounts = mutableListOf<Int>()
    treeCounts.add(countTrees(input, 1, 1))
    treeCounts.add(countTrees(input, 3, 1))
    treeCounts.add(countTrees(input, 5, 1))
    treeCounts.add(countTrees(input, 7, 1))
    treeCounts.add(countTrees(input, 1, 2))

    val totalTreeCount = treeCounts.fold(1L) { product, it -> product * it }

    println(totalTreeCount)
}
