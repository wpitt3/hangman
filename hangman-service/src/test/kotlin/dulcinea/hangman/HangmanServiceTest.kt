package dulcinea.hangman


import dulcinea.hangman.elasticsearch.EsHangmanService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HangmanServiceTest {

    var esHangmanService: EsHangmanService = mock(EsHangmanService::class.java)
    lateinit var hangmanService: HangmanService

    @Before
    fun setup() {
        hangmanService = HangmanService(esHangmanService, HangmanProps(6, ""))
    }

    @Test
    fun `status is returned`() {
        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("______", listOf()))
    }

    @Test
    fun `status contains internal state`() {
        hangmanService.with[0] = "A"
        hangmanService.without.add("D")

        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("A_____", listOf("D")))
    }

    @Test
    fun `new game resets status`() {
        hangmanService.with[0] = "A"
        hangmanService.without.add("D")
        hangmanService.newGame()

        assertThat(hangmanService.without).isEmpty()
        assertThat(hangmanService.with).hasSize(6)
        assertThat(hangmanService.with).allMatch{ it == ""}
    }

    @Test
    fun `make guesses with invalid`() {
        hangmanService.makeGuess("")
        hangmanService.makeGuess(" ")
        hangmanService.makeGuess("0")
        hangmanService.makeGuess("AA")

        verify(esHangmanService, never()).makeGuess(anyString(), anyList(), anyList())
    }

    @Test
    fun `make guesses with already correct letter`() {
        hangmanService.with[0] = "A"
        hangmanService.makeGuess("A")

        verify(esHangmanService, never()).makeGuess(anyString(), anyList(), anyList())
    }

    @Test
    fun `make guesses with already false letter`() {
        hangmanService.without.add("A")
        hangmanService.makeGuess("A")

        verify(esHangmanService, never()).makeGuess(anyString(), anyList(), anyList())
    }
}