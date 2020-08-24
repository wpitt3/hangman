package dulcinea.hangman.local.wordstore

class WordCache(words: List<String>) {
    private val allWords: List<Word> = words.map { Word(it) }

    fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>): List<List<Word>> {
        val words = findMatchingWords(with, without)
        val guesses = with.indices.map{
            if (with[it] == null) {
                val x = with.toMutableList()
                x[it] = letter
                findMatchingWords(x, without, words)
            } else {
                listOf()
            }
        }.toMutableList()
        guesses.add(findMatchingWords(with, without + letter, words))
        return guesses.toList()
    }

    fun findMatchingWords(with: List<Char?>, without: Set<Char>): List<Word> {
        return findMatchingWords(with, without, allWords)
    }

    private fun findMatchingWords(with: List<Char?>, without: Set<Char>, words: List<Word>): List<Word> {
        val result = words.filter { it.matches(with, without) }
        return result
    }

    fun cacheKey(with: List<Char?>, without: Set<Char>): String {
        return with.map{ it ?: " "}.joinToString() + without.toList().sorted().joinToString()
    }
}

data class Word(var input: String) {
    private val aCharIndex: Int = 'A'.toInt()
    private val word: String = input.toUpperCase()
    private val with: List<Char>
    private val without: Set<Char>
    private val single: Set<Char>
    private val double: Set<Char>

    init {
        val letters = word.map{it}
        val unique = letters.toSet()
        single = unique.filter{ letter -> letters.count { it == letter } == 1}.toSet()
        double = unique.filter{ letter -> letters.count { it == letter } == 2}.toSet()
        with = letters.map{ it }
        without = (0..25).map{ (it + aCharIndex).toChar() }
                .filter{ !with.contains(it) }
                .toSet()
    }

    fun matches(with: List<Char?>, without: Set<Char>): Boolean {
        return this.with.indices.all{ with[it] == null || (with[it] == this.with[it] && single.contains(with[it])) } && this.without intersect without == without
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Word
        if (word != other.word) return false
        return true
    }

    override fun hashCode(): Int {
        return word.hashCode()
    }

    override fun toString(): String {
        return word
    }

}