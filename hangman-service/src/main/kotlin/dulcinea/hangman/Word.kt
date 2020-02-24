package dulcinea.hangman


data class Word(var input: String) {
    val word: String
    val position: List<String>
    val unique: List<String>
    val count: List<String>
    val positionOfSingle: List<String>
    val negative: List<String>

    init {
        val letters = input.toUpperCase().toCharArray().map { it.toString() }
        word = input.toUpperCase()
        unique = letters.toSet().toList()
        position = (0..(letters.size - 1)).map{"${letters[it]}$it"}
        count = unique.map{ letter -> "${letters.count { it == letter }}$letter"}
        positionOfSingle = (0..(letters.size - 1))
                .filter{ count.contains("1${letters[it]}") }
                .map{ "${letters[it]}$it" }
        negative = (0..25).map { (it + 65).toChar().toString() }.filter { !unique.contains(it) }
    }

    override fun toString(): String {
        return "word='$word', position=$position, unique=$unique, count=$count, positionOfSingle=$positionOfSingle, negative=$negative"
    }


}
