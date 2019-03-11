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

    Closure setResult = {
        result.set(it)
    }

}
