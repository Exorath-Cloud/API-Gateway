package exorath.cloud.apigateway.transactions;

import exorath.cloud.apigateway.Main;
import exorath.cloud.apigateway.RouteMapper;

/**
 * Created by Connor on 12/19/2016.
 */
public class RouteAddRequest implements Request {

    RouteMapper.Route route;

    public RouteAddRequest(RouteMapper.Route route) {
        this.route = route;
    }

    @Override
    public RouteAddResponse process() {
        if (route != null && route.domain != null && route.path != null && route.method != null) {
            Main.service.routeMapper.addRoute(route);
            return new RouteAddResponse(200, "Success");
        } else {
            return new RouteAddResponse(400, "error parsing route");
        }
    }
}
