package exorath.cloud.apigateway.transactions;

import com.google.gson.GsonBuilder;

/**
 * Created by Connor on 12/19/2016.
 */
public class APIAddResponse implements Response {

    int status;
    String errorMessage;

    APIAddResponse(int status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getBody() {
        return new GsonBuilder().create().toJson(this);
    }

    @Override
    public int getStatus() {
        return status;
    }

}
