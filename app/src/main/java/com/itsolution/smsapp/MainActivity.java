package com.itsolution.smsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
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
import com.itsolution.smsapp.models.Contacts;
import com.itsolution.smsapp.models.Groups;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 99;
    List<Groups> groupList;
    List<Contacts> contactList;
    List<String> groupNameList;
    Button btnGroupList;
    TextView tvContactCount;
    Button btnViewContacts;
    TextView etTextInput;
    Button btnSendSMS;
    TextView tvSMSCharCount;

    List<String> numberList = new ArrayList<>(Arrays.asList("01520103480","01520103480","01520103480","01520103480","01520103480"));
    private String message="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        groupList = new ArrayList<>();
        groupNameList = new ArrayList<>();
        contactList = new ArrayList<>();
        btnGroupList = findViewById(R.id.btnGroupList);
        tvContactCount = findViewById(R.id.tvContactCount);
        btnViewContacts = findViewById(R.id.btnViewContacts);
        etTextInput = findViewById(R.id.etTextInput);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        tvSMSCharCount = findViewById(R.id.tvSMSCharCount);

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


                    message = etTextInput.getText().toString();
                    checkSMSPermission();
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

        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Select Group");
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.group_item_layout);


        ListView lvGroupList = dialog.findViewById(R.id.lvGroupList);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,groupNameList);
        lvGroupList.setAdapter(adapter);


        lvGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                btnGroupList.setText(groupNameList.get(i));
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
                        c.setMobileNumber(obj.getInt("mobile"));

                        contactList.add(c);

                    }

                    Log.d("size::",contactList.size()+"");
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
        queue.add(request);

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

            startSMSSending();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("log","This is log");

                    startSMSSending();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }


    void  startSMSSending(){

        for (int i = 0; i<numberList.size(); i++) {
            final int finalI = i;

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(numberList.get(finalI), null,"I Love You" , null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();

            Log.d("sent SMS to: ",+finalI+ " "+ numberList.get(finalI));

            sleep(1500);

        }
    }

    private void sleep(long n) {
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
        }
    }
}