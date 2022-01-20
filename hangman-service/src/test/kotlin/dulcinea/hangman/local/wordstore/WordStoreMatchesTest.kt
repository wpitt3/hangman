package dulcinea.hangman.local.wordstore

import org.assertj.core.api.AbstractBooleanAssert
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class WordStoreMatchesTest {

    @Test
    fun `word matches no restrictions`() {
        wordAndPatternMustContain("ADJURE", "      ").isTrue()
    }

    @Test
    fun `word matches D is position 2`() {
        wordAndPatternMustContain("ADJURE", " D    ").isTrue()
    }

    @Test
    fun `word not matches B is position 2`() {
        wordAndPatternMustContain("ADJURE", " B    ").isFalse()
    }

    @Test
    fun `word matches B is not in word`() {
        wordAndPatternMustNotContain("ADJURE", "      ", setOf('B')).isTrue()
    }

    @Test
    fun `word not matches D is not in word`() {
        wordAndPatternMustNotContain("ADJURE", "      ", setOf('D')).isFalse()
    }

    @Test
    fun `word matches must contain A`() {
        wordAndPatternMustContain("ADJURE", "      ", setOf('A')).isTrue()
    }

    @Test
    fun `word not matches must contain B`() {
        wordAndPatternMustContain("ADJURE", "      ", setOf('B')).isFalse()
    }

    @Test
    fun `word matches must contain Two Ds`() {
        wordAndPatternMustContain("ADDER", "      ", setOf(), setOf('D')).isTrue()
    }

    @Test
    fun `word matches must contain Two Ds with pattern`() {
        wordAndPatternMustContain("ADDER", " DD   ", setOf(), setOf('D')).isTrue()
    }

    @Test
    fun `word not matches must contain Two Ds with non matching pattern`() {
        wordAndPatternMustContain("ADDER", " D    ", setOf(), setOf('D')).isFalse()
    }

    @Test
    fun `word matches must contain Three Ds`() {
        wordAndPatternMustContain("ADDED", "      ", setOf(), setOf(), setOf('D')).isTrue()
    }

    @Test
    fun `word matches must contain Three Ds with pattern`() {
        wordAndPatternMustContain("ADDED", " DD D", setOf(), setOf(), setOf('D')).isTrue()
    }

    @Test
    fun `word matches complex criteria`() {
        wordAndPatternMustContain("SCROLLS", "S     S", setOf('C', 'O', 'R'), setOf('S', 'L'), setOf()).isTrue()
    }

    private fun wordAndPatternMustNotContain(word: String, pattern: String, without: Set<Char>): AbstractBooleanAssert<*> {
        return assertThat(Word(word).matches(toList(pattern), without))
    }

    private fun wordAndPatternMustContain(word: String, pattern: String, single: Set<Char> = setOf(), double: Set<Char> = setOf(), triple: Set<Char> = setOf()): AbstractBooleanAssert<*> {
        return assertThat(Word(word).matches(toList(pattern), setOf(), single, double, triple))
    }

    private fun toList(word: String): MutableList<Char?> {
        return (0..(word.length-1)).map{if (word[it] == ' ') null else word[it]}.toMutableList()
    }
}
