package exorath.cloud.apigateway;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import exorath.cloud.apigateway.data.DatabaseProvider;
import exorath.cloud.apigateway.data.MongoDBProvider;
import io.vertx.core.Vertx;

/**
 * Created by Connor on 12/14/2016.
 */
public class Main {

    public static Service service;
    static DatabaseProvider databaseProvider;

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv("PORT"));
        String host = System.getenv("MONGO_HOST");
        String username = System.getenv("MONGO_USER");
        String database = System.getenv("MONGO_DATABASE");
        String password = System.getenv("MONGO_PWD");
        Vertx vertx = Vertx.vertx();
        databaseProvider = new MongoDBProvider(new ServerAddress(host), MongoCredential.createCredential(username, database, password.toCharArray()), database);
        service = new Service(port);
        vertx.deployVerticle(service);
    }

}
