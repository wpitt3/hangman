package dulcinea.hangman

interface WordService {
    fun setup()
    fun makeGuess(letter: String, with: List<String>, without: List<String>) : SearchOption
}