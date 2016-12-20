package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/19/2016.
 */

import com.google.gson.GsonBuilder;
import exorath.cloud.apigateway.transactions.RouteAddRequest;
import exorath.cloud.apigateway.transactions.RouteAddResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Service extends AbstractVerticle {

    private final int ROUTE_VAILD_LOOP = 10;
    private final int ROUTE_VAILD_THREAD = 6;
    public RouteMapper routeMapper = new RouteMapper();
    HttpClient client;
    ExecutorService executor;
    ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);
    int port;
    private Router router;
    private Router proxyRouter;

    Service(int port) {
        routeMapper = Main.databaseProvider.loadRouteMapper();
        if (routeMapper.routes == null) {
            routeMapper.routes = new ArrayList<>();
        }
        this.port = port;
    }

    @Override
    public void start() {
        setupRouter();
        setupHttpServer();
        setupVaildiator();
    }

    private void setupVaildiator() {
        executor = Executors.newFixedThreadPool(ROUTE_VAILD_THREAD);
        scheduledExecutor.scheduleAtFixedRate(() -> {
            for (RouteMapper.Route route : routeMapper.routes) {
                executor.execute(new RouteValidator(route));
            }
            routeMapper.updateRouter(proxyRouter);
        }, 0, ROUTE_VAILD_LOOP, TimeUnit.SECONDS);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                executor.shutdown();
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
        RouteAddRequest routeAddRequest = new RouteAddRequest(new GsonBuilder().create().fromJson(routingContext.getBodyAsString(), RouteMapper.Route.class));
        RouteAddResponse routeAddResponse = routeAddRequest.process();
        res.setStatusCode(routeAddResponse.getStatus());
        res.end(routeAddResponse.getBody());
        Main.databaseProvider.saveRouteMapper(routeMapper);
    }

    void rerouteRequest(RoutingContext routingContext) {
        HttpServerRequest req = routingContext.request();
        HttpServerResponse res = routingContext.response();
        RouteMapper.Route route = routeMapper.getRouteByPath(req.path(), req.method().toString());
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
