package dulcinea.hangman

import org.apache.coyote.http11.Constants.a
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HangmanServiceTest {

    var wordRepository: WordRepository = Mockito.mock(WordRepository::class.java)
    lateinit var hangmanService: HangmanService

    @Before
    fun setup() {
        hangmanService = HangmanService(wordRepository)
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

        verify(wordRepository, never()).findAggregations(anyList(), anyList())
    }

    @Test
    fun `make guesses with already correct letter`() {
        hangmanService.with[0] = "A"
        hangmanService.makeGuess("A")

        verify(wordRepository, never()).findAggregations(anyList(), anyList())
    }

    @Test
    fun `make guesses with already false letter`() {
        hangmanService.without.add("A")
        hangmanService.makeGuess("A")

        verify(wordRepository, never()).findAggregations(anyList(), anyList())
    }
}