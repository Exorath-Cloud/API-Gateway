package exorath.cloud.apigateway.transactions;

import com.google.gson.GsonBuilder;

import java.util.Date;

/**
 * Created by Connor on 12/19/2016.
 */
public class RouteAddResponse implements Response{

    int status;
    String errorMessage;

    RouteAddResponse(int status, String errorMessage) {
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
