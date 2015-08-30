package com.zoonref.nanachat.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zoonref.nanachat.R;
import com.zoonref.nanachat.model.Friend;
import com.zoonref.nanachat.model.Message;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public class FriendListAdapter extends RealmBaseAdapter<Friend> implements ListAdapter {

    private SimpleDateFormat mDateFormat;

    static class ViewHolder {
        @Bind(R.id.imageview) ImageView imageView;
        @Bind(R.id.name_textview) TextView nameTextView;
        @Bind(R.id.time_textview) TextView timeTextView;
        @Bind(R.id.message_textview) TextView messageTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public FriendListAdapter(Context context, RealmResults<Friend> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_main_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Friend item = realmResults.get(position);
        viewHolder.nameTextView.setText(item.getName());
        viewHolder.imageView.setImageBitmap(null);

        if (!TextUtils.isEmpty(item.getAvatar())) {
            Picasso.with(context).load(item.getAvatar()).into(viewHolder.imageView);
        }

        // get last message
        RealmResults<Message> messages = Realm.getInstance(context).where(Message.class)
                .equalTo("friend.id", item.getId())
                .findAllSorted("timestamp", true);

        Message lastMessage = messages.size() > 0 ? messages.last() : null;
        if (lastMessage != null) {
            viewHolder.messageTextView.setText(lastMessage.getMessage());
            viewHolder.timeTextView.setText(mDateFormat.format(lastMessage.getTimestamp()));
        }

        return convertView;
    }

    public RealmResults<Friend> getRealmResults() {
        return realmResults;
    }

}
