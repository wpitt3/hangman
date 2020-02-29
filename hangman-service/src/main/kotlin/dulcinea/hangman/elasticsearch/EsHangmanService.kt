package dulcinea.hangman.elasticsearch

import dulcinea.hangman.*
import org.springframework.stereotype.Service
import java.io.File

@Service
class EsHangmanService(val wordRepository: EsWordRepository, val resultAnalyser: EsResultAnalyser) {
    var status: PersistenceStatus = PersistenceStatus.DOWN

    init {
        Thread {setup(wordRepository)}.start()
    }

    private fun setup(wordRepository: EsWordRepository) {
        var words: List<Word> = listOf()
        try {
            words = wordRepository.get()
        } catch (e: Exception) {
            println("Setting up index")
            wordRepository.setupIndex()
        }
        status = PersistenceStatus.INITIALISEING
        if (words.isEmpty()) {
            println("Indexing words")
           populateRepo()
            println("Initialised")
        }
        status = PersistenceStatus.READY
    }

    fun makeGuess(letter: String, with: List<String>, without: List<String>) : SearchOption {
        val options = (0..5).filter{with[it] == ""}
                .map{ SearchOption(listOf(letter + it), listOf()) }
                .toMutableList() + SearchOption(listOf(), listOf(letter))

        val searchOption = options.map {
            val result = wordRepository.findAggregations((indexedLetters(with) + it.with), (without + it.without))
            Pair(resultAnalyser.score(result, it.with + it.without), it)
        }.sortedBy { println(it); -it.first }.first().second

        return searchOption
    }

    private fun indexedLetters(with: List<String>): MutableList<String> {
        return (0..5).filter{with[it] != ""}.map{"${with[it]}${it}"}.toMutableList()
    }

    fun populateRepo() {
        val words: List<String> = File("src/main/resources/six_letter_words.txt").readLines() //TODO configurable
        words.forEach {
            wordRepository.create(Word(it))
        }
    }

}

enum class PersistenceStatus{ DOWN, INITIALISEING, READY}