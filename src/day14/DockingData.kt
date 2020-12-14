package day14

import kotlin.math.pow

const val MAX_36_ULONG = 68719476735L

class Memory {
    // Maps bit positions to their values
    private var bitMask = mutableMapOf<Int, Int>()

    //
    private var floatingBitMask = mutableListOf<Int>()

    // Maps memory addresses to their values
    private val memory = mutableMapOf<Long, Long>()

    private fun setBitMask(mask: String) {
        val wordLength = 35

        bitMask = mutableMapOf()
        floatingBitMask = mutableListOf()

        for (i in 0..wordLength) {
            if (mask[i] == 'X') {
                floatingBitMask.add(35 - i)
            } else {
                bitMask[wordLength - i] = mask[i].toString().toInt()
            }
        }
    }

    private fun applyBitMaskToValue(value: Long): Long {
        var maskedValue = value

        for ((bitPosition, bitValue) in bitMask) {
            var singleBitMask = 2.toDouble().pow(bitPosition.toDouble()).toLong()
            if (bitValue == 1) {
                maskedValue = maskedValue or singleBitMask
            } else if (bitValue == 0) {
                singleBitMask = MAX_36_ULONG - singleBitMask
                maskedValue = maskedValue and singleBitMask
            }
        }

        return maskedValue
    }

    private fun applyBitMaskToValueV2(value: Long): Long {
        var maskedValue = value

        for ((bitPosition, bitValue) in bitMask) {
            if (bitValue == 1) {
                val singleBitMask = 2.toDouble().pow(bitPosition.toDouble()).toLong()
                maskedValue = maskedValue or singleBitMask
            }
        }

        return maskedValue
    }

    private fun writeValue(position: Long, value: Long) {
        memory[position] = applyBitMaskToValue(value)
    }

    private fun writeValueV2(position: Long, value: Long) {
        var writtenPosition = applyBitMaskToValueV2(position)
        val positionsToWrite = mutableSetOf(writtenPosition)

        for (floaterPosition in floatingBitMask) {
            val highBitMask = 2.toDouble().pow(floaterPosition.toDouble()).toLong()
            val lowBitMask = MAX_36_ULONG - highBitMask
            val newPositions = mutableSetOf<Long>()
            for (pos in positionsToWrite) {
                newPositions.add(pos or highBitMask)
                newPositions.add(pos and lowBitMask)
            }
            positionsToWrite.addAll(newPositions)
        }

        for (pos in positionsToWrite) {
            memory[pos] = value
        }
    }

    fun processInstruction(instruction: String) {
        val setMaskPrefix = "mask = "
        val setMemPrefix = "mem["
        if (instruction.startsWith(setMaskPrefix)) {
            setBitMask(instruction.removePrefix(setMaskPrefix))
        } else if (instruction.startsWith(setMemPrefix)) {
            val setMemSplitter = "] = "
            val args = instruction.removePrefix(setMemPrefix).split(setMemSplitter)
            writeValue(args[0].toLong(), args[1].toLong())
        }
    }

    fun processInstructionV2(instruction: String) {
        val setMaskPrefix = "mask = "
        val setMemPrefix = "mem["
        if (instruction.startsWith(setMaskPrefix)) {
            setBitMask(instruction.removePrefix(setMaskPrefix))
        } else if (instruction.startsWith(setMemPrefix)) {
            val setMemSplitter = "] = "
            val args = instruction.removePrefix(setMemPrefix).split(setMemSplitter)
            writeValueV2(args[0].toLong(), args[1].toLong())
        }
    }

    fun dumpMemorySum(): Long {
        return memory.values.sum()
    }
}

fun solvePuzzle1(input: List<String>) {
    val memory = Memory()

    for (instruction in input) {
        memory.processInstruction(instruction)
    }

    println(memory.dumpMemorySum())
}

fun solvePuzzle2(input: List<String>) {
    val memory = Memory()

    for (instruction in input) {
        memory.processInstructionV2(instruction)
    }

    println(memory.dumpMemorySum())
}
