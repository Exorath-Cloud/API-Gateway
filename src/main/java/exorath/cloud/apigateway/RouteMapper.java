package exorath.cloud.apigateway;

import java.util.ArrayList;

/**
 * Created by Connor on 12/20/2016.
 */
public class RouteMapper {

    ArrayList<Route> routes = new ArrayList<Route>();

    public Route getRouteByDomain(String domain, String method) {
        for (Route route : routes) {
            if (route.domain.equalsIgnoreCase(domain) && route.method.equalsIgnoreCase(method)) {
                return route;
            }
        }
        return null;
    }

    public Route getRouteByPath(String path, String method) {
        for (Route route : routes) {
            if (route.path.equalsIgnoreCase(path) && route.method.equalsIgnoreCase(method)) {
                return route;
            }
        }
        return null;
    }

    public void addRoute(Route route) {
        if (getRouteByDomain(route.domain, route.method) == null || getRouteByPath(route.path, route.method) == null) {
            routes.add(route);
        }
    }

    public void removeRoute(Route route) {
        routes.remove(route);
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
