package com.zoonref.nanachat.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.zoonref.nanachat.R;
import com.zoonref.nanachat.api.NanaChat;
import com.zoonref.nanachat.model.Friend;
import com.zoonref.nanachat.ui.adapter.FriendListAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {

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

        // when item is clicked, open chat
        RxAdapterView.itemClicks(mListView).subscribe(i -> {
            Friend friend = mAdapter.getItem(i);
            startActivity(ChatActivity.createIntent(this, friend));
        });

        // when refreshing
        RxSwipeRefreshLayout.refreshes(mRefreshLayout)
                .flatMap(v -> NanaChat.getInstance(this).getApi().listFriends())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(error -> mRefreshLayout.setRefreshing(false))
                .subscribe(friends -> {
                    saveFriends(friends);
                    mRefreshLayout.setRefreshing(false);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-load from server when resume
        NanaChat.getInstance(this).getApi().listFriends()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnCompleted(() -> mRefreshLayout.setRefreshing(false))
                .doOnError(error -> mRefreshLayout.setRefreshing(false))
                .subscribe(this::saveFriends);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    // private

    private void saveFriends(List<Friend> friends) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(friends);
        mRealm.commitTransaction();
    }
}
