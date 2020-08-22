package dulcinea.hangman

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/ui")
class HangmanUiController(val hangmanService: HangmanService) {

    val aCharIndex: Int = 'A'.toInt()
    val maxLives = 10

    @GetMapping
    fun getUi(model: Model, @RequestParam action: String?) : String {
        if (action != null) {
            if (action == "new-game") {
                hangmanService.newGame()
            } else if (action.length == 1 && action[0].toInt() >= aCharIndex && action[0].toInt() < aCharIndex + 25) {
                hangmanService.makeGuess(action)
            }
        }

        val status = hangmanService.getStatus()
        addStatusToModel(model, status)
        return "game-ui"
    }

    private fun addStatusToModel(model: Model, status: GameStatus) {
        val state = status.state.toUpperCase().split("").filter{it != ""}
        val wrongGuesses = status.wrongGuesses.map{it.toUpperCase()}.toSet()
        val letters = (0..25).map{ (it + aCharIndex).toChar().toString() }
                .map{ Letter(it, !wrongGuesses.contains(it) && !state.contains(it))}

        model.addAttribute("state", state)
        model.addAttribute("letters", letters)
        model.addAttribute("lives", maxLives - wrongGuesses.size)
    }

}

data class Letter(val value: String, val active: Boolean)


