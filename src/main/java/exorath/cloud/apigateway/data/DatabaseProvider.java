package exorath.cloud.apigateway.data;

import java.util.List;

/**
 * Created by Connor on 12/20/2016.
 */
public interface DatabaseProvider {
    void addAPI(API api);
    List<API> getAPIs();
}
