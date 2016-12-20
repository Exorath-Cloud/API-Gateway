package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/19/2016.
 */

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import exorath.cloud.apigateway.transactions.RouteAddRequest;
import exorath.cloud.apigateway.transactions.RouteAddResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service extends AbstractVerticle {

    final int ROUTE_VAILD_LOOP = 10;
    final int ROUTE_VAILD_THREAD = 6;
    public RouteMapper routeMapper = new RouteMapper();
    public Router router;
    public HttpClient client;
    ExecutorService executor;
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    int port;

    Service(int port) {
        routeMapper = Main.databaseProvider.loadRouteMapper();
        if (routeMapper.routes == null) {
            routeMapper.routes = new ArrayList<>();
        }
        this.port = port;
    }

    @Override
    public void start() {
        router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.post("/apigateway/add/").handler(this::addRoute);
        router.post("/auth/").handler(this::proxyConnection);
        vertx.createHttpServer().requestHandler(router::accept).listen(port);
        client = vertx.createHttpClient(new HttpClientOptions());
        executor = Executors.newFixedThreadPool(ROUTE_VAILD_THREAD);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            for (RouteMapper.Route route : routeMapper.routes) {
                executor.execute(new RouteValidator(route));
            }
        }, 0, ROUTE_VAILD_LOOP, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdown();
                scheduledExecutor.shutdown();
            }
        });
    }

    private void addRoute(RoutingContext routingContext) {
        HttpServerResponse res = routingContext.response();
        RouteAddRequest routeAddRequest = new RouteAddRequest(new GsonBuilder().create().fromJson(routingContext.getBodyAsString(), RouteMapper.Route.class));
        RouteAddResponse routeAddResponse = routeAddRequest.process();
        res.end(routeAddResponse.getBody());
        res.setStatusCode(routeAddResponse.getStatus());
        Main.databaseProvider.saveRouteMapper(routeMapper);
    }

    private void proxyConnection(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        System.out.println(req.method());
        System.out.println(req.uri());

        HttpClientRequest proxy_req = client.request(req.method(), 8001, "localhost", req.uri(), proxy_res -> {
            res.setChunked(true);
            res.headers().setAll(proxy_res.headers());
            res.setStatusCode(proxy_res.statusCode());
            proxy_res.handler(buffer -> res.write(buffer));
            proxy_res.endHandler(aVoid -> res.end());
        });
        proxy_req.headers().setAll(req.headers());
        proxy_req.write(routingContext.getBody());
    }


}
