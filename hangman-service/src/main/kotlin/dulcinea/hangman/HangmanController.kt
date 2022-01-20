package dulcinea.hangman

import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/game")
class HangmanController(val hangmanService: HangmanService) {

    var handlingRequest = false //hack until BE is made stateless

    @CrossOrigin(origins = ["http://localhost:3000"])
    @GetMapping
    fun getStatus() : GameStatus {
        return hangmanService.getStatus()
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PutMapping("/{letter}")
    fun makeGuess(@PathVariable letter: String) : GameStatus {
        if (!handlingRequest) {
            handlingRequest = true
            val result = hangmanService.makeGuess(letter.toUpperCase())
            handlingRequest = false
            return result
        } else {
            return hangmanService.getStatus()
        }
    }

    @CrossOrigin(origins = ["http://localhost:3000"])
    @PostMapping("/{wordLength}")
    fun start(@PathVariable wordLength: String) : String {

        hangmanService.newGame(wordLength.toInt())
        return "New game started"
    }

}




