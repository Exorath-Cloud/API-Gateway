package exorath.cloud.apigateway;

import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Connor on 12/14/2016.
 */
public class MongoDBProvider implements DatabaseProvider {

    private MongoClient mongoClient;
    private MongoDatabase db;

    MongoDBProvider(ServerAddress serverAddress, MongoCredential credential, String database) {
        mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));
        db = mongoClient.getDatabase(database);
    }

    @Override
    public void saveRouteMapper(RouteMapper routeMapper) {
        for (RouteMapper.Route route : routeMapper.routes) {
            Document document = Document.parse(new GsonBuilder().create().toJson(route));
            db.getCollection("routes").insertOne(document);
        }
    }

    @Override
    public RouteMapper loadRouteMapper() {
        Iterator<Document> routes = db.getCollection("routes").find().iterator();
        RouteMapper routeMapper = new RouteMapper();
        while (routes.hasNext()) {
            RouteMapper.Route route = new GsonBuilder().create().fromJson(routes.next().toJson(), RouteMapper.Route.class);
            if (route != null && route.domain != null && route.path != null) {
                routeMapper.addRoute(route);
            }
        }
        return routeMapper;
    }

    @Override
    public void removeRoute(RouteMapper.Route route) {
        db.getCollection("routes").deleteOne(Document.parse(new GsonBuilder().create().toJson(route)));
    }
}
