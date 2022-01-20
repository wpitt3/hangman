package dulcinea.hangman.local.wordstore

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class WordStoreTest {
    @Test
    fun `cache gets hit`() {
        val wordCache = wordCache()

        println(wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf()))
        println(wordCache.makeGuess('B', listOf('A', null, null, null, null, null), setOf()))
        println(wordCache.makeGuess('B', listOf(null, 'A', null, null, null, null), setOf()))
        println(wordCache.makeGuess('B', listOf(null, null, 'A', null, null, null), setOf()))
    }

    private fun toList(word: String): MutableList<Char?> {
        return (0..(word.length-1)).map{if (word[it] == ' ') null else word[it]}.toMutableList()
    }

    private fun wordCache(): WordCache {
        return WordCache(listOf("ABIDES", "BACHES", "BEAMED", "BEHAVE", "BELEAP", "BUCKRA", "BEDDER", "FLUFFY"))
    }
}
