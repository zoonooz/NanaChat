package com.zoonref.nanachat.model;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public class Message extends RealmObject {

    private String message;
    private Date timestamp;
    private boolean isSent;

    private Friend friend;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setIsSent(boolean isSent) {
        this.isSent = isSent;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }
}
