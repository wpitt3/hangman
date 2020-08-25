package dulcinea.hangman

interface WordService {
    fun setup()
    fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>) : SearchOption
}