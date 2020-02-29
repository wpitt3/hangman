package dulcinea.hangman.elasticsearch


import dulcinea.hangman.SearchOption
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer

@RunWith(MockitoJUnitRunner::class)
class EsHangmanServiceTest {

    var wordRepository: EsWordRepository = mock(EsWordRepository::class.java)
    var resultAnalyser: EsResultAnalyser = mock(EsResultAnalyser::class.java)
    lateinit var hangmanService: EsHangmanService

    @Before
    fun setup() {
        hangmanService = EsHangmanService(wordRepository, resultAnalyser)
    }

    @Test
    fun `make guesses calls wordRepository with existing letter`() {
        val with = with("B", 0)

        `when`(wordRepository.findAggregations(anyList(), anyList())).thenReturn(EsResult(0, listOf(), listOf()))
        `when`(resultAnalyser.score(any(EsResult::class.java), anyList())).thenReturn(1)

        hangmanService.makeGuess("A", with, listOf())

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
        `when`(wordRepository.findAggregations(anyList(), anyList())).thenReturn(EsResult(0, listOf(), listOf()))
        `when`(wordRepository.findAggregations(eq(listOf("A2")), anyList())).thenReturn(EsResult(1, listOf(), listOf()))

        mockResultAnalyserToResultTotal()

        val result = hangmanService.makeGuess("A", with(), listOf())

        assertThat(result).isEqualTo(SearchOption(listOf("A2"), listOf()))
    }

    @Test
    fun `update state with non selected letter`() {
        `when`(wordRepository.findAggregations(anyList(), eq(listOf()))).thenReturn(EsResult(0, listOf(), listOf()))
        `when`(wordRepository.findAggregations(anyList(), eq(listOf("A")))).thenReturn(EsResult(1, listOf(), listOf()))

        mockResultAnalyserToResultTotal()

        val result = hangmanService.makeGuess("A", with(), listOf())

        assertThat(result).isEqualTo(SearchOption(listOf(), listOf("A")))
    }

    private fun mockResultAnalyserToResultTotal() {
        `when`(resultAnalyser.score(any(EsResult::class.java), anyList())).thenAnswer( Answer {
            val x = it.arguments[0]; if (x is EsResult) x.total.toLong() else 0L
        })
    }

    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)
    private fun <T> eq(value: T): T = Mockito.eq(value)

    private fun with(letter: String, index: Int): List<String> {
        val with = with()
        with[index] = letter
        return with
    }

    private fun with(): MutableList<String> {
        return (0..5).map{""}.toMutableList()
    }
}