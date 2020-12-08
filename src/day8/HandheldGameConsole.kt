package day8

class HandheldGameConsole {
    private var accumulator = 0
    private var instructionPointer = 0

    private var instructionsSeen = mutableSetOf<Int>()

    fun reset() {
        accumulator = 0
        instructionPointer = 0
        instructionsSeen = mutableSetOf()
    }

    private fun nop() {
        instructionPointer += 1
    }

    private fun acc(arg: Int) {
        accumulator += arg
        instructionPointer += 1
    }

    private fun jmp(arg: Int) {
        instructionPointer += arg
    }

    fun runProgramUntilLoop(input: List<String>) : Int {
        val instructionRegex = Regex("^([a-z]{3}) ([+-]\\d+)$")

        reset()

        while (true) {
            if (instructionsSeen.contains(instructionPointer) ||
                    instructionPointer >= input.size) break
            instructionsSeen.add(instructionPointer)

            val currentInput = input[instructionPointer]
            val instructionMatch = instructionRegex.find(currentInput) ?:
                throw Exception("Could not parse instruction from: $currentInput")
            val instruction = instructionMatch.groupValues[1]
            val argument = instructionMatch.groupValues[2].toInt()

            when (instruction) {
                "nop" -> nop()
                "acc" -> acc(argument)
                "jmp" -> jmp(argument)
            }
        }

        return accumulator
    }

    fun findCorruptedInstruction(input: List<String>) : Int {
        runProgramUntilLoop(input)
        // There's a small chance we short-circuit here
        if (instructionPointer > input.size) return accumulator

        /* The only instructions that can be swapped to affect the program's operation
         * are ones that are executed in a "normal" run and only jmps and nops can be
         * corrupted.
         */
        val corruptibleInstructionsRegex = Regex("nop|jmp")
        val instructionsInNormalRun = instructionsSeen
        val possiblyCorruptedInstructions = instructionsInNormalRun.filter {
            input[it].contains(corruptibleInstructionsRegex)
        }

        for (possibleCorruption in possiblyCorruptedInstructions) {
            val newInput = input.toMutableList()
            when {
                newInput[possibleCorruption].contains("nop") -> {
                    newInput[possibleCorruption] = newInput[possibleCorruption].replace("nop", "jmp")
                }
                newInput[possibleCorruption].contains("jmp") -> {
                    newInput[possibleCorruption] = newInput[possibleCorruption].replace("jmp", "nop")
                }
                else -> {
                    throw Exception("Incorruptible instruction flagged as corruptible: $possibleCorruption")
                }
            }
            runProgramUntilLoop(newInput)
            if (instructionPointer >= input.size) break
        }

        return accumulator
    }
}

fun solvePuzzle1(input: List<String>) {
    val gameConsole = HandheldGameConsole()
    println(gameConsole.runProgramUntilLoop(input))
}

fun solvePuzzle2(input: List<String>) {
    val gameConsole = HandheldGameConsole()
    println(gameConsole.findCorruptedInstruction(input))
}