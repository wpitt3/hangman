package dulcinea.hangman

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable

@Service
class HangmanService(val wordRepository: WordRepository) {

    val inputRegex = "^[a-zA-Z]$".toRegex()

    var with: MutableList<String> = (0..5).map{""}.toMutableList()
    var without: MutableList<String> = mutableListOf()

    fun newGame() {
        with = (0..5).map{""}.toMutableList()
        without = mutableListOf()
    }

    fun getStatus() : GameStatus {
        val state = with.map{ if (it=="") "_" else it}.joinToString("")
        return GameStatus(state, without)
    }

    fun makeGuess(@PathVariable letter: String) : GameStatus {
        if (letter.length != 1 || !inputRegex.matches(letter) || (with + without).contains(letter)){
            return getStatus()
        }
        val options = (0..5).map{ mapOf(Pair("with", (with.toMutableList() + (letter + it))), Pair("without", without.toList())) }.toMutableList()
        options.add(mapOf(Pair("with", with.toList()), Pair("without", without.toMutableList() + letter)))

        options.forEach {
            wordRepository.findAggregations(it["with"]!!, it["without"]!!)
        }

        return getStatus()
    }
}
