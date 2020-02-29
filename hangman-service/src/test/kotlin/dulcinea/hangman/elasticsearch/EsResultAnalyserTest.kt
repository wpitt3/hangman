package dulcinea.hangman.elasticsearch

import org.junit.Test
import org.assertj.core.api.Assertions.assertThat as assertThat

class EsResultAnalyserTest {

    @Test
    fun `ResultAnalyser gets score from aggResult`() {
        val result = EsResult(0, listOf(), listOf(ar("A", 1)))

        val score = EsResultAnalyser().score(result, listOf())

        assertThat(score).isEqualTo(1)
    }

    @Test
    fun `ResultAnalyser gets lowest scoring aggResult`() {
        val result = EsResult(0, listOf(), listOf(
                ar("A", 3),
                ar("B", 2),
                ar("C", 5)
        ))

        val score = EsResultAnalyser().score(result, listOf())

        assertThat(score).isEqualTo(2)
    }

    @Test
    fun `ResultAnalyser ignores searchTeam`() {
        val result = EsResult(0, listOf(), listOf(
                ar("A", 1),
                ar("B", 2)
        ))

        val score = EsResultAnalyser().score(result, listOf("A"))

        assertThat(score).isEqualTo(2)
    }

    @Test
    fun `ResultAnalyser gets highest scoring of lowest scoring aggResult children`() {
        val result = EsResult(0, listOf(), listOf(
                ar("C", listOf(
                        ar("A", 2),
                        ar("B", 5)
                )),
                ar("D", listOf(
                        ar("A", 4),
                        ar("B", 3)
                ))
        ))

        val score = EsResultAnalyser().score(result, listOf("E"))

        assertThat(score).isEqualTo(3)
    }

    @Test
    fun `ResultAnalyser gets highest scoring of lowest scoring aggResult children ignoring search terms`() {
        val result = EsResult(0, listOf(), listOf(
                ar("D", listOf(
                        ar("A", 2), //ignored
                        ar("B", 5),
                        ar("C", 4)
                )),
                ar("E", listOf(
                        ar("A", 4), //ignored
                        ar("B", 3),
                        ar("C", 5)
                ))
        ))

        val score = EsResultAnalyser().score(result, listOf("A"))

        assertThat(score).isEqualTo(4)
    }

    @Test
    fun `ResultAnalyser gets highest scoring of lowest scoring aggResult children ignoring where parent matches child`() {
        val result = EsResult(0, listOf(), listOf(
                ar("B", listOf(
                        ar("A", 2), //ignored
                        ar("B", 5), //ignored
                        ar("C", 3)
                )),
                ar("C", listOf(
                        ar("A", 4), //ignored
                        ar("B", 2),
                        ar("C", 5)  //ignored
                ))
        ))

        val score = EsResultAnalyser().score(result, listOf("A"))

        assertThat(score).isEqualTo(3)
    }

    @Test
    fun `ResultAnalyser gets highest of highest scoring of lowest scoring aggResult children`() {
        val result = EsResult(0, listOf(), listOf(
                ar("A", listOf(
                        ar("A", listOf( //ignored
                                ar("A", 9), //ignored
                                ar("B", 9), //ignored
                                ar("C", 9), //ignored
                                ar("D", 9)  //ignored
                        )),
                        ar("B", listOf(
                                ar("A", 9), //ignored
                                ar("B", 9), //ignored
                                ar("C", 3),
                                ar("D", 9)
                        )),
                        ar("C", listOf(
                                ar("A", 4), //ignored
                                ar("B", 2),
                                ar("C", 5), //ignored
                                ar("D", 3)
                        ))
                )),
                ar("B", listOf(
                        ar("A", listOf(
                                ar("A", 1), //ignored
                                ar("B", 1), //ignored
                                ar("C", 5),
                                ar("D", 4), // this is used
                                ar("E", 1)  //ignored
                        )),
                        ar("B", listOf(
                                ar("A", 9), //ignored
                                ar("B", 9), //ignored
                                ar("C", 9), //ignored
                                ar("D", 9)  //ignored
                        )),
                        ar("C", listOf(
                                ar("A", 2),
                                ar("B", 2), //ignored
                                ar("C", 5), //ignored
                                ar("D", 6)
                        ))
                ))
        ))

        val score = EsResultAnalyser().score(result, listOf("E"))

        assertThat(score).isEqualTo(4)
    }

    private fun ar(key: String, score: Long): EsResult.AggResult {
        return EsResult.AggResult(key, score, listOf())
    }

    private fun ar(key: String, children: List<EsResult.AggResult>): EsResult.AggResult {
        return EsResult.AggResult(key, 0, children)
    }

    private fun aggResults(vararg scores: Long): List<EsResult.AggResult> {
        return scores.map{ EsResult.AggResult("", it, listOf()) }
    }

    private fun aggResults(vararg aggResults: List<EsResult.AggResult>): List<EsResult.AggResult>{
        return aggResults.map{ EsResult.AggResult("", 1, it)}
    }
}
