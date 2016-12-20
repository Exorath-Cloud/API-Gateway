package exorath.cloud.apigateway;

import io.vertx.core.Vertx;

/**
 * Created by Connor on 12/14/2016.
 */
public class Main {

    public static void main(String[] args) {
//        int port = Integer.parseInt(System.getenv("PORT"));
        int port = 8001;
        Service service = new Service(port);
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(service);
    }

}
