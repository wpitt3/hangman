package dulcinea

import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
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

  void "Aggregation"() {
    given:

      Closure x = { result.set(it) }


    when:
      esClient.someBetterName(x, "A")

    then:
      result.get()
  }

  Closure setResult = {
    result.set(it)
  }
}
