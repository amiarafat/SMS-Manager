package com.itsolution.smsapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itsolution.smsapp.models.Contacts;
import com.itsolution.smsapp.models.SMS;

import java.util.ArrayList;

public class SMSLogAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SMS> logList;

    public SMSLogAdapter(Context context, ArrayList<SMS> logList) {

        this.context = context;
        this.logList = logList;
    }


    @Override
    public int getCount() {
        return logList.size();
    }

    @Override
    public Object getItem(int i) {
        return logList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.each_row_contact_item, parent, false);
        }


        SMS sms = logList.get(i);
        // get the TextView for item name and item description
        TextView tvLogText = (TextView)
                convertView.findViewById(R.id.tvContacts);

        tvLogText.setTextSize(12);
        tvLogText.setHeight(100);
        tvLogText.setPadding(8,0,0,0);
        tvLogText.setText("Name: "+sms.getGroupName()+"\nTotal Sms: "+sms.getSmsCount()+"\nTime: "+sms.getTime());


        return convertView;
    }
}
