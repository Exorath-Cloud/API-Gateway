package exorath.cloud.apigateway.data;

import io.vertx.core.http.HttpMethod;

/**
 * Created by Connor on 12/22/2016.
 */
public class Route {
    public String domain;
    String path;
    public HttpMethod method;
    Permission permission;

    private enum Permission {
        LOCAL,GLOBAL
    }
}