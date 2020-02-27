package dulcinea.hangman

import org.springframework.stereotype.Service

@Service
class HangmanService(val wordRepository: WordRepository, val resultAnalyser: ResultAnalyser) {
    private final val INPUT_REGEX = "^[A-Z]$".toRegex()

    var with: MutableList<String> = (0..5).map{""}.toMutableList()
    var without: MutableList<String> = mutableListOf()

    fun newGame() {
        with = (0..5).map{""}.toMutableList()
        without = mutableListOf()
    }

    fun getStatus() : GameStatus {
        val state = with.map{if (it=="") "_" else it}.joinToString("")
        return GameStatus(state, without)
    }

    fun makeGuess(letter: String) : GameStatus {
        val upperCaseLetter = letter.toUpperCase()
        if (upperCaseLetter.length != 1 || !INPUT_REGEX.matches(upperCaseLetter) || (with + without).contains(upperCaseLetter)){
            return getStatus()
        }
        val options = (0..5).filter{with[it] == ""}
                .map{ SearchOption(listOf(upperCaseLetter + it), listOf()) }
                .toMutableList() + SearchOption(listOf(), listOf(upperCaseLetter))

        val searchOption = options.map {
            val result = wordRepository.findAggregations((indexedLetters() + it.with), (without + it.without))
            Pair(resultAnalyser.score(result), it)
        }.sortedBy { -it.first }.first().second

        updateStateWithOption(searchOption)

        return getStatus()
    }

    private fun updateStateWithOption(searchOption: SearchOption) {
        without.addAll(searchOption.without)
        searchOption.with.forEach{
            with[it[1].toString().toInt()] = it[0].toString()
        }
    }

    private fun indexedLetters(): MutableList<String> {
        return (0..5).filter{with[it] != ""}.map{"${with[it]}${it}"}.toMutableList()
    }
}

data class SearchOption(val with: List<String>, val without: List<String>)
