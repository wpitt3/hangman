package dulcinea.hangman

import dulcinea.hangman.elasticsearch.EsHangmanService
import org.springframework.stereotype.Service

@Service
class HangmanService(val esHangmanService: EsHangmanService) {
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

        val searchOption = esHangmanService.makeGuess(letter, with, without)
        updateStateWithOption(searchOption)
        return getStatus()
    }

    private fun updateStateWithOption(searchOption: SearchOption) {
        without.addAll(searchOption.without)
        searchOption.with.forEach{
            with[it[1].toString().toInt()] = it[0].toString()
        }
    }
}

data class SearchOption(val with: List<String>, val without: List<String>)
