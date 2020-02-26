package dulcinea.hangman

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/game")
class GameController(val hangmanService: HangmanService) {

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
        return "New game started"
    }
}

