//package dulcinea.hangman.local.wordstore
//
//import org.junit.Test
//import org.assertj.core.api.Assertions.assertThat as assertThat
//
//class WordStoreTest {
//
//    @Test
//    fun `word is set`() {
//        assertThat(Word("ADJURE").matches(emptyList("      "), setOf())).isTrue()
//        assertThat(Word("ADJURE").matches(emptyList(" D    "), setOf())).isTrue()
//        assertThat(Word("ADJURE").matches(emptyList(" B    "), setOf())).isFalse()
//        assertThat(Word("ADJURE").matches(emptyList("      "), setOf('D'))).isFalse()
//        assertThat(Word("ADDER").matches(emptyList(" DD  "), setOf(), setOf('A', 'E', 'R'), setOf('D'), setOf())).isTrue()
//        assertThat(Word("ADDER").matches(emptyList("     "), setOf(), setOf('A', 'E', 'R'), setOf('D'), setOf())).isTrue()
//        assertThat(Word("ADDER").matches(emptyList(" D   "), setOf(), setOf('A', 'E', 'R'), setOf('D'), setOf())).isFalse()
//        assertThat(Word("ADDED").matches(emptyList(" DD D"), setOf(), setOf('A', 'E'), setOf(), setOf('D'))).isTrue()
//        assertThat(Word("ADDED").matches(emptyList(" DD  "), setOf(), setOf('A', 'E'), setOf(), setOf('D'))).isFalse()
//        assertThat(Word("ADDED").matches(emptyList(" D   "), setOf(), setOf('A', 'E'), setOf(), setOf('D'))).isFalse()
//        assertThat(Word("ADDED").matches(emptyList("     "), setOf(), setOf('A', 'E'), setOf(), setOf('D'))).isTrue()
//        assertThat(Word("SCROLLS").matches(emptyList("S     S"), setOf(), setOf('C', 'O', 'R'), setOf('S', 'L'), setOf())).isTrue()
//    }
//
//    @Test
//    fun `wordcache returns guesses for single letter`() {
//        val wordCache = wordCache()
//
//        val result = wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf('F'))
//
//        assertThat(result).hasSize(7)
//        assertThat(result).contains(pair("      ", "BEDDER"))
//        assertThat(result).contains(pair("A     ", "ABIDES"))
//        assertThat(result).contains(pair(" A    ", "BACHES"))
//        assertThat(result).contains(pair("  A   ", "BEAMED"))
//        assertThat(result).contains(pair("   A  ", "BEHAVE"))
//        assertThat(result).contains(pair("    A ", "BELEAP"))
//        assertThat(result).contains(pair("     A", "BUCKRA"))
//    }
//
//    @Test
//    fun `wordcache returns guesses for single letter and without`() {
//        val wordCache = wordCache()
//
//        val result = wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf('D'))
//
//        assertThat(result).hasSize(5)
//        assertThat(result).contains(pair(" A    ", "BACHES"))
//        assertThat(result).contains(pair("   A  ", "BEHAVE"))
//        assertThat(result).contains(pair("    A ", "BELEAP"))
//        assertThat(result).contains(pair("     A", "BUCKRA"))
//    }
//
//    @Test
//    fun `wordcache returns guesses for double letter`() {
//        val wordCache = wordCache()
//
//        val result = wordCache.makeGuess('D', listOf(null, null, null, null, null, null), setOf())
//
//        assertThat(result).hasSize(4)
//        assertThat(result).contains(pair("   D  ", "ABIDES"))
//        assertThat(result).contains(pair("     D", "BEAMED"))
//        assertThat(result).contains(pair("  DD  ", "BEDDER"))
//    }
//
//    @Test
//    fun `wordcache returns guesses for existing double letter`() {
//        val wordCache = wordCache()
//
//        val result = wordCache.makeGuess('B', listOf(null, null, 'D', 'D', null, null), setOf())
//
//        assertThat(result).hasSize(2)
//        assertThat(result).contains(pair("B DD  ", "BEDDER"))
//    }
//
//    @Test
//    fun `wordcache returns guesses for existing triple letter`() {
//        val wordCache = wordCache()
//
//        val result = wordCache.makeGuess('F', listOf(null, null, null, null, null, null), setOf())
//
//        assertThat(result).hasSize(2)
//        assertThat(result).contains(pair("F  FF ", "FLUFFY"))
//    }
//
//    @Test
//    fun `cache gets hit`() {
//        val wordCache = wordCache()
//
//        wordCache.makeGuess('A', listOf(null, null, null, null, null, null), setOf())
//        wordCache.makeGuess('B', listOf('A', null, null, null, null, null), setOf())
//        wordCache.makeGuess('B', listOf(null, 'A', null, null, null, null), setOf())
//        wordCache.makeGuess('B', listOf(null, null, 'A', null, null, null), setOf())
//    }
//
//    private fun emptyList(word: String): MutableList<Char?> {
//        return (0..(word.length-1)).map{if (word[it] == ' ') null else word[it]}.toMutableList()
//    }
//
//    private fun wordCache(): WordCache {
//        return WordCache(listOf("ABIDES", "BACHES", "BEAMED", "BEHAVE", "BELEAP", "BUCKRA", "BEDDER", "FLUFFY"))
//    }
//
//    private fun pair(key: String, value: String?): Map.Entry<List<Char?>, List<Word>>? {
//        val x = hashMapOf<List<Char?>, List<Word>>()
//        x[key.map{if (it == ' ') null else it}] = if (value != null) listOf(Word(value)) else listOf()
//        return x.entries.toList()[0]
//    }
//}
