package dulcinea.hangman

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class ResultAnalyserTest {

    @Test
    fun `ResultAnalyser gets score from aggResult`() {
        val result =  Result(0, listOf(), aggResults(1))

        val score = ResultAnalyser().score(result)

        assertThat(score).isEqualTo(1)
    }

    @Test
    fun `ResultAnalyser gets lowest scoring aggResult`() {
        val result =  Result(0, listOf(), aggResults(3, 2, 4))

        val score = ResultAnalyser().score(result)

        assertThat(score).isEqualTo(2)
    }

    @Test
    fun `ResultAnalyser gets highest scoring of lowest scoring aggResult children`() {
        val result =  Result(0, listOf(),
            aggResults(
                aggResults(2, 5),
                aggResults(4, 3)
            ))

        val score = ResultAnalyser().score(result)

        assertThat(score).isEqualTo(3)
    }

    @Test
    fun `ResultAnalyser gets highest of highest scoring of lowest scoring aggResult children`() {
        val result =  Result(0, listOf(),
            aggResults(
                aggResults(
                    aggResults(6, 5), // 5 is highest of lowest this block
                    aggResults(9, 3)
                ),
                aggResults(
                    aggResults(3, 6),
                    aggResults(5, 4) // 4 is highest of lowest this block
                )
            ))

        val score = ResultAnalyser().score(result)

        assertThat(score).isEqualTo(5)
    }

    private fun aggResults(vararg scores: Long): List<Result.AggResult> {
        return scores.map{ Result.AggResult("", it, listOf()) }
    }

    private fun aggResults(vararg aggResults: List<Result.AggResult>): List<Result.AggResult>{
        return aggResults.map{ Result.AggResult("", 1, it)}
    }
}

