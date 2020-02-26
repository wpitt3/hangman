package dulcinea.hangman

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(EsProps::class)
class HangmanApplication

fun main(args: Array<String>) {
	runApplication<HangmanApplication>(*args)
}
