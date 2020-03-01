package dulcinea.hangman

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class HangmanController(val hangmanService: HangmanService) {

    @GetMapping
    fun getStatus() : GameStatus {
        return hangmanService.getStatus()
    }

    @PutMapping("/{letter}")
    fun makeGuess(@PathVariable letter: String) : GameStatus {
        return hangmanService.makeGuess(letter)
    }

    @PostMapping
    fun start() : String {
        hangmanService.newGame()
        return "New game started"
    }
}

