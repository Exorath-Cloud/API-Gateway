package exorath.cloud.apigateway;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import io.vertx.core.Vertx;

/**
 * Created by Connor on 12/14/2016.
 */
public class Main {

    public static Service service;
    static DatabaseProvider databaseProvider;

    public static void main(String[] args) {
//        int port = Integer.parseInt(System.getenv("PORT"));
//        String host = System.getenv("MONGO_HOST");
//        String username = System.getenv("MONGO_USER");
//        String database = System.getenv("MONGO_DATABASE");
//        String password = System.getenv("MONGO_PWD");
        String host = "ddkg.flynnhub.com:3162";
        String username = "068076029742ef69838b2eb38b55f772";
        String database = "b4d87ff9bbdfc11609c93ee311a59a2a";
        String password = "58cbe3cffa4a5561f5e9fde56000b61e";
        int port = 8000;
        Vertx vertx = Vertx.vertx();
        databaseProvider = new MongoDBProvider(new ServerAddress(host), MongoCredential.createCredential(username, database, password.toCharArray()), database);
        service = new Service(port);
        vertx.deployVerticle(service);
    }

}
