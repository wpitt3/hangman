package dulcinea.hangman.local.wordstore

import dulcinea.hangman.HangmanProps
import dulcinea.hangman.SearchOption
import dulcinea.hangman.WordService
import org.springframework.stereotype.Service
import java.io.File
import java.lang.RuntimeException

@Service
class WordStoreService(val hangmanProps: HangmanProps): WordService {
    val wordLength = hangmanProps.letters
    val file = hangmanProps.file
    lateinit var wordCache: WordCache

    override fun setup() {
        val words: List<String> = File(file).readLines() //TODO configurable
        wordCache = WordCache(words)
    }

    override fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>): SearchOption {
        val result = wordCache.makeGuess(letter, with, without)
        println("")
        result.forEach{ k, v ->
            if (v.size > 0) {
                println(k.map { if (it == null) " " else it }.joinToString("") + " " + v.size + " " + v.take(10))
            }
        }
        val best = result.maxBy{ it.value.size }
        if (best == null || best.value.isEmpty()) {
            throw RuntimeException("need to implement more letters")
        }
        if (best.key == with) {
            return SearchOption(with, without + letter)
        }
        return SearchOption(best.key, without)
    }
}
