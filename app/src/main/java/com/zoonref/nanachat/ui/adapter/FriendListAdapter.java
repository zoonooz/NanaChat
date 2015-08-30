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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.RealmBaseAdapter;
import io.realm.RealmResults;

/**
 * Created by zoonooz on 8/30/15 AD.
 */
public class FriendListAdapter extends RealmBaseAdapter<Friend> implements ListAdapter {

    static class ViewHolder {
        @Bind(R.id.imageview) ImageView imageView;
        @Bind(R.id.name_textview) TextView nameTextView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public FriendListAdapter(Context context, RealmResults<Friend> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
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

        return convertView;
    }

    public RealmResults<Friend> getRealmResults() {
        return realmResults;
    }

}
