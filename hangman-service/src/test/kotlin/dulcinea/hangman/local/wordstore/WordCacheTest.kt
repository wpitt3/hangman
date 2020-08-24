package dulcinea.hangman.local.wordstore

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class WordCacheTest {

    @Test
    fun `word is set`() {
        assertThat(Word("ADJURE").matches(emptyList("      "), setOf())).isTrue()
        assertThat(Word("ADJURE").matches(emptyList(" D    "), setOf())).isTrue()
        assertThat(Word("ADJURE").matches(emptyList(" B    "), setOf())).isFalse()
        assertThat(Word("ADJURE").matches(emptyList("      "), setOf('D'))).isFalse()
        assertThat(Word("ADDED").matches(emptyList(" D   "), setOf())).isFalse()
    }

    @Test
    fun `wordcache returns guesses`() {
        val wordCache = wordCache()

        val result = wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf())

        assertThat(result).hasSize(7)
        assertThat(result).containsExactly(
            listOf(Word("AEEEEB")),
            listOf(Word("EAEEEE")),
            listOf(Word("EEAEEB")),
            listOf(Word("EEEAEE")),
            listOf(Word("EEEEAE")),
            listOf(Word("EEEEEA")),
            listOf(Word("EEEEEE"))
        )
    }

    @Test
    fun `wordcache cachekey`() {
        assertThat(wordCache().cacheKey(listOf(null, 'C'), setOf('A', 'B'))).isEqualTo(wordCache().cacheKey(listOf(null, 'C'), setOf('B', 'A')))
    }

    @Test
    fun `cache gets hit`() {
        val wordCache = wordCache()

        wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf())
        wordCache.makeGuess('B', listOf('A', null, null, null, null, null), setOf())
        wordCache.makeGuess('B', listOf(null, 'A', null, null, null, null), setOf())
        wordCache.makeGuess('B', listOf(null, null, 'A', null, null, null), setOf())
    }

    private fun emptyList(word: String): MutableList<Char?> {
        return (0..(word.length-1)).map{if (word[it] == ' ') null else word[it]}.toMutableList()
    }

    private fun wordCache(): WordCache {
        return WordCache(listOf("AEEEEB", "EAEEEE", "EEAEEB", "EEEAEE", "EEEEAE", "EEEEEA", "EEEEEE"))
    }
}