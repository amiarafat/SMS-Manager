package com.itsolution.smsapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.itsolution.smsapp.models.Contacts;
import com.itsolution.smsapp.models.Groups;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Contacts> contactsList;

        public ContactListAdapter(Context context, ArrayList<Contacts> contactsList) {

        this.context = context;
        this.contactsList = contactsList;
    }

    @Override
    public int getCount() {
        return contactsList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactsList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.each_row_contact_item, parent, false);
        }

        Contacts contact = contactsList.get(position);
        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.tvContacts);


        textViewItemName.setText((position+1)+". "+contact.getName()+" ("+contact.getMobileNumber()+")");

        return convertView;
    }
}
