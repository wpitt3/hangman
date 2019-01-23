import dulcinea.HangmanVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import spock.lang.Shared
import spock.lang.Specification

class HangmanVerticleSpec extends Specification {

    @Shared Vertx vertx

    def setupSpec() {
        vertx = Vertx.vertx()

//        DeploymentOptions options = new DeploymentOptions()
//                .setConfig(new JsonObject().put("http.port", 8002)
//        );

        vertx.deployVerticle(HangmanVerticle.class.getName(), options ) { deployResponse ->
            assert deployResponse.succeeded()
//            verticleId = deployResponse.result
        }
        Thread.sleep 1000
    }


    def "Basic get"(){
        given:
            def response = ""

        when:
            int x = 0

        then:
          x == 0
    }
}