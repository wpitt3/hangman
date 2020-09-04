package dulcinea.hangman.local.wordstore

import dulcinea.hangman.HangmanProps
import dulcinea.hangman.SearchOption
import dulcinea.hangman.WordService
import org.springframework.stereotype.Service
import java.io.File
import java.lang.RuntimeException

@Service
class WordStoreService(val hangmanProps: HangmanProps): WordService {
    private val aCharIndex: Int = 'A'.toInt()
    val wordLength = hangmanProps.letters
    val file = hangmanProps.file
    lateinit var wordCache: WordCache
    lateinit var lettersByFreq: List<Char>

    override fun setup() {
        val words: List<String> = File(file).readLines() //TODO configurable
        wordCache = WordCache(words)
        val letterFreqTable: MutableMap<Char, Int> = (0..25).map{ (it + aCharIndex).toChar() to 0 }.toMap().toMutableMap()
        words.forEach{word -> word.toUpperCase().forEach { letterFreqTable[it] = letterFreqTable[it]!! + 1 }}
        lettersByFreq = letterFreqTable.toList().sortedBy { -it.second }.map{ it.first }
    }

    override fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>): SearchOption {

        val allMatchingWords = wordCache.allMatchingWords(with, without)

        val result: Map<SearchKey, List<Word>> = wordCache.makeGuess(letter, with, without)

        val x: Map<SearchKey, Map<Char, Map<SearchKey, List<Word>>>> = result.toList().sortedBy { -it.second.size }.map {
            println(it.second.size)
            it.first to makeGuess(it.first.with, it.first.without, it.second, 1)
        }.toMap()

        val best = result.maxBy{ it.value.size }
        if (best == null || best.value.isEmpty()) {
            throw RuntimeException("need to implement more letters")
        }

        if (best.key.with == with) {
            return SearchOption(with, without + letter)
        }
        return SearchOption(best.key.with, without)
    }

    fun findMax(letter: Char, searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int ): SearchKeyScore {
        val guesses: Map<SearchKey, List<Word>> = wordCache.makeGuess(letter, searchKey.with, searchKey.without, words)

        if (depth <= 0) {
            return SearchKeyScore(searchKey, 0)
        } else {
            for (guess in guesses.keys.toMutableList().sortedBy { -guesses[it]!!.size }) {
                findMin(guess, guesses[guess]!!, depth - 1, alpha, beta)
            }
        }

    }

    fun findMin(searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int ): SearchKeyScore {
        val untouchedLetters = lettersByFreq
                .filter{ !searchKey.with.contains(it) }
                .filter{ !searchKey.without.contains(it) }
                .take(10)
        if (depth <= 0) {
            return SearchKeyScore(searchKey, 0)
        } else {
            for (letter in untouchedLetters) {
                findMax(letter, searchKey, words, depth - 1, alpha, beta)
            }
        }
        return SearchKeyScore(searchKey, 0)
    }

    fun makeGuess(with: List<Char?>, without: Set<Char>, words: List<Word>, depth: Int): SearchKey {

        for (letter: Char in untouchedLetters) {
            val guesses: Map<SearchKey, List<Word>> = wordCache.makeGuess(letter, with, without, words)

            if (depth > 0) {
                for (guess in guesses.keys.toMutableList().sortedBy { -guesses[it]!!.size }) {
                    makeGuess(guess.with, guess.without, guesses[guess]!!, depth - 1)
                }
            } else {
                return wordCache.makeGuess(letter, with, without, words).maxBy { it.value.size }
            }
        }

        return untouchedLetters.map{ letter ->
            letter to wordCache.makeGuess(letter, with, without, words)
        }.toMap()
    }
}

data class SearchKeyScore(val searchKey: SearchKey, val score: Int) {

}
