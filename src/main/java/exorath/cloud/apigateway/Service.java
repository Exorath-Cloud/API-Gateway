package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/19/2016.
 */

import com.google.gson.GsonBuilder;
import exorath.cloud.apigateway.data.API;
import exorath.cloud.apigateway.data.Route;
import exorath.cloud.apigateway.transactions.APIAddRequest;
import exorath.cloud.apigateway.transactions.APIAddResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service extends AbstractVerticle {

    private final int ROUTE_VAILD_LOOP = 10;
    private final int ROUTE_VAILD_THREAD = 6;
    HttpClient client;
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    int port;
    private Router router;
    private Router proxyRouter;
    public APIManager apiManager = new APIManager();

    Service(int port) {
        this.port = port;
    }

    @Override
    public void start() {
        setupRouter();
        setupHttpServer();
        setupUpdate();
    }

    private void setupUpdate() {
        scheduledExecutor.scheduleAtFixedRate(() -> {

        }, 0, ROUTE_VAILD_LOOP, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                scheduledExecutor.shutdown();
            }
        });
    }

    private void setupHttpServer(){
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        client = vertx.createHttpClient(new HttpClientOptions());
    }

    private void setupRouter(){
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/apigateway/add/").handler(this::addRoute);
        proxyRouter = Router.router(vertx);
        router.mountSubRouter("/", proxyRouter);
    }

    private void addRoute(RoutingContext routingContext) {
        HttpServerResponse res = routingContext.response();
        APIAddRequest routeAddRequest = new APIAddRequest(new GsonBuilder().create().fromJson(routingContext.getBodyAsString(), API.class));
        APIAddResponse routeAddResponse = routeAddRequest.process();
        res.setStatusCode(routeAddResponse.getStatus());
        res.end(routeAddResponse.getBody());
    }

    public void rerouteRequest(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        Route route = Main.service.apiManager.getRouteByPath(req.path(), req.method());
        HttpClientRequest proxy_req;
        Handler<HttpClientResponse> httpHandle = proxy_res -> {
            res.setChunked(true);
            res.headers().setAll(proxy_res.headers());
            res.setStatusCode(proxy_res.statusCode());
            proxy_res.handler(buffer -> res.write(buffer));
            proxy_res.endHandler(aVoid -> res.end());
        };
        if (route != null) {
            if (route.domain.contains(":")) {
                proxy_req = client.request(req.method(), Integer.parseInt(route.domain.split(":")[1]), route.domain.split(":")[0], req.uri(), httpHandle);
            } else {
                proxy_req = client.request(req.method(), route.domain, req.uri(), httpHandle);
            }
            if (proxy_req != null) {
                proxy_req.headers().setAll(req.headers());
                proxy_req.write(routingContext.getBody());
            }
        }
    }


}
