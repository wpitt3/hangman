package dulcinea.hangman

import org.assertj.core.api.Assertions
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.assertj.core.api.Assertions.assertThat

class WordRepositoryTest: ElasticsearchWrapper() {

    companion object {
        lateinit var repo: WordRepository

        @BeforeClass @JvmStatic fun setup() {
            elasticsearchContainer.start()
            val address = elasticsearchContainer.tcpHost.toString().split(":")
            repo = WordRepository("hangman", address[1].toInt(), "localhost")
            repo.setupIndex()
            repo.create(Word("ADJURE"))
            repo.create(Word("BADGER"))
            repo.create(Word("GARDEN"))
            repo.refresh()
        }

        @AfterClass @JvmStatic fun teardown() {
            elasticsearchContainer.stop()
        }
    }

    @Test
    fun basicGetAggs() {
        val result: Result = repo.findAggregations(listOf(), listOf())

        assertThat(result.total).isEqualTo(3)
        assertThat(result.words).containsExactlyInAnyOrder("ADJURE","BADGER","GARDEN")
        assertThat(result.aggs[0].key).isEqualTo("A1")
        assertThat(result.aggs[0].count).isEqualTo(2)
        assertThat(result.aggs[0].aggs[1].key).isEqualTo("E4")
        assertThat(result.aggs[0].aggs[1].count).isEqualTo(2)
    }

    @Test
    fun `get aggs with e4`() {
        val result: Result = repo.findAggregations(listOf("E4"), listOf())

        assertThat(result.total).isEqualTo(2)
        assertThat(result.words).containsExactlyInAnyOrder("BADGER","GARDEN")
        assertThat(result.aggs[0].key).isEqualTo("A1")
        assertThat(result.aggs[0].count).isEqualTo(2)
        assertThat(result.aggs[0].aggs[1].key).isEqualTo("E4")
        assertThat(result.aggs[0].aggs[1].count).isEqualTo(2)
    }

    @Test
    fun `get aggs without a B`() {
        val result: Result = repo.findAggregations(listOf(), listOf("B"))

        assertThat(result.total).isEqualTo(2)
        assertThat(result.words).containsExactlyInAnyOrder("ADJURE","GARDEN")
        assertThat(result.aggs[0].key).isEqualTo("A0")
        assertThat(result.aggs[0].count).isEqualTo(1)
        assertThat(result.aggs[0].aggs[1].key).isEqualTo("D1")
        assertThat(result.aggs[0].count).isEqualTo(1)
    }
}