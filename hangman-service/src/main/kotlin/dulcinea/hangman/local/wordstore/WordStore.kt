package dulcinea.hangman.local.wordstore

class WordCache(words: List<String>) {
    private val minBeforeDoubles = 40
    private val allWords: List<Word> = words.map { Word(it) }


    fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>): Map<List<Char?>, List<Word>> {
        val unique: Set<Char> = with.filterNotNull().toSet()
        val single = unique.filter{ letter -> with.count { it == letter } == 1}.toSet()
        val double = unique.filter{ letter -> with.count { it == letter } == 2}.toSet()

        val words = allWords.filter{ it.matches(with, without, single, double) }
        return makeGuess(letter, with, without, words)
    }

    fun makeGuess(letter: Char, with: List<Char?>, without: Set<Char>, words: List<Word>): Map<List<Char?>, List<Word>> {
        val guesses = mutableMapOf<List<Char?>, List<Word>>()
        guesses[with] = words.filterWithout(with, without, letter)

        guesses.putAll(with.indices.filter{with[it] == null}.map{
            val newWith = with.addLetters(letter, it)
            newWith.toList() to words.filterSingle(newWith, without, letter)
        }.toMap())

        if (words.size < minBeforeDoubles) {
            guesses.putAll(combinationPairs(with).map { combo ->
                val newWith = with.addLetters(letter, combo[0], combo[1])
                newWith.toList() to words.filterDouble(newWith, without, letter)
            })
        }

        return guesses
    }

    private fun List<Char?>.addLetters(letter: Char, vararg indices: Int) : List<Char?> {
        return indices.fold(this.toMutableList(), {acc, i ->  acc[i] = letter; acc})
    }

    private fun List<Word>.filterWithout(with: List<Char?>, without: Set<Char>, letter: Char): List<Word> {
        return this.filter{ it.matches(with, without + letter) }
    }

    private fun List<Word>.filterSingle(with: List<Char?>, without: Set<Char>, letter: Char): List<Word> {
        return this.filter{ it.matches(with, without, setOf(letter), setOf()) }
    }

    private fun List<Word>.filterDouble(with: List<Char?>, without: Set<Char>, letter: Char): List<Word> {
        return this.filter { it.matches(with, without, setOf(), setOf(letter)) }
    }

    private fun combinationPairs(with: List<Char?>): List<List<Int>> {
        val remaining = with.indices.filter{with[it] == null}
        return remaining.indices.map{ index ->
            ((index+1)..(remaining.size-1)).map{
                listOf(remaining[index], remaining[it])
            }
        }.flatten()
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
        return this.with.indices.all{ with[it] == null || (with[it] == this.with[it]) } && this.without intersect without == without
    }

    fun matches(with: List<Char?>, without: Set<Char>, singleLetters: Set<Char>, doubleLetters: Set<Char>): Boolean {
        return this.single intersect singleLetters == singleLetters &&
                this.double intersect doubleLetters == doubleLetters &&
                this.without intersect without == without &&
                this.with.indices.all{ with[it] == null || with[it] == this.with[it] } &&
                doubleLetters.all { letter -> val count = with.count{ it == letter }; count == 0 || count == 2 }
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