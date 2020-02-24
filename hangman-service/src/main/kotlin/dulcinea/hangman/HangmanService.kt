package dulcinea.hangman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HangmanService

fun main(args: Array<String>) {
	runApplication<HangmanService>(*args)
}
