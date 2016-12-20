package exorath.cloud.apigateway;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpMethod;

/**
 * Created by Connor on 12/20/2016.
 */
public class RouteValidator implements Runnable {
    RouteMapper.Route route;

    RouteValidator(RouteMapper.Route route) {
        this.route = route;
    }

    @Override
    public void run() {

        HttpClientRequest req = null;

        Handler<HttpClientResponse> httpHandle = httpClientResponse -> {
            if (httpClientResponse.statusCode() == 404) {
                System.out.println("removed route (" + httpClientResponse.statusCode() + "): " + route.toString());
                Main.databaseProvider.removeRoute(route);
                Main.service.routeMapper.removeRoute(route);
            }else{
                System.out.println("Route is valid: " + route.toString());
            }
        };

        if(route.domain.contains(":")){
            req = Main.service.client.request(HttpMethod.valueOf(route.method), Integer.parseInt(route.domain.split(":")[1]), route.domain.split(":")[0], route.path, httpHandle);
        }else{
             req = Main.service.client.request(HttpMethod.valueOf(route.method),route.domain, route.path, httpHandle);
        }

        if(req != null){
            req.setTimeout(10000);
            req.exceptionHandler(throwable -> {
                System.out.println("removed route (" + throwable.getClass().getName() + "): " + route.toString());
                Main.databaseProvider.removeRoute(route);
                Main.service.routeMapper.removeRoute(route);
            });
            req.end();
        }

    }
}
