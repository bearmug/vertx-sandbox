package io.bearmug.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SimpleVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(new SimpleVerticle());
    }

    @Override
    public void start() throws Exception {
        getVertx()
                .createHttpServer()
                .requestHandler(req -> {
                    req.response().end(
                            "Hello from simple verticle at: " +
                                    System.currentTimeMillis(), event ->
                            log.info("event has been processed {}", event));})
                .listen(8080);
    }
}
