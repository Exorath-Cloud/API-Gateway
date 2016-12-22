package exorath.cloud.apigateway.transactions;

import exorath.cloud.apigateway.APIManager;
import exorath.cloud.apigateway.Main;
import exorath.cloud.apigateway.Service;
import exorath.cloud.apigateway.data.API;

/**
 * Created by Connor on 12/19/2016.
 */
public class APIAddRequest implements Request {

    API api;

    public APIAddRequest(API api) {
        this.api = api;
    }

    @Override
    public APIAddResponse process() {
        if (api != null && api.name != null) {
            Main.service.apiManager.addAPI(api);
            return new APIAddResponse(200, "APi added successfully");
        } else {
            return new APIAddResponse(400, "error parsing route");
        }
    }
}
