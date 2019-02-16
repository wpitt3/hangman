import dulcinea.HangmanVerticle
import io.vertx.core.Vertx
import io.vertx.ext.web.client.HttpResponse
import io.vertx.ext.web.client.WebClient
import spock.lang.Shared
import spock.lang.Specification
import spock.util.concurrent.BlockingVariable


class HangmanVerticleSpec extends Specification {

    @Shared Vertx vertx

    @Shared WebClient client

    Map<String, Closure> rests = [ get: { it.&get }, post: { it.&post }, put: { it.&put }, delete: { it.&delete }]

    def setupSpec() {
        vertx = Vertx.vertx()
        BlockingVariable deployed = new BlockingVariable(10)
        vertx.deployVerticle(HangmanVerticle.class.getName()) { deployResponse ->
            if (deployResponse.succeeded()) {
                deployed.set(true)
            }
        }
        assert deployed.get()

        client = WebClient.create(vertx)
    }

    def "Basic get"(){
        given:
            Map expected = [status: 200, body: [:]]

        when:
            Map response = call(rests.get, "/game", true, null)

        then:
            response == expected
    }

    def "Basic get for unknown"(){
        given:
          Map expected = [status: 404]

        when:
          Map response = call(rests.get, "/unknown", false, null)

        then:
          response == expected
    }

    def "Basic put"(){
        given:
          Map expected = [status: 200]

        when:
          Map response = call(rests.put, "/game", false, [:])

        then:
          response == expected
    }

    def "Basic post"(){
        given:
          Map expected = [status: 200]

        when:
          Map response = call(rests.post, "/game", false, [:])

        then:
          response == expected
    }

    def "Basic delete"(){
        given:
          Map expected = [status: 200]

        when:
          Map response = call(rests.delete, "/game", false, [:])

        then:
          response == expected
    }

    private Map call(Closure method, String uri, boolean responseBody, Map requestBody) {
        BlockingVariable responseWithStatus = new BlockingVariable(1)
        def handler = { ar ->
            if (ar.succeeded()) {
                HttpResponse response = ar.result()
                Map toReturn = [status: response.statusCode()]
                if( responseBody ) {
                    toReturn.body = response.bodyAsJsonObject().map
                }
                responseWithStatus.set(toReturn)
            }
        }
        if (requestBody == null ) {
            method(client)(8081, "localhost", uri).send(handler)
        } else {
            method(client)(8081, "localhost", uri).sendJson(requestBody, handler)
        }
        return responseWithStatus.get()
    }
}