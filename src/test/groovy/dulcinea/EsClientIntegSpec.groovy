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
      esClient.findAggregations([Word.positionToNum("A1")], [], x)

    then:
      result.get().aggs.size() == 137
  }

  void "Aggregation without term"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations([], [-Word.letterToNum("A")], x)

    then:
      result.get().aggs.size() == 171
  }

  void "All aggregation withAnalyser"() {
    given:
      Closure x = { result.set(it) }

    when:
      esClient.findAggregations([], [], x)
      Map groupedAggs = EsAggAnalyser.groupAggsByLetter(result.get().aggs, [])
      Integer score = EsAggAnalyser.findMinScore(groupedAggs)

    then:
      groupedAggs.count{ k,v -> v.size() == 7} == 24
      score == 1931

      groupedAggs[Word.letterToNum("E")][(long)Word.positionToNum("E")][Word.letterToNum("S")][(long)Word.positionToNum("S")] == 1980
      groupedAggs[Word.letterToNum("E")][(long)Word.positionToNum("E1")][Word.letterToNum("S")][(long)Word.positionToNum("S2")] == 47
  }

  Closure setResult = {
    result.set(it)
  }
}
