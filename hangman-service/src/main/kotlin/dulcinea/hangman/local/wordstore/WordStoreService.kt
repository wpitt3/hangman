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

        val keyScore = findMax(letter, SearchKey(with, without), allMatchingWords, 1, Integer.MAX_VALUE, Int.MIN_VALUE)

        return SearchOption(keyScore.searchKey.with, keyScore.searchKey.without)
    }

    fun findMax(letter: Char, searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int ): SearchKeyScore {
        if (depth <= 0) {
            return SearchKeyScore(searchKey, words.size)
        } else {
            val guesses: Map<SearchKey, List<Word>> = wordCache.makeGuess(letter, searchKey.with, searchKey.without, words)
            var currentMax : SearchKeyScore? = null
            for (guess in guesses.keys.toMutableList().sortedBy { -guesses[it]!!.size }) {
                var x = findMin(guess, guesses[guess]!!, depth - 1, alpha, beta)
                if (currentMax == null || currentMax.score < x.score) {
                    currentMax = x
                }
            }
            return currentMax!!
        }
    }

    fun findMin(searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int ): SearchKeyScore {
        if (depth <= 0) {
            return SearchKeyScore(searchKey, words.size)
        } else {
            val untouchedLetters = lettersByFreq
                    .filter{ !searchKey.with.contains(it) }
                    .filter{ !searchKey.without.contains(it) }
                    .take(10)
            var currentMin : SearchKeyScore? = null
            for (letter in untouchedLetters) {
                var x = findMax(letter, searchKey, words, depth - 1, alpha, beta)
                if (currentMin == null || currentMin.score > x.score) {
                    currentMin = x
                }
            }
            return currentMin!!
        }
    }
}

data class SearchKeyScore(val searchKey: SearchKey, val score: Int) {

}
