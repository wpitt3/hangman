package dulcinea

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class HangmanVerticle extends AbstractVerticle {
    private static final String JSON_CONTENT = "application/json"
    private static final String CONTENT_TYPE = "content-type"
    private HangmanGame hangmanGame
    private boolean ready = false

    void start() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
        Router router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.post("/game").handler(startGame)
        router.put("/game/:letter").handler(performTurn)
        router.get("/game/:letter").handler(performTurn)
        router.delete("/game").handler(removeTurn)
//        router.get("/game").handler(getStatus)

        hangmanGame = new HangmanGame()
        hangmanGame.setup {
            ready = true
        }

        vertx.createHttpServer().requestHandler({req ->
                router.accept(req)
    }).listen(8081)
        System.out.println("Started Verticle")
    }

    private Handler<RoutingContext> startGame = ( { req ->
        hangmanGame.start()
        req.response()
                .setStatusCode(200)
                .putHeader(CONTENT_TYPE, JSON_CONTENT)
                .end("restarted")
    })

    private Handler<RoutingContext> performTurn = ( { req ->
        String letter = req.pathParam("letter")
        if (letter && letter.length() == 1) {
            hangmanGame.addLetter(letter.toUpperCase(), { response ->
                req.response()
                    .setStatusCode(200)
                    .putHeader(CONTENT_TYPE, JSON_CONTENT)
                    .end(response)
            })
        } else [
            req.response()
                .setStatusCode(400)
                .putHeader(CONTENT_TYPE, JSON_CONTENT)
                .end()
        ]

    })

    private Handler<RoutingContext> removeTurn = ( { req ->
        Map<String, Object> body = safelyHandleBody(req)
        req.response()
            .setStatusCode(200)
            .putHeader(CONTENT_TYPE, JSON_CONTENT)
            .end("{}")
    })

    private Handler<RoutingContext> getStatus = ( { req ->
        req.response()
            .setStatusCode(200)
            .putHeader(CONTENT_TYPE, JSON_CONTENT)
            .end("{}")
    })

    private Map<String, Object> safelyHandleBody(RoutingContext req) {
        Map<String, Object> body = new HashMap<>()
        try {
            body = req.getBodyAsJson().getMap()
        } catch(Exception e) {
        }
        return body
    }
}