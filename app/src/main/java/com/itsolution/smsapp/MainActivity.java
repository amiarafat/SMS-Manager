package com.itsolution.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.itsolution.smsapp.models.Contacts;
import com.itsolution.smsapp.models.Groups;
import com.itsolution.smsapp.models.SMS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 99;
    ArrayList<Groups> groupList;
    ArrayList<Contacts> contactList;
    ArrayList<String> contactNumList = new ArrayList<>();
    ArrayList<String> groupNameList;
    Button btnGroupList;
    TextView tvContactCount;
    Button btnViewContacts;
    TextView etTextInput;
    Button btnSendSMS;
    Button btnShowLog;
    TextView tvSMSCharCount;

    //ArrayList<String> numberList =  new ArrayList<>(Arrays.asList("01520103480","01520103480","01520103480"));
    private String message="";
    //AlertDialog dialog;
    String selectedGroup;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private String MyPREFERENCES = "sms_log_pref";
    private String LOG_KEY = "sms_log_key";
    private Set<SMS> contactSet;
    private ArrayList<SMS> smsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        groupList = new ArrayList<>();
        groupNameList = new ArrayList<>();
        contactList = new ArrayList<>();
        btnGroupList = findViewById(R.id.btnGroupList);
        tvContactCount = findViewById(R.id.tvContactCount);
        btnViewContacts = findViewById(R.id.btnViewContacts);
        etTextInput = findViewById(R.id.etTextInput);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnShowLog = findViewById(R.id.btnShowLog);
        tvSMSCharCount = findViewById(R.id.tvSMSCharCount);

        btnViewContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSelectedContacts();
            }
        });

        btnShowLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showLogDialog();
            }
        });

        etTextInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                int length = charSequence.length();
                tvSMSCharCount.setText(length+"/180");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnSendSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(etTextInput.getText().toString())){
                    Toast.makeText(getApplicationContext(), "Please Write a sms to send.", Toast.LENGTH_SHORT).show();
                }else {




                    if(contactList.size()>0){

                        message = etTextInput.getText().toString();
                        checkSMSPermission();

                    }else {

                        Toast.makeText(getApplicationContext(), "Please Select a group.", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });

        getAllGroups();

        btnGroupList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shwoGroupList();
            }
        });
    }

    private void shwoGroupList(){

        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setTitle("Select Group");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.group_item_layout);


        ListView lvGroupList = dialog.findViewById(R.id.lvGroupList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,groupNameList);
        lvGroupList.setAdapter(adapter);


        lvGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                selectedGroup = groupNameList.get(i);
                btnGroupList.setText(selectedGroup);
                dialog.cancel();


                getAllContacts(groupList.get(i).getGroupId());
            }
        });

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancelDialog);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void getAllContacts(int groupId) {
        contactList = new ArrayList<>();
        contactList.clear();
        contactNumList.clear();

        StringRequest request = new StringRequest(Request.Method.GET, ApiConstants.CONTACT_LIST_API + groupId, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("contacts::",response);

                try {
                    JSONArray jArray = new JSONArray(response);

                    for (int i=0; i<jArray.length(); i++){
                        JSONObject obj = jArray.getJSONObject(i);
                        Log.d("name::", obj.getString("name"));

                        Contacts c = new Contacts();
                        c.setId(obj.getInt("id"));
                        c.setName(obj.getString("name"));
                        c.setMobileNumber(obj.getString("mobile"));
                        contactList.add(c);

                        Log.d("mobile::", obj.getString("mobile"));
                        contactNumList.add(obj.getString("mobile"));
                        //contactNumList.add("01816810643");
                    }

                    Log.d("size::",contactNumList+"");
                    tvContactCount.setText(contactList.size()+ " Contacts");


                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);

    }

    private void getAllGroups(){
        Log.d("group::","response");
        groupList.clear();
        groupNameList.clear();

        final RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, ApiConstants.GROUP_LIST_API, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("group::",response);

                try {
                    JSONArray jArray = new JSONArray(response);

                    for (int i=0; i<jArray.length(); i++){
                        JSONObject obj = jArray.getJSONObject(i);
                        Log.d("name::", obj.getString("grp_name"));
                        Groups group = new Groups();
                        group.setGroupId(obj.getInt("id"));
                        group.setGroupName(obj.getString("grp_name"));
                        groupNameList.add(obj.getString("grp_name"));
                        groupList.add(group);
                    }

                    Log.d("size::",groupNameList.size()+"");


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");

                return params;
            }
        };
        SMSApp.getInstance().addToRequestQueue(request);

    }


    void checkSMSPermission(){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }else {

            //startService();
            startSendingSMS();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("log","This is log");

                    //startService();
                    startSendingSMS();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    private void showSelectedContacts(){

        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.group_item_layout);


        TextView tvTitle = dialog.findViewById(R.id.titleTV);
        tvTitle.setText("Selected Contacts");
        ListView lvGroupList = dialog.findViewById(R.id.lvGroupList);

        /*ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,contactNumList);
        lvGroupList.setAdapter(adapter);*/
        ContactListAdapter adapter =  new ContactListAdapter(this,contactList);
        lvGroupList.setAdapter(adapter);

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancelDialog);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void showLogDialog(){

        ArrayList<SMS> sList = new ArrayList<>();

        if(sharedpreferences.contains(LOG_KEY)){
            String serializedObject = sharedpreferences.getString(LOG_KEY, null);

            if (serializedObject != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<SMS>>(){}.getType();
                sList = gson.fromJson(serializedObject, type);
                Log.d("list::",sList.size()+"");
                Log.d("list::",sList.toString());
            }


        }


        final Dialog dialog = new Dialog(this,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.group_item_layout);


        TextView tvTitle = dialog.findViewById(R.id.titleTV);
        tvTitle.setText("SMS LOG");
        ListView lvGroupList = dialog.findViewById(R.id.lvGroupList);


        SMSLogAdapter adapter =  new SMSLogAdapter(this,sList);
        lvGroupList.setAdapter(adapter);

        Button dialogCancelButton = (Button) dialog.findViewById(R.id.btnCancelDialog);
        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }


    BroadcastReceiver sendBroadcastReceiver, deliveryBroadcastReciever;
    ArrayList<String> dReport = new ArrayList<>();
    int itemCount = 0;

    public void startSendingSMS(){



        smsSendingStarted();

    }

    public void smsSendingStarted(){
        sendBroadcastReceiver = new SentReceiver();
        deliveryBroadcastReciever = new DeliverReceiver();

        for (int i = 0; i<contactNumList.size(); i++) {
            final int finalI = i;

            sendSMS(finalI);
            Log.d("number:",contactNumList.get(finalI));

            //sleeping(1000);

        }

        saveSentData(selectedGroup,contactNumList.size());
    }

    private void saveSentData(String selectedGroup, int size) {

        smsList = new ArrayList<>();

        if(sharedpreferences.contains(LOG_KEY)){
            String serializedObject = sharedpreferences.getString(LOG_KEY, null);

            if (serializedObject != null) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<SMS>>(){}.getType();
                smsList = gson.fromJson(serializedObject, type);
            }

        }

        SMS sms = new SMS();
        sms.setGroupName(selectedGroup);
        sms.setSmsCount(size);
        sms.setTime(new SimpleDateFormat("dd-MM-yy HH:mm a", Locale.getDefault()).format(new Date()));

        smsList.add(sms);
        Gson gson2 = new Gson();
        String json = gson2.toJson(smsList);

        editor.putString(LOG_KEY, json);
        editor.commit();

    }

    private void sendSMS(int numPos) {

        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));

        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(String.valueOf(contactNumList.get(numPos)), null,message , sentPI, deliveredPI);
    }

    private void sleeping(long n) {
        try {
            Thread.sleep(n);
        } catch (InterruptedException e) {
        }
    }

    class DeliverReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:

                    Toast.makeText(getBaseContext(), "sms_delivered", Toast.LENGTH_SHORT).show();

                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(getBaseContext(), "sms_not_delivered",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }

    class SentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(getBaseContext(), "sms_sent", Toast.LENGTH_SHORT)
                            .show();


                    dReport.add(arg1.getAction());
                    itemCount++;
                    //progress.setMessage(itemCount+"/"+contactNumList.size()+" SMS Sent");
                    //dialog.setTitle("SMS Sending On Progress \n("+selectedGroup+")");
                    //dialog.setMessage(itemCount+"/"+contactNumList.size()+" SMS Sent");

                    Log.d("items::",dReport.size()+"");

                    if(itemCount == contactNumList.size()){
                        Log.d("totalItems::",dReport.size()+"");
                        //progress.dismiss();
                        //dialog.dismiss();

                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("SMS Sending")
                                .setMessage(itemCount+"/"+contactNumList.size()+" SMS Sent Successfully to \n"+selectedGroup)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                                .show();
                        itemCount=0;
                        unregisterReceiver(sendBroadcastReceiver);
                        unregisterReceiver(deliveryBroadcastReciever);
                    }


                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    Toast.makeText(getBaseContext(), "Generic failure",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    Toast.makeText(getBaseContext(), "No service",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT)
                            .show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    Toast.makeText(getBaseContext(), "Radio off",
                            Toast.LENGTH_SHORT).show();
                    break;
            }

        }
    }


}