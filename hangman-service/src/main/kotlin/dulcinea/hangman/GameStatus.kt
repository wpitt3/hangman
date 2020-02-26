package dulcinea.hangman

data class GameStatus(val state: String, val wrongGuesses: List<String>)