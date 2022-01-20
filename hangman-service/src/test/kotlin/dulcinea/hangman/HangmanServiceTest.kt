package dulcinea.hangman


import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyChar

import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HangmanServiceTest {

    var wordService: WordService = mock(WordService::class.java)
    lateinit var hangmanService: HangmanService

    @Before
    fun setup() {
        hangmanService = HangmanService(wordService, HangmanProps(6, "", 1))
    }

    @Test
    fun `status is returned`() {
        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("______", listOf(), listOf()))
    }

    @Test
    fun `status contains internal state`() {
        hangmanService.with[0] = 'A'
        hangmanService.without.add('D')

        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("A_____", listOf("D"), listOf()))
    }

    @Test
    fun `new game resets status`() {
        hangmanService.with[0] = 'A'
        hangmanService.without.add('D')
        hangmanService.newGame()

        assertThat(hangmanService.without).isEmpty()
        assertThat(hangmanService.with).hasSize(6)
        assertThat(hangmanService.with).allMatch{ it == null }
    }

    @Test
    fun `make guesses with invalid`() {
        hangmanService.makeGuess("")
        hangmanService.makeGuess(" ")
        hangmanService.makeGuess("0")
        hangmanService.makeGuess("AA")

        verify(wordService, never()).makeGuess(anyChar(), anyList(), anySet())
    }

    @Test
    fun `make guesses with already correct letter`() {
        hangmanService.with[0] = 'A'
        hangmanService.makeGuess("A")

        verify(wordService, never()).makeGuess(anyChar(), anyList(), anySet())
    }

    @Test
    fun `make guesses with already false letter`() {
        hangmanService.without.add('A')
        hangmanService.makeGuess("A")

        verify(wordService, never()).makeGuess(anyChar(), anyList(), anySet())
    }
}