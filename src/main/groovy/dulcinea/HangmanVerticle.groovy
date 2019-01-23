package dulcinea

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class HangmanVerticle extends AbstractVerticle {
    private static final String JSON_CONTENT = "application/json"
    private static final String CONTENT_TYPE = "content-type"

    void start() {
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/game").handler(startGame)
        router.put("/game").handler(performTurn)
        router.get("/game").handler(getStatus)

        vertx.createHttpServer().requestHandler({req ->
                router.accept(req)
    }).listen(8081)
        System.out.println("Started Verticle")
    }

    private Handler<RoutingContext> startGame = ( { req ->
        Map<String, Object> body = safelyGetBody(req)
        req.response()
                .setStatusCode(200)
                .putHeader(CONTENT_TYPE, JSON_CONTENT)
                .end("post")
    })

    private Handler<RoutingContext> performTurn = ( { req ->
        Map<String, Object> body = safelyGetBody(req)
        req.response()
        .setStatusCode(200)
        .putHeader(CONTENT_TYPE, JSON_CONTENT)
        .end("put")

    })

    private Handler<RoutingContext> getStatus = ( { req ->
        req.response()
            .setStatusCode(200)
            .putHeader(CONTENT_TYPE, JSON_CONTENT)
            .end("{}")
    })

    private Map<String, Object> safelyGetBody(RoutingContext req) {
        Map<String, Object> body = new HashMap<>()
        try {
            body = req.getBodyAsJson().getMap()
        } catch(Exception e) {
        }
        return body
    }
}