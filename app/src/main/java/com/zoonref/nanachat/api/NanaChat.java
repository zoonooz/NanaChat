package com.zoonref.nanachat.api;

import android.content.Context;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.realm.RealmObject;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public class NanaChat {

    private static NanaChat mInstance = null;

    private Context mContext;
    private NanaChatInterface mApi;

    public NanaChat(Context context) {

        // custom convertor to use with realm object
        // https://realm.io/docs/java/latest/#other-libraries
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        // Set up service
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(NanaChatInterface.API_URL)
                .setConverter(new GsonConverter(gson))
                .build();
        mApi = restAdapter.create(NanaChatInterface.class);
        mContext = context;
    }

    // singleton accessor
    public static NanaChat getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NanaChat(context);
        }
        return mInstance;
    }

    public NanaChatInterface getApi() {
        return mApi;
    }
}
