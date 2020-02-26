package dulcinea.hangman

import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable


@Service
class HangmanService {

    fun getStatus() : GameStatus {
        return GameStatus("___A__", listOf("E"))
    }

    fun makeGuess(@PathVariable letter: String) : GameStatus {
        return GameStatus("___R__", listOf("F"))
    }
}
