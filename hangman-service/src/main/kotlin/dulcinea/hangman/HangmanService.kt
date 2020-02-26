package dulcinea.hangman

import org.springframework.stereotype.Service

@Service
class HangmanService(val wordRepository: WordRepository, val resultAnalyser: ResultAnalyser) {

    val inputRegex = "^[A-Z]$".toRegex()

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
        if (upperCaseLetter.length != 1 || !inputRegex.matches(upperCaseLetter) || (with + without).contains(upperCaseLetter)){
            return getStatus()
        }
        val options = (0..5).filter{with[it] == ""}
                .map{ SearchOption(listOf(upperCaseLetter + it), listOf()) }
                .toMutableList() + SearchOption(listOf(), listOf(upperCaseLetter))

        val searchOption = options.map {
            val result = wordRepository.findAggregations((indexedLetters() + it.with), (without + it.without))
            Pair(resultAnalyser.score(result), it)
        }//.sortedBy { it.first }.first().second


        return getStatus()
    }

    private fun indexedLetters(): MutableList<String> {
        return (0..5).filter{with[it] != ""}.map{"${with[it]}${it}"}.toMutableList()
    }
}

data class SearchOption(val with: List<String>, val without: List<String>)
