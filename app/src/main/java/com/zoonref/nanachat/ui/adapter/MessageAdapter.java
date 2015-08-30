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
import com.zoonref.nanachat.model.Message;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public class MessageAdapter extends RealmBaseAdapter<Message> implements ListAdapter {

    static class ViewHolder {
        @Bind(R.id.imageview) ImageView imageView;
        @Bind(R.id.message_textview) TextView messageTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MessageAdapter(Context context, RealmResults<Message> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        Message item = realmResults.get(position);

        if (convertView == null) {
            if (item.isSent()) {
                convertView = inflater.inflate(R.layout.activity_chat_item_right, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.activity_chat_item_left, parent, false);
            }
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.messageTextView.setText(item.getMessage());
        viewHolder.imageView.setImageBitmap(null);

        if (!item.isSent()) {
            String avatar = item.getFriend().getAvatar();
            if (!TextUtils.isEmpty(avatar)) {
                Picasso.with(context).load(avatar).into(viewHolder.imageView);
            }
        } else {
            // set my avatar
            viewHolder.imageView.setImageResource(R.drawable.myself);
        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message item = realmResults.get(position);
        return item.isSent() ? 0 : 1;
    }
}
