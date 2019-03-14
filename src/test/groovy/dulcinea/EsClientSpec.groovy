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
                          esClient.findAggregations([Word.positionToNum("L4")], [Word.letterToNum("M") * -1], setResult)
                      })
                  })
              })
          })

        then:
          println result.get().aggs[0].aggs
          result.get()
          result.get().total == 3
          result.get().aggs[0].key == Word.positionToNum("A1")
          result.get().aggs[0].count == 3
          result.get().aggs[0].aggs[0].key == Word.positionToNum("A1")
          result.get().aggs[0].aggs[1].key == Word.positionToNum("L4")
          result.get().aggs[0].aggs[1].count == 3
          result.get().aggs[0].aggs[2].key == Word.positionToNum("E5")
          result.get().aggs[0].aggs[2].count == 3
          result.get().aggs[0].aggs[3].key == Word.positionToNum("C0")
          result.get().aggs[0].aggs[3].count == 2
    }

    Closure setResult = {
        result.set(it)
    }

}
