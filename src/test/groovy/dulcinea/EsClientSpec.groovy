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

    void "Basic index"(){
        when:
          esClient.index(new Word("mitten"), setResult)

        then:
          result.get()
          result.get().result.lowercase == "created"
    }

    void "Basic get"(){
        when:
          esClient.get(setResult)

        then:
          result.get()
    }

    Closure setResult = {
        result.set(it)
    }

}
