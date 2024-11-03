package main.Models.TCP;

import main.Enums.RequestType;

public class Request {
    private RequestType requestType;
    private String requestMessage;

    public Request(){};
    public Request (RequestType requestType, String requestMessage )
    {
        this.setRequestType(requestType);
        this.setRequestMessage(requestMessage);
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(String requestMessage) {
        this.requestMessage = requestMessage;
    }
}
