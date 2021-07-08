package com.trile.walletnote.model;

public class ReturnData {
    int Result; //1-void success, 2- void fail
    String Message;

    public int getResult() {
        return Result;
    }

    public void setResult(int result) {
        Result = result;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
