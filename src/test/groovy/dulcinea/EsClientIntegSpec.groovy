package dulcinea

import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class EsClientIntegSpec extends Specification {

  @Shared
  EsClient esClient

  BlockingVariable result

  def setupSpec() {
    BlockingVariable deployed = new BlockingVariable(10)
    esClient = new EsClient("hangman")
    esClient.setup {
      deployed.set(true)
    }
    assert deployed.get()
  }

  def setup() {
    result = new BlockingVariable(1)
  }

  def cleanupSpec() {
    esClient.close()
  }

  void "All aggregation"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations([], [], x)

    then:
      result.get().aggs.size() == 179
  }

  void "Aggregation with term"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations(["A1"], [], x)

    then:
      result.get().aggs.size() == 137
  }

  void "Aggregation without term"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations([], ["A"], x)

    then:
      result.get().aggs.size() == 171
  }

  void "All aggregation withAnalyser"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations([], [], x)

    then:
      Map groupedAggs = EsAggAnalyser.groupAggsByLetter(result.get().aggs, [])
      Integer score = EsAggAnalyser.findMinScore(groupedAggs)
      score == 1931
  }

  Closure setResult = {
    result.set(it)
  }
}
