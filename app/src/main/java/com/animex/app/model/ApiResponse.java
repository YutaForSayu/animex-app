package com.animex.app.model;
import com.google.gson.annotations.SerializedName;
public class ApiResponse<T> {
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;
    @SerializedName("data") private T data;
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public boolean isSuccess() { return "success".equals(status); }
}
