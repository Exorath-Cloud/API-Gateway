package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/19/2016.
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.HashMap;
import java.util.Map;

public class Service extends AbstractVerticle{

    int port;

    Service(int port){
        this.port = port;
    }

    private Map<String, JsonObject> products = new HashMap<>();
    Router router;
    HttpClient client;
    @Override
    public void start() {
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/apigateway/add/").handler(this::addRoute);
        router.post("/auth/").handler(this::proxyConnection);
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        client = vertx.createHttpClient(new HttpClientOptions());
    }

    private void addRoute(RoutingContext routingContext) {

    }

    private void proxyConnection(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        HttpClientRequest proxy_req = client.request(req.method(), 8000, "localhost", req.uri(), new Handler<HttpClientResponse>() {
            @Override
            public void handle(HttpClientResponse proxy_res) {
                res.setChunked(true);
                res.headers().setAll(proxy_res.headers());
                res.setStatusCode(proxy_res.statusCode());
                proxy_res.handler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        res.write(buffer);
                    }
                });
                proxy_res.endHandler(new Handler<Void>() {
                    @Override
                    public void handle(Void aVoid) {
                        res.end();
                    }
                });
            }
        });
        proxy_req.headers().setAll(req.headers());
        proxy_req.write(routingContext.getBody());
    }
}
