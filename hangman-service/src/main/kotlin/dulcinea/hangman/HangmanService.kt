package dulcinea.hangman

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

@Service
class HangmanService(@Qualifier("wordStoreService") val wordService: WordService, hangmanProps: HangmanProps) {
    private final val INPUT_REGEX = "^[A-Z]$".toRegex()
    var wordLength = hangmanProps.letters

    var with: MutableList<Char?> = (1..wordLength).map{null}.toMutableList()
    var without: MutableSet<Char> = mutableSetOf()
    var exampleWords: List<String> = listOf()

    init {
        Thread {wordService.setup()}.start()
    }

    fun newGame(wordLength: Int = 6) {
        if (wordLength >=5 && wordLength <= 7) {
            wordService.setLength(wordLength)
            this.wordLength = wordLength
        }
        with = (1..this.wordLength).map { null }.toMutableList()
        without = mutableSetOf()
    }

    fun getStatus() : GameStatus {
        val state = with.map{if (it == null) "_" else it}.joinToString("")
        return GameStatus(state, without.map{it.toString()}, exampleWords)
    }

    fun makeGuess(letter: String) : GameStatus {
        val upperCaseLetter = letter.toUpperCase()
        if (upperCaseLetter.length != 1 || !INPUT_REGEX.matches(upperCaseLetter) || (with.filter{it != null}.toList() + without).contains(upperCaseLetter[0])){
            return getStatus()
        }

        val searchOption = wordService.makeGuess(letter[0], with, without)
        updateStateWithOption(searchOption)
        return getStatus()
    }

    private fun updateStateWithOption(searchOption: SearchOption) {
        without.addAll(searchOption.without)
        searchOption.with.indices.forEach{
            with[it] = searchOption.with[it]
        }
        exampleWords = searchOption.words
    }
}

data class SearchOption(val with: List<Char?>, val without: Set<Char>, val words: List<String>)
data class GameStatus(val state: String, val wrongGuesses: List<String>, val exampleWords: List<String>)
