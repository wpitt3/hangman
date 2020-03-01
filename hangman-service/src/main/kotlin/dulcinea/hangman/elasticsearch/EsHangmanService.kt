package dulcinea.hangman.elasticsearch

import dulcinea.hangman.*
import org.springframework.stereotype.Service
import java.io.File

@Service
class EsHangmanService(val wordRepository: EsWordRepository, val resultAnalyser: EsResultAnalyser, val hangmanProps: HangmanProps) {
    var status: PersistenceStatus = PersistenceStatus.DOWN
    val wordLength = hangmanProps.letters
    val file = hangmanProps.file

    fun setup() {
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
        val options = (0..(wordLength-1)).filter{with[it] == ""}
                .map{ SearchOption(listOf(letter + it), listOf()) }
                .toMutableList() + SearchOption(listOf(), listOf(letter))

        val searchOption = options.map {
            val result = wordRepository.findAggregations((indexedLetters(with) + it.with), (without + it.without))
            println(result.words)
            Pair(resultAnalyser.score(result, it.with + it.without), it)
        }.sortedBy { -it.first }.first().second
        println(searchOption)
        return searchOption
    }

    private fun indexedLetters(with: List<String>): MutableList<String> {
        return (0..(wordLength-1)).filter{with[it] != ""}.map{"${with[it]}${it}"}.toMutableList()
    }

    fun populateRepo() {
        val words: List<String> = File(file).readLines() //TODO configurable
        words.forEach {
            wordRepository.create(Word(it))
        }
    }

}

enum class PersistenceStatus{ DOWN, INITIALISEING, READY}
