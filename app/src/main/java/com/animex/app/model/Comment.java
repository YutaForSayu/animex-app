package com.animex.app.model;

import com.google.firebase.database.PropertyName;

public class Comment {
    private String id;       // Firebase push key
    private String username;
    private String message;
    private long   timestamp;

    // Required empty constructor for Firebase deserialization
    public Comment() {}

    public Comment(String username, String message) {
        this.username  = username;
        this.message   = message;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId()        { return id; }
    public void   setId(String id) { this.id = id; }

    public String getUsername()           { return username; }
    public void   setUsername(String v)   { this.username = v; }

    public String getMessage()            { return message; }
    public void   setMessage(String v)    { this.message = v; }

    public long   getTimestamp()          { return timestamp; }
    public void   setTimestamp(long v)    { this.timestamp = v; }
}
