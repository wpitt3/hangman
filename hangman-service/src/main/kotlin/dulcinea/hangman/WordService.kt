package dulcinea.hangman

interface WordService {

    fun setup()
    fun setLength(length: Int)
    fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>) : SearchOption
}