package day17

import day17.ConwayCube.State.ACTIVE
import day17.ConwayCube.State.INACTIVE
import kotlin.math.max
import kotlin.math.min

class ConwayCube(startState: State) {
    enum class State {
        ACTIVE,
        INACTIVE,
        ;
    }

    var state = startState
        private set

    private var nextState: State? = null

    private val neighbours = mutableSetOf<ConwayCube>()

    fun addNeighbour(newNeighbour: ConwayCube) {
        if (this == newNeighbour) return
        neighbours.add(newNeighbour)
        newNeighbour.addNeighbourBack(this)
    }

    private fun addNeighbourBack(newNeighbour: ConwayCube) {
        if (this == newNeighbour) return
        neighbours.add(newNeighbour)
    }

    // Returns true if the next state is different from the current state
    fun calculateNextState(): Boolean {
        val occupiedNeighbours = neighbours.count { it.state == ACTIVE }

        return if (state == ACTIVE) {
            if (occupiedNeighbours != 2 && occupiedNeighbours != 3) {
                nextState = INACTIVE
                true
            } else {
                nextState = ACTIVE
                false
            }
        } else if (occupiedNeighbours == 3) {
            nextState = ACTIVE
            true
        } else {
            nextState = INACTIVE
            false
        }
    }

    fun updateState() {
        state = nextState
                ?: throw IllegalStateException("must call calculateNextState before each call to updateState")
        nextState = null
    }
}

fun parseCubeMap(input: List<String>): MutableList<MutableList<MutableList<ConwayCube>>> {
    val cubeSpace = mutableListOf(mutableListOf<MutableList<ConwayCube>>())

    input.forEachIndexed { i, row ->
        var rowOfCubes = mutableListOf<ConwayCube>()
        row.forEachIndexed { j, c ->
            var state = when (c) {
                '.' -> INACTIVE
                '#' -> ACTIVE
                else -> throw IllegalArgumentException("Invalid state: $c")
            }
            val newCube = ConwayCube(state)
            rowOfCubes.add(newCube)

            if (i > 0) {
                val previousRow = cubeSpace[0][i - 1]

                if (j > 0) previousRow[j - 1].addNeighbour(newCube)

                previousRow[j].addNeighbour(newCube)

                if (j < row.lastIndex) previousRow[j + 1].addNeighbour(newCube)
            }
            if (j > 0) rowOfCubes[j - 1].addNeighbour(newCube)
        }
        cubeSpace[0].add(rowOfCubes)
    }

    return cubeSpace
}

fun expandCubeSpace(cubeSpace: MutableList<MutableList<MutableList<ConwayCube>>>) {
    val initialLength = cubeSpace[0].size
    val initialWidth = cubeSpace[0][0].size

    // Add layer below and above existing space since it could now be affected
    cubeSpace.add(0, mutableListOf())
    cubeSpace.add(mutableListOf())
    for (y in 0 until initialLength) {
        cubeSpace[0].add(mutableListOf())
        cubeSpace.last().add(mutableListOf())
        for (x in 0 until initialWidth) {
            cubeSpace[0][y].add(ConwayCube(INACTIVE))
            cubeSpace.last()[y].add(ConwayCube(INACTIVE))
        }
    }
    val height = cubeSpace.size

    // Add layer to left and right of existing space
    for (z in 0 until height) {
        for (y in 0 until initialLength) {
            cubeSpace[z][y].add(0, ConwayCube(INACTIVE))
            cubeSpace[z][y].add(ConwayCube(INACTIVE))
        }
    }
    val width = cubeSpace[0][0].size

    // Add layer to front and back of existing space
    for (z in 0 until height) {
        cubeSpace[z].add(0, mutableListOf())
        cubeSpace[z].add(mutableListOf())
        for (x in 0 until width) {
            val newFrontCube = ConwayCube(INACTIVE)
            val newBackCube = ConwayCube(INACTIVE)

            cubeSpace[z][0].add(newFrontCube)
            cubeSpace[z].last().add(newBackCube)
        }
    }
    val length = cubeSpace[0].size

    // Connect new neighbours
    for (z in 0 until height) {
        for (y in 0 until length) {
            for (x in 0 until width) {
                if (!isOnOutside(z, y, x, height, length, width)) continue
                for (z0 in max(0, z - 1)..min(height - 1, z + 1)) {
                    for (y0 in max(0, y - 1)..min(length - 1, y + 1)) {
                        for (x0 in max(0, x - 1)..min(width - 1, x + 1)) {
                            cubeSpace[z][y][x].addNeighbour(cubeSpace[z0][y0][x0])
                        }
                    }
                }
            }
        }
    }
}

fun cycleCubeSpace(cubeSpace: MutableList<MutableList<MutableList<ConwayCube>>>) {
    expandCubeSpace(cubeSpace)

    for (layer in cubeSpace) {
        for (row in layer) {
            for (cube in row) {
                cube.calculateNextState()
            }
        }
    }

    for (layer in cubeSpace) {
        for (row in layer) {
            for (cube in row) {
                cube.updateState()
            }
        }
    }
}

fun parseHyperCubeMap(input: List<String>): MutableList<MutableList<MutableList<MutableList<ConwayCube>>>> {
    return mutableListOf(parseCubeMap(input))
}

fun makeInactiveCubeSpace(height: Int, length: Int, width: Int): MutableList<MutableList<MutableList<ConwayCube>>> {
    val newCubeSpace = mutableListOf<MutableList<MutableList<ConwayCube>>>()

    for (z in 0 until height) {
        newCubeSpace.add(mutableListOf())
        for (y in 0 until length) {
            newCubeSpace[z].add(mutableListOf())
            for (x in 0 until width) {
                newCubeSpace[z][y].add(ConwayCube(INACTIVE))
            }
        }
    }

    return newCubeSpace
}

fun expandHyperCubeSpace(hyperCubeSpace: MutableList<MutableList<MutableList<MutableList<ConwayCube>>>>) {
    for (cubeSpace in hyperCubeSpace) expandCubeSpace(cubeSpace)

    val height = hyperCubeSpace[0].size
    val length = hyperCubeSpace[0][0].size
    val width = hyperCubeSpace[0][0][0].size

    hyperCubeSpace.add(0, makeInactiveCubeSpace(height, length, width))
    hyperCubeSpace.add(makeInactiveCubeSpace(height, length, width))

    val shells = hyperCubeSpace.size

    // Connect new neighbours
    for (w in 0 until shells) {
        for (z in 0 until height) {
            for (y in 0 until length) {
                for (x in 0 until width) {
                    for (w0 in max(0, w - 1)..min(shells - 1, w + 1)) {
                        for (z0 in max(0, z - 1)..min(height - 1, z + 1)) {
                            for (y0 in max(0, y - 1)..min(length - 1, y + 1)) {
                                for (x0 in max(0, x - 1)..min(width - 1, x + 1)) {
                                    hyperCubeSpace[w][z][y][x].addNeighbour(hyperCubeSpace[w0][z0][y0][x0])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun cycleHyperCubeSpace(hyperCubeSpace: MutableList<MutableList<MutableList<MutableList<ConwayCube>>>>) {
    expandHyperCubeSpace(hyperCubeSpace)

    for (cubeSpace in hyperCubeSpace) {
        for (layer in cubeSpace) {
            for (row in layer) {
                for (cube in row) {
                    cube.calculateNextState()
                }
            }
        }
    }

    for (cubeSpace in hyperCubeSpace) {
        for (layer in cubeSpace) {
            for (row in layer) {
                for (cube in row) {
                    cube.updateState()
                }
            }
        }
    }
}

private fun isOnOutside(z: Int, y: Int, x: Int, height: Int, length: Int, width: Int): Boolean {
    if ((x == 0) or (x == width - 1)) return true
    if ((y == 0) or (y == length - 1)) return true
    if ((z == 0) or (z == height - 1)) return true
    return false
}

fun solvePuzzle1(input: List<String>) {
    val cubeSpace = parseCubeMap(input)

    for (i in 0 until 6) cycleCubeSpace(cubeSpace)

    println(cubeSpace.sumBy { layer -> layer.sumBy { row -> row.count { it.state == ACTIVE } } })
}

fun solvePuzzle2(input: List<String>) {
    val hyperCubeSpace = parseHyperCubeMap(input)

    for (i in 0 until 6) cycleHyperCubeSpace(hyperCubeSpace)

    println(hyperCubeSpace.sumBy { cubeSpace -> cubeSpace.sumBy { layer -> layer.sumBy { row -> row.count { it.state == ACTIVE } } } })
}
