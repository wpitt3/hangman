package dulcinea.hangman

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class WordTest {

    @Test
    fun `word is set`() {
        assertThat(Word("ADJURE").word).isEqualTo("ADJURE")
        assertThat(Word("Adjoin").word).isEqualTo("ADJOIN")
    }

    @Test
    fun `unique creates each letter`() {
        assertThat(Word("ADJURE").unique).containsExactlyInAnyOrder("A", "D", "J", "U", "R", "E")
    }

    @Test
    fun `unique has no repeats`() {
        assertThat(Word("ACCESS").unique).containsExactlyInAnyOrder("A", "C", "E", "S")
    }

    @Test
    fun `postion denotes position of letters`() {
        assertThat(Word("ADJURE").position).containsExactlyInAnyOrder("A0", "D1", "J2", "U3", "R4", "E5")
    }

    @Test
    fun `postion does not care about frequency`() {
        assertThat(Word("ACCESS").position).containsExactlyInAnyOrder("A0", "C1", "C2", "E3", "S4", "S5")
    }

    @Test
    fun `count displays the count of each letter`() {
        assertThat(Word("ADJURE").count).containsExactlyInAnyOrder("1A", "1D", "1E", "1J", "1R", "1U")
    }

    @Test
    fun `count displays the count of each letter with twos`() {
        assertThat(Word("ACCESS").count).containsExactlyInAnyOrder("1A", "2C", "1E", "2S")
    }

    @Test
    fun `postionOfSingle denotes position of single occuring letters`() {
        assertThat(Word("ADJURE").positionOfSingle).containsExactlyInAnyOrder("A0", "D1", "J2", "U3", "R4", "E5")
    }

    @Test
    fun `postionOfSingle ignores letters which are used more than once`() {
        assertThat(Word("ACCESS").positionOfSingle).containsExactlyInAnyOrder("A0", "E3")
    }

    @Test
    fun `negative includes all letters not in word`() {
        assertThat(Word("ADJURE").negative).containsExactlyInAnyOrder("B", "C", "F", "G", "H", "I", "K", "L", "M", "N", "O", "P", "Q", "S", "T", "V", "W", "X", "Y", "Z")
    }
}