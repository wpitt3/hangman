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
        val result = wordCache.makeGuess(letter, with, without)
        println("")
        result.forEach{ k, v ->
            if (v.size > 0) {
                println(k.with.map { if (it == null) " " else it }.joinToString("") + " " + v.size + " " + v.take(10))
            }
        }
        val best = result.maxBy{ it.value.size }
        if (best == null || best.value.isEmpty()) {
            throw RuntimeException("need to implement more letters")
        }
        if (best.key.with == with) {
            return SearchOption(with, without + letter)
        }
        return SearchOption(best.key.with, without)
    }

}
