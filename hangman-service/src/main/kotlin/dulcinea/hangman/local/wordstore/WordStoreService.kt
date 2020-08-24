package dulcinea.hangman.local.wordstore

import dulcinea.hangman.HangmanProps
import dulcinea.hangman.SearchOption
import dulcinea.hangman.WordService
import org.springframework.stereotype.Service
import java.io.File

@Service
class WordStoreService(val hangmanProps: HangmanProps): WordService{
    val wordLength = hangmanProps.letters
    val file = hangmanProps.file
    lateinit var wordCache: WordCache

    override fun setup() {
        val words: List<String> = File(file).readLines() //TODO configurable
        wordCache = WordCache(words)
    }

    override fun makeGuess(letter: String, with: List<String>, without: List<String>): SearchOption {
        val result = wordCache.makeGuess(letter[0], with.map{ if (it == "") null else it[0]}, without.map{it[0]}.toSet())

        (0..6).forEach{
            println(result[it].size.toString() + " " + result[it].take(10))
        }
        val bestIndex = result.indexOf(result.maxBy { it.size })
        if (bestIndex == wordLength) {
            return SearchOption(with, without + letter)
        }
        val newWith = with.toMutableList()
        newWith[bestIndex] = letter
        return SearchOption(newWith, without)
    }
}
