package exorath.cloud.apigateway;

import exorath.cloud.apigateway.data.API;
import exorath.cloud.apigateway.data.Route;
import io.vertx.core.http.HttpMethod;

import java.util.ArrayList;

/**
 * Created by Connor on 12/22/2016.
 */
public class APIManager {

    ArrayList<API> apis = new ArrayList<API>();

    public void  addAPI(API api){
        API oldapi = getAPI(api.name);
        if(oldapi == null){
            apis.add(api);
        }else{
            apis.remove(oldapi);
            apis.add(api);
        }
    }

    public API getAPI(String name){
        for(API api : apis){
            if(api.name.equalsIgnoreCase(name))
                return api;
        }
        return null;
    }

    public Route getRouteByPath(String path, HttpMethod method){
        for(API api : apis){
            for(Route route : api.routes){
                if(api.name.equalsIgnoreCase(path) && route.method.equals(method))
                    return route;
            }
        }
        return null;
    }

}
