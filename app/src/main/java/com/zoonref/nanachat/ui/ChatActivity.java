package com.zoonref.nanachat.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.picasso.Picasso;
import com.zoonref.nanachat.R;
import com.zoonref.nanachat.model.Friend;
import com.zoonref.nanachat.model.Message;
import com.zoonref.nanachat.ui.adapter.MessageAdapter;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class ChatActivity extends AppCompatActivity {

    public static final String INTENT_FRIEND_ID = "friend_id";

    @Bind(R.id.listview) ListView mListView;
    @Bind(R.id.message_edittext) EditText mMessageEditText;
    @Bind(R.id.send_button) Button mSendButton;

    private Realm mRealm;
    private MessageAdapter mAdapter;
    private Friend mFriend;

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
        ButterKnife.bind(this);

        mRealm = Realm.getInstance(this);

        Intent intent = getIntent();
        if (intent.hasExtra(INTENT_FRIEND_ID)) {

            // query friend
            mFriend = mRealm.where(Friend.class).equalTo("id", intent.getIntExtra(INTENT_FRIEND_ID, 0)).findFirst();
            // query related messages
            RealmResults<Message> messages = mRealm.where(Message.class)
                    .equalTo("friend.id", mFriend.getId())
                    .findAllSorted("timestamp", true);

            mAdapter = new MessageAdapter(this, messages, true);
            mListView.setAdapter(mAdapter);

            // set title
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
                getSupportActionBar().setCustomView(R.layout.action_bar_chat);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);

                ImageView imageView = (ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imageview);
                TextView textView = (TextView) getSupportActionBar().getCustomView().findViewById(R.id.textview);

                textView.setText(mFriend.getName());

                String avatar = mFriend.getAvatar();
                if (!TextUtils.isEmpty(avatar)) {
                    Picasso.with(this).load(avatar).into(imageView);
                }
            }

        } else {
            // cannot find friend
            finish();
        }

        // message events to enable or disable send button
        RxTextView.textChanges(mMessageEditText)
                .map(text -> !TextUtils.isEmpty(text))
                .subscribe(mSendButton::setEnabled);

        // when click send button
        RxView.clicks(mSendButton)
                .doOnNext(o -> createMessage(mMessageEditText.getText().toString(), true))
                .flatMap(v -> Observable.just("Hello" + mAdapter.getCount()).delay(3, TimeUnit.SECONDS))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> createMessage(o, false));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    // private

    private void createMessage(String message, boolean isSent) {
        Message msg = new Message();
        msg.setMessage(message);
        msg.setTimestamp(new Date());
        msg.setFriend(mFriend);
        msg.setIsSent(isSent);

        mRealm.beginTransaction();
        mRealm.copyToRealm(msg);
        mRealm.commitTransaction();

        if (isSent) {
            mMessageEditText.setText("");
        }

        mListView.smoothScrollToPosition(mAdapter.getCount() - 1);
    }
}
