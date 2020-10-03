package com.itsolution.smsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itsolution.smsapp.models.Groups;

import java.util.List;

public class GroupAdapter extends ArrayAdapter<String> {

    LayoutInflater flater;

    List<String> groupNameList;
    Activity activity;

    public GroupAdapter(Activity context,int resource, List<String> groupNameList ) {
        super(context,resource);

        this.groupNameList = groupNameList;
        flater = context.getLayoutInflater();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View rowview = flater.inflate(R.layout.group_item_layout,null,true);
        TextView txtTitle = (TextView) rowview.findViewById(R.id.title);
        txtTitle.setText(groupNameList.get(position));

        return rowview;
    }
}
