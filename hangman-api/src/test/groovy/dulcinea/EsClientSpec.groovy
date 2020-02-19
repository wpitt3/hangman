package dulcinea

import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable

class EsClientSpec extends Specification {

    @Shared
    EsClient esClient

    BlockingVariable result

    def setupSpec() {
        BlockingVariable deployed = new BlockingVariable(10)
        esClient = new EsClient("test")
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

    void "Basic indexTest"(){
        when:
          esClient.delete("MITTEN", {
              esClient.index(new Word("mitten"), {
                  esClient.get(setResult)
              })
          })

        then:
          result.get().hits.collect{ it.id }.contains("MITTEN")
    }

    void "Basic getWithAggregation"(){
        when:
          esClient.index(new Word("castle"), {
              esClient.index(new Word("cattle"), {
                  esClient.index(new Word("battle"), {
                      esClient.index(new Word("mantle"), {
                          esClient.findAggregations(["L4"], ["M"], setResult)
                      })
                  })
              })
          })

        then:
          result.get()
          result.get().total == 3
          result.get().aggs[0].key == "A1"
          result.get().aggs[0].count == 3
          result.get().aggs[0].aggs[1].key == "E5"
          result.get().aggs[0].aggs[1].count == 3
    }

    Closure setResult = {
        result.set(it)
    }

}
