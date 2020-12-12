package day12

import kotlin.math.abs

enum class Direction {
    N,
    E,
    S,
    W,
    ;

    private fun toDegrees(): Int {
        return when (this) {
            N -> 0
            E -> 90
            S -> 180
            W -> 270
        }
    }

    private fun fromDegrees(degrees: Int): Direction {
        // Have to add 360 to get rid of negative results
        return when ((degrees + 360) % 360) {
            0 -> N
            90 -> E
            180 -> S
            270 -> W
            else -> throw IllegalArgumentException("Cannot only convert from 0, 90, 180, or 270")
        }
    }

    fun turn(rotationDir: Char, degrees: Int): Direction {
        val direction = if (rotationDir == 'L') -1 else 1
        return fromDegrees(toDegrees() + direction * degrees)
    }
}

class NavigationSystem {
    private var currentDirection: Direction = Direction.E

    private var latitude = 0
    private var longitude = 0

    fun getManhattanDistance(): Int {
        return abs(latitude) + abs(longitude)
    }

    fun move(direction: Direction, distance: Int) {
        when (direction) {
            Direction.N -> latitude += distance
            Direction.E -> longitude += distance
            Direction.S -> latitude -= distance
            Direction.W -> longitude -= distance
        }
    }

    private fun moveForward(distance: Int) {
        move(currentDirection, distance)
    }

    private fun turn(rotationDir: Char, degrees: Int) {
        currentDirection = currentDirection.turn(rotationDir, degrees)
    }

    fun processInstruction(instruction: String) {
        val quantity = instruction.drop(1).toInt()
        when (instruction[0]) {
            'N' -> move(Direction.N, quantity)
            'E' -> move(Direction.E, quantity)
            'S' -> move(Direction.S, quantity)
            'W' -> move(Direction.W, quantity)
            'L' -> turn(instruction[0], quantity)
            'R' -> turn(instruction[0], quantity)
            'F' -> moveForward(quantity)
        }
    }
}

class WaypointSystem {
    val navigationSystem = NavigationSystem()

    private var latitude = 1
    private var longitude = 10

    private fun move(direction: Direction, distance: Int) {
        when (direction) {
            Direction.N -> latitude += distance
            Direction.E -> longitude += distance
            Direction.S -> latitude -= distance
            Direction.W -> longitude -= distance
        }
    }

    private fun moveForward(quantity: Int) {
        navigationSystem.move(Direction.N, quantity * latitude)
        navigationSystem.move(Direction.E, quantity * longitude)
    }

    private fun rotate(rotationDir: Char, degrees: Int) {
        if (degrees == 0) return // ezpz
        if (degrees == 180) {
            latitude *= -1
            longitude *= -1
            return
        }
        if (degrees == 270) {
            val newDirection = if (rotationDir == 'L') 'R' else 'L'
            rotate(newDirection, 90)
            return
        }
        // Has to be 90 degree rotation
        if (rotationDir == 'L') {
            val temp = -latitude
            latitude = longitude
            longitude = temp
        }
        if (rotationDir == 'R') {
            val temp = -longitude
            longitude = latitude
            latitude = temp
        }
    }

    fun processInstruction(instruction: String) {
        val quantity = instruction.drop(1).toInt()
        when (instruction[0]) {
            'N' -> move(Direction.N, quantity)
            'E' -> move(Direction.E, quantity)
            'S' -> move(Direction.S, quantity)
            'W' -> move(Direction.W, quantity)
            'L' -> rotate(instruction[0], quantity % 360)
            'R' -> rotate(instruction[0], quantity % 360)
            'F' -> moveForward(quantity)
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val navigationSystem = NavigationSystem()

    for (instruction in input) {
        navigationSystem.processInstruction(instruction)
    }

    println(navigationSystem.getManhattanDistance())
}

fun solvePuzzle2(input: List<String>) {
    val waypointSystem = WaypointSystem()

    for (instruction in input) {
        waypointSystem.processInstruction(instruction)
    }

    println(waypointSystem.navigationSystem.getManhattanDistance())
}
