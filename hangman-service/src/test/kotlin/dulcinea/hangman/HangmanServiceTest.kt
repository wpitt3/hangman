package dulcinea.hangman


import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatcher

import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer

@RunWith(MockitoJUnitRunner::class)
class HangmanServiceTest {

    var wordRepository: WordRepository = mock(WordRepository::class.java)
    var resultAnalyser: ResultAnalyser = mock(ResultAnalyser::class.java)
    lateinit var hangmanService: HangmanService

    @Before
    fun setup() {
        hangmanService = HangmanService(wordRepository, resultAnalyser)
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

    @Test
    fun `make guesses calls wordRepository`() {
        `when`(wordRepository.findAggregations(anyList(), anyList())).thenReturn(Result(0, listOf(), listOf()))
        `when`(resultAnalyser.score(any(Result::class.java))).thenReturn(1)

        hangmanService.makeGuess("A")

        verify(wordRepository, times(1)).findAggregations(listOf("A0"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("A1"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("A2"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("A3"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("A4"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("A5"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf(), listOf("A"))
    }

    @Test
    fun `make guesses calls wordRepository with existing letter`() {
        hangmanService.with[0] = "B"
        `when`(wordRepository.findAggregations(anyList(), anyList())).thenReturn(Result(0, listOf(), listOf()))
        `when`(resultAnalyser.score(any(Result::class.java))).thenReturn(1)

        hangmanService.makeGuess("A")

        verify(wordRepository, never()).findAggregations(listOf("B0", "A0"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0", "A1"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0", "A2"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0", "A3"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0", "A4"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0", "A5"), listOf())
        verify(wordRepository, times(1)).findAggregations(listOf("B0"), listOf("A"))
    }

    @Test
    fun `update state with selected letter position`() {
        `when`(wordRepository.findAggregations(anyList(), anyList())).thenReturn(Result(0, listOf(), listOf()))
        `when`(wordRepository.findAggregations(eq(listOf("A2")), anyList())).thenReturn(Result(1, listOf(), listOf()))

        mockResultAnalyserToResultTotal()

        hangmanService.makeGuess("A")

        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("__A___", listOf()))
    }

    @Test
    fun `update state with non selected letter`() {
        `when`(wordRepository.findAggregations(anyList(), eq(listOf()))).thenReturn(Result(0, listOf(), listOf()))
        `when`(wordRepository.findAggregations(anyList(), eq(listOf("A")))).thenReturn(Result(1, listOf(), listOf()))

        mockResultAnalyserToResultTotal()

        hangmanService.makeGuess("A")

        assertThat(hangmanService.getStatus()).isEqualTo(GameStatus("______", listOf("A")))
    }

    private fun mockResultAnalyserToResultTotal() {
        `when`(resultAnalyser.score(any(Result::class.java))).thenAnswer( Answer {
            val x = it.arguments[0]; if (x is Result) x.total.toLong() else 0L
        })
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
    private fun <T> eq(value: T): T = Mockito.eq(value)
}