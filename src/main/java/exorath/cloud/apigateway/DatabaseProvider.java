package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/20/2016.
 */
public interface DatabaseProvider {
    void saveRouteMapper(RouteMapper routeMapper);

    RouteMapper loadRouteMapper();

    void removeRoute(RouteMapper.Route route);
}
