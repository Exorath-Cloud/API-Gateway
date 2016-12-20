package exorath.cloud.apigateway;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

import java.util.ArrayList;

/**
 * Created by Connor on 12/20/2016.
 */
public class RouteMapper {

    ArrayList<Route> routes = new ArrayList<>();

    public Route getRouteByDomain(String domain, String method) {
        for (Route route : routes) {
            if (route.domain.equalsIgnoreCase(domain) && route.method.equalsIgnoreCase(method)) {
                return route;
            }
        }
        return null;
    }

    Route getRouteByPath(String path, String method) {
        for (Route route : routes) {
            if (route.path.equalsIgnoreCase(path) && route.method.equalsIgnoreCase(method)) {
                return route;
            }
        }
        return null;
    }

    public void addRoute(Route route) {
        if (getRouteByPath(route.path, route.method) == null) {
            routes.add(route);
        }
    }

    void removeRoute(Route route) {
        routes.remove(route);
    }

    void updateRouter(Router router) {
        for (Route route : routes) {
            router.route(HttpMethod.valueOf(route.method), route.path).handler(Main.service::rerouteRequest);
        }
    }

    public class Route {
        public String domain = "";
        public String path = "";
        public String method = "";

        public Route(String domain, String path, String method) {
            this.domain = domain;
            this.path = path;
            this.method = method;
        }

        @Override
        public String toString() {
            return method + " " + domain + path;
        }
    }

}
