package exorath.cloud.apigateway;

/**
 * Created by Connor on 12/20/2016.
 */
public interface DatabaseProvider {
    public void saveRouteMapper(RouteMapper routeMapper);

    public RouteMapper loadRouteMapper();

    public void removeRoute(RouteMapper.Route route);
}
