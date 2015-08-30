package com.zoonref.nanachat.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.zoonref.nanachat.R;
import com.zoonref.nanachat.api.NanaChat;
import com.zoonref.nanachat.model.Friend;
import com.zoonref.nanachat.ui.adapter.FriendListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.refresh_layout) SwipeRefreshLayout mRefreshLayout;
    @Bind(R.id.listview) ListView mListView;

    private Realm mRealm;
    private FriendListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // setup
        mRealm = Realm.getInstance(this);
        mAdapter = new FriendListAdapter(this, mRealm.where(Friend.class).findAll(), true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Friend friend = mAdapter.getItem(i);
                openChat(friend);
            }
        });

        mRefreshLayout.setRefreshing(true);
        mRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    @Override
    public void onRefresh() {
        // flow for get list of friends
        NanaChat.getInstance(this).getApi().listFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mRefreshLayout.setRefreshing(false))
                .doOnError(error -> mRefreshLayout.setRefreshing(false))
                .subscribe(this::saveFriends);
    }

    // private

    private void saveFriends(List<Friend> friends) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(friends);
        mRealm.commitTransaction();
    }

    private void openChat(Friend friend) {
        startActivity(ChatActivity.createIntent(this, friend));
    }
}
