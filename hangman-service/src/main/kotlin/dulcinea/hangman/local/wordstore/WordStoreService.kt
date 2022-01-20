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
    var wordLength = hangmanProps.letters
    val file = hangmanProps.file
    val depth = hangmanProps.depth
    lateinit var wordCaches: Map<Int, WordCache>
    lateinit var lettersByFreq: Map<Int, List<Char>>

    override fun setup() {
        val wordCaches = mutableMapOf<Int, WordCache>()
        val lettersByFreq = mutableMapOf<Int, List<Char>>()
        listOf(5, 6, 7).forEach { wordLength ->
            val words: List<String> = File("src/main/resources/" + wordLength + "_letter_words.txt").readLines()
            wordCaches[wordLength] = WordCache(words)
            val letterFreqTable: MutableMap<Char, Int> = (0..25).map { (it + aCharIndex).toChar() to 0 }.toMap().toMutableMap()
            words.forEach { word -> word.toUpperCase().forEach { letterFreqTable[it] = letterFreqTable[it]!! + 1 } }
            lettersByFreq[wordLength] = letterFreqTable.toList().sortedBy { -it.second }.map { it.first }
            this.wordCaches = wordCaches.toMap()
            this.lettersByFreq = lettersByFreq.toMap()
        }
    }

    override fun setLength(length: Int){
        if (length <= 7 && length >= 5) {
            wordLength = length
        }
    }

    override fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>): SearchOption {
        val allMatchingWords = wordCaches[wordLength]!!.allMatchingWords(with, without)

        val keyScore = findMax(letter, SearchKey(with, without), allMatchingWords, depth, Integer.MAX_VALUE, Int.MIN_VALUE)

        val resolvedKey = resolveMultiDepthMove(letter, SearchKey(with, without), keyScore.first)
        print(resolvedKey)
        return SearchOption(resolvedKey!!.with, resolvedKey!!.without, keyScore.second.take(20).map{it.toString()})
    }

    fun resolveMultiDepthMove(letter: Char, searchKey: SearchKey, max: SearchKeyScore): SearchKey? {
        for (i in max.searchKey.with.indices) {
            if (letter == max.searchKey.with[i]) {
                val newWith = searchKey.with.addLetters(letter, i)
                return SearchKey(newWith.toList(), searchKey.without)
            }
        }
        if (max.searchKey.without.contains(letter)) {
            return SearchKey(searchKey.with, searchKey.without + letter)
        }
        return null
    }

    private fun List<Char?>.addLetters(letter: Char, vararg indices: Int) : List<Char?> {
        return indices.fold(this.toMutableList(), {acc, i ->  acc[i] = letter; acc})
    }

    fun findMax(letter: Char, searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int): Pair<SearchKeyScore, List<Word>> {
        if (depth <= 0) {
            return Pair(SearchKeyScore(searchKey, words.size), words)
        } else {
            val guesses: Map<SearchKey, List<Word>> = wordCaches[wordLength]!!.makeGuess(letter, searchKey.with, searchKey.without, words)
            var currentMax : SearchKeyScore? = null
            for (guess in guesses.keys.toMutableList().sortedBy { -guesses[it]!!.size }) {
                var x = findMin(guess, guesses[guess]!!, depth - 1, alpha, beta)
                if (currentMax == null || currentMax.score < x.score) {
                    currentMax = x
                }
            }
            if (currentMax == null || currentMax!!.searchKey == null ||  guesses[currentMax!!.searchKey] == null) {
                return Pair(currentMax!!, listOf())
            }
            return Pair(currentMax!!, guesses[currentMax!!.searchKey]!!)
        }
    }

    fun findMin(searchKey: SearchKey, words: List<Word>, depth: Int, alpha: Int, beta: Int ): SearchKeyScore {
        if (depth <= 0) {
            return SearchKeyScore(searchKey, words.size)
        } else {
            val untouchedLetters = lettersByFreq[wordLength]!!
                    .filter{ !searchKey.with.contains(it) }
                    .filter{ !searchKey.without.contains(it) }
                    .take(10)
            var currentMin : SearchKeyScore? = null
            for (letter in untouchedLetters) {
                var x = findMax(letter, searchKey, words, depth - 1, alpha, beta)
                if (currentMin == null || currentMin.score > x.first.score) {
                    currentMin = x.first
                }
            }
            return currentMin!!
        }
    }
}

data class SearchKeyScore(val searchKey: SearchKey, val score: Int) {

}
