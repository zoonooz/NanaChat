package com.zoonref.nanachat.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zoonref.nanachat.R;
import com.zoonref.nanachat.model.Friend;

public class ChatActivity extends AppCompatActivity {

    public static final String INTENT_FRIEND_ID = "friend_id";

    /**
     * Get intent to navigate to this activity
     * @param context context
     * @param friend friend object
     * @return intent
     */
    public static Intent createIntent(Context context, Friend friend) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(INTENT_FRIEND_ID, friend.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
    }

}
