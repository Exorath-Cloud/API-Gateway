package exorath.cloud.apigateway.data;

import com.google.gson.GsonBuilder;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Connor on 12/14/2016.
 */
public class MongoDBProvider implements DatabaseProvider {

    private MongoClient mongoClient;
    private MongoDatabase db;

    public MongoDBProvider(ServerAddress serverAddress, MongoCredential credential, String database) {
        mongoClient = new MongoClient(serverAddress, Arrays.asList(credential));
        db = mongoClient.getDatabase(database);
    }

    @Override
    public void addAPI(API api) {
        db.getCollection("apis").updateMany(new Document("name",api.name),Document.parse(new GsonBuilder().create().toJson(api)),new UpdateOptions().upsert(true));
    }

    @Override
    public List<API> getAPIs() {
        ArrayList<API> apis = new ArrayList<>();
        for (Document document : db.getCollection("apis").find()) {
            apis.add(new GsonBuilder().create().fromJson(document.toJson(), API.class));
        }
        return apis;
    }
}
