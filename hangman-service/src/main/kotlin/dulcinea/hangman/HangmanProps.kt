package dulcinea.hangman

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.stereotype.Component

@ConstructorBinding
@ConfigurationProperties("hangman")
class HangmanProps(val letters: Int, val file: String, val depth: Int) {}