package dulcinea.hangman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class HangmanApplication

fun main(args: Array<String>) {
	runApplication<HangmanApplication>(*args)
}
