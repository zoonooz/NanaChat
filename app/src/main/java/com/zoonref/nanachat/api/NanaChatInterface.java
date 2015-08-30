package com.zoonref.nanachat.api;

import com.zoonref.nanachat.model.Friend;

import java.util.List;

import retrofit.http.GET;
import rx.Observable;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public interface NanaChatInterface {

    String API_URL = "http://private-061bd-nanachat.apiary-mock.com";

    @GET("/friend")
    Observable<List<Friend>> listFriends();

}
