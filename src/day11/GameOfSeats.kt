package day11

enum class Direction {
    N,
    NE,
    E,
    SE,
    S,
    SW,
    W,
    NW,
    ;

    fun opposite(): Direction {
        return when (this) {
            N -> S
            NE -> SW
            E -> W
            SE -> NW
            S -> N
            SW -> NE
            W -> E
            NW -> SE
        }
    }
}

class Seat(startState: State) {
    enum class State {
        FLOOR,
        EMPTY,
        OCCUPIED,
        ;

        override fun toString(): String {
            return when (this) {
                FLOOR -> "."
                EMPTY -> "L"
                OCCUPIED -> "#"
            }
        }
    }

    var state: State = startState
        private set

    private var nextState: State? = null
    private val neighbours = mutableMapOf<Direction, Seat>()

    fun addNeighbour(newNeighbour: Seat, direction: Direction) {
        neighbours[direction] = newNeighbour
        newNeighbour.addNeighbourBack(this, direction)
    }

    private fun addNeighbourBack(newNeighbour: Seat, direction: Direction) {
        neighbours[direction.opposite()] = newNeighbour
    }

    private fun occupiedSeatInDirection(direction: Direction): Boolean {
        val nextSeat = neighbours[direction] ?: return false
        return when (nextSeat.state) {
            State.OCCUPIED -> true
            State.EMPTY -> false
            State.FLOOR -> nextSeat.occupiedSeatInDirection(direction)
        }
    }

    // Returns true if the next state is different from the current state
    fun calculateNextState(): Boolean {
        nextState = state
        if (state == State.FLOOR) return false

        val occupiedNeighbours = neighbours.count { it.value.state == State.OCCUPIED }
        if (state == State.EMPTY && occupiedNeighbours == 0) {
            nextState = State.OCCUPIED
            return true
        } else if (state == State.OCCUPIED && occupiedNeighbours >= 4) {
            nextState = State.EMPTY
            return true
        }
        return false
    }

    fun calculateNextStateFromVisible(): Boolean {
        nextState = state
        if (state == State.FLOOR) return false

        val occupiedLines = neighbours.count { this.occupiedSeatInDirection(it.key) }
        if (state == State.EMPTY && occupiedLines == 0) {
            nextState = State.OCCUPIED
            return true
        } else if (state == State.OCCUPIED && occupiedLines >= 5) {
            nextState = State.EMPTY
            return true
        }
        return false
    }

    fun updateState() {
        state = nextState
                ?: throw IllegalStateException("must call calculateNextState before each call to updateState")
        nextState = null
    }
}

fun setupSeats(input: List<String>): List<List<Seat>> {
    var seats = mutableListOf<List<Seat>>()

    input.forEachIndexed { i, row ->
        var rowOfSeats = mutableListOf<Seat>()
        row.forEachIndexed { j, c ->
            var state = when (c) {
                '.' -> Seat.State.FLOOR
                'L' -> Seat.State.EMPTY
                '#' -> Seat.State.OCCUPIED
                else -> throw IllegalArgumentException("Invalid seat: $c")
            }
            val newSeat = Seat(state)
            rowOfSeats.add(newSeat)

            /* Polite seats introduce themselves only to neighbours who directly west of them or farther are north.
             * Neighbours who are directly east or are farther south will introduce themselves to you.
             * (This is because seats are created from NW corner going across each row before moving south).
             */

            // Add northern neighbours if they exist
            if (i > 0) {
                val previousRow = seats[i - 1]

                // Only add NW neighbour if they exist
                if (j > 0) previousRow[j - 1].addNeighbour(newSeat, Direction.NW)

                previousRow[j].addNeighbour(newSeat, Direction.N)

                // Only add NE neighbour if they exist
                if (j < row.lastIndex) previousRow[j + 1].addNeighbour(newSeat, Direction.NE)
            }
            // Add Western neighbour if they exist
            if (j > 0) rowOfSeats[j - 1].addNeighbour(newSeat, Direction.W)
        }
        seats.add(rowOfSeats)
    }

    return seats
}

fun simulateToStability(seats: List<List<Seat>>, calculateNextStateFn: Seat.() -> Boolean) {
    var stateChanged = true
    while (stateChanged) {
        stateChanged = seats.fold(false) { seatsChanged, rowOfSeats ->
            rowOfSeats.fold(false) { acc, seat ->
                seat.calculateNextStateFn() || acc
            } || seatsChanged
        }
        seats.forEach { rowOfSeats ->
            rowOfSeats.forEach {
                it.updateState()
            }
        }
    }
}

fun solvePuzzle1(input: List<String>) {
    val seats = setupSeats(input)
    simulateToStability(seats, Seat::calculateNextState)
    println(seats.sumBy { it.count { seat -> seat.state == Seat.State.OCCUPIED } })
}

fun solvePuzzle2(input: List<String>) {
    val seats = setupSeats(input)
    simulateToStability(seats, Seat::calculateNextStateFromVisible)
    println(seats.sumBy { it.count { seat -> seat.state == Seat.State.OCCUPIED } })
}
