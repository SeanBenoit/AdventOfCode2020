package day22

import java.util.*

private var maxGameNumber = 0
private var gamesSeen = mutableMapOf<Pair<List<Int>, List<Int>>, String>()

class CombatCrab(val playerName: String) {
    private val deck = mutableListOf<Int>()

    fun buildDeck(deckList: List<String>): Int {
        require(deck.size == 0) { "deck has already been built" }

        for (line in deckList) {
            if (line.isEmpty()) break
            deck.add(line.toInt())
        }

        return deck.size
    }

    fun buildDeck(deckList: List<Int>) {
        require(deck.size == 0) { "deck has already been built" }

        for (card in deckList) {
            deck.add(card)
        }
    }

    fun getDeck(): IntArray {
        return deck.toIntArray()
    }

    override fun toString(): String {
        return "$playerName's deck: ${deck.joinToString(", ")}"
    }

    fun drawCard(): Int {
        return deck.removeAt(0)
    }

    fun addCard(newCard: Int) {
        deck.add(newCard)
    }

    fun hasCards(): Boolean {
        return deck.isNotEmpty()
    }

    fun scoreDeck(): Long {
        return deck.foldIndexed(0L) { i, score, card ->
            score + card * (deck.size - i)
        }
    }
}

fun listsAreTheSame(a: List<Int>, b: List<Int>): Boolean {
    if (a.size != b.size) return false
    a.forEachIndexed { i, value ->
        if (value != b[i]) return false
    }

    return true
}

fun playRound(player1: CombatCrab, player2: CombatCrab, noisy: Boolean) {
    if (noisy) {
        println(player1)
        println(player2)
    }
    val player1Card = player1.drawCard()
    val player2Card = player2.drawCard()
    if (noisy) {
        println("Player 1 plays: $player1Card")
        println("Player 2 plays: $player2Card")
    }
    if (player1Card > player2Card) {
        if (noisy) println("Player 1 wins the round!")
        player1.addCard(player1Card)
        player1.addCard(player2Card)
    } else {
        if (noisy) println("Player 2 wins the round!")
        player2.addCard(player2Card)
        player2.addCard(player1Card)
    }
}

fun playGame(player1: CombatCrab, player2: CombatCrab, noisy: Boolean): CombatCrab {
    var i = 0
    while (player1.hasCards() && player2.hasCards()) {
        i++
        if (noisy) println("-- Round $i --")

        playRound(player1, player2, noisy)
        if (noisy) println()
    }
    if (player1.hasCards()) return player1
    return player2
}

fun playRecursiveRound(player1: CombatCrab, player2: CombatCrab, noisy: Boolean, gameNumber: Int): CombatCrab {
    val player1Card = player1.drawCard()
    val player2Card = player2.drawCard()
    if (noisy) {
        println("Player 1 plays: $player1Card")
        println("Player 2 plays: $player2Card")
    }
    if (player1Card > player1.getDeck().size || player2Card > player2.getDeck().size) {
        if (player1Card > player2Card) {
            player1.addCard(player1Card)
            player1.addCard(player2Card)
            return player1
        }
        player2.addCard(player2Card)
        player2.addCard(player1Card)
        return player2
    }
    // Both players can recurse
    val player1SubDeck = player1.getDeck().take(player1Card)
    val subPlayer1 = CombatCrab(player1.playerName)
    subPlayer1.buildDeck(player1SubDeck)

    val player2SubDeck = player2.getDeck().take(player2Card)
    val subPlayer2 = CombatCrab(player2.playerName)
    subPlayer2.buildDeck(player2SubDeck)

    val subWinner = playRecursiveGame(subPlayer1, subPlayer2, noisy)

    if (noisy) println("\n...anyway, back to game $gameNumber.")

    if (subWinner.playerName == player1.playerName) {
        player1.addCard(player1Card)
        player1.addCard(player2Card)
        return player1
    }

    player2.addCard(player2Card)
    player2.addCard(player1Card)
    return player2
}

fun playRecursiveGame(player1: CombatCrab, player2: CombatCrab, noisy: Boolean): CombatCrab {
    maxGameNumber++

    val gameNumber = maxGameNumber
    if (noisy) println("=== Game $gameNumber ===\n\n")

    val positionsSeen = mutableSetOf<Pair<IntArray, IntArray>>()

    var i = 0
    while (player1.hasCards() && player2.hasCards()) {
        i++
        if (noisy) {
            println("-- Round $i (Game $gameNumber) --")
            println(player1)
            println(player2)
        }

        // Prevent infinite games
        if (positionsSeen.any {
                    it.first contentEquals player1.getDeck() && it.second contentEquals player2.getDeck()
                }
        ) {
            if (noisy) println("The winner of game $gameNumber is player 1!")
            return player1
        }
        positionsSeen.add(Pair(player1.getDeck(), player2.getDeck()))

        val roundWinner = playRecursiveRound(player1, player2, noisy, gameNumber)
        if (noisy) {
            print("${roundWinner.playerName} wins round $i of game $gameNumber!")
            println("")
        }
    }

    if (player1.hasCards()) return player1
    return player2
}

fun solvePuzzle1(input: List<String>) {
    val noisy = false
    val player1 = CombatCrab(input[0].removeSuffix(":"))
    val player1DeckSize = player1.buildDeck(input.drop(1))

    val player2Input = input.drop(2 + player1DeckSize)
    val player2 = CombatCrab(player2Input[0].removeSuffix(":"))
    player2.buildDeck(player2Input.drop(1))

    val winner = playGame(player1, player2, noisy)

    if (noisy) {
        println("== Post-game results ==")
        println(player1)
        println(player2)
    }

    println(winner.scoreDeck())
}

fun solvePuzzle2(input: List<String>) {
    val noisy = false
    val player1 = CombatCrab(input[0].removeSuffix(":"))
    val player1DeckSize = player1.buildDeck(input.drop(1))

    val player2Input = input.drop(2 + player1DeckSize)
    val player2 = CombatCrab(player2Input[0].removeSuffix(":"))
    player2.buildDeck(player2Input.drop(1))

    val winner = playRecursiveGame(player1, player2, noisy)

    if (noisy) {
        println("== Post-game results ==")
        println(player1)
        println(player2)
    }

    println(winner.scoreDeck())
}
