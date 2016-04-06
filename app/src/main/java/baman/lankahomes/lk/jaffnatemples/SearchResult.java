package baman.lankahomes.lk.jaffnatemples;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import baman.lankahomes.lk.jaffnatemples.mainClasses.Domain;


public class SearchResult extends AppCompatActivity{


    public String from;
    public String radius;
    public String temple_type;
    public String no_of_temples;
    public List<Data> data = null;
    public String json_value;
    public String from_lat_lng;
    public String imei = "";
    public String deviceName = "";
    public String manufacturer= "";
    public String deviceAssignedName = "";

    Domain Api_url;
    public String domain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //get from intent

        from = getIntent().getExtras().getString("from");
        radius = getIntent().getExtras().getString("radius");
        temple_type = getIntent().getExtras().getString("temple_type");
        no_of_temples = getIntent().getExtras().getString("no_of_temples");
        from_lat_lng = getIntent().getExtras().getString("from_lat_lng");

        Api_url = new Domain();
        domain = Api_url.get_main_domain();



        setPhoneDetails();


        try {
            data = fill_with_data(from, radius, temple_type, no_of_temples, from_lat_lng);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        launchRingDialog();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        Button btn_onmap = (Button) findViewById(R.id.btn_search_on_map);


        btn_onmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SearchOnMap.class);
                intent.putExtra("from", from);
                intent.putExtra("radius", radius);
                intent.putExtra("temple_type", temple_type);
                intent.putExtra("no_of_temples", no_of_temples);
                intent.putExtra("json_value", json_value);
                startActivity(intent);
            }
        });


    }

    public void setPhoneDetails(){
        imei = getIMEI(this);
        deviceAssignedName = getPhoneName();
        deviceName = android.os.Build.MODEL;
        manufacturer = Build.MANUFACTURER;
    }

    public List<Data> launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(SearchResult.this, "Please wait ...", "Loading data...", true);
        ringProgressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }} catch (Exception e) {}
                ringProgressDialog.dismiss();
            }}).start();
        return data;
    }

    public String getIMEI(Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public String getPhoneName()
    {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName = myDevice.getName();
        return deviceName;
    }


    public List<Data> fill_with_data(String from, String radius, String type, String count, String from_lat_lng) throws ExecutionException, InterruptedException, JSONException {

        List<Data> data = new ArrayList<>();

        String value = new GetTemples().execute(from, radius, type, count, from_lat_lng, imei, deviceName, manufacturer, deviceAssignedName).get();
        json_value = value;
        JSONArray mJsonArray = new JSONArray(value);
        JSONObject mJsonObject = new JSONObject();
        for (int i = 0; i < mJsonArray.length(); i++) {
            mJsonObject = mJsonArray.getJSONObject(i);
            String temple_id = mJsonObject.getString("id");
            String name = mJsonObject.getString("name");
            String description = mJsonObject.getString("description");
            String address = mJsonObject.getString("address");
            String latitude = mJsonObject.getString("latitude");
            String Longitude = mJsonObject.getString("longitude");
            String image = mJsonObject.getString("image");



            //adding to data array list
            data.add(new Data(temple_id, name, address, R.drawable.ic_action_movie));
        }

        return data;
    }





















    class GetTemples extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

            URL url = null;
            try {
                url = new URL(domain+"gettTemples.php");

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("from", arg0[0]);
                params.put("radius", arg0[1]);
                params.put("type", arg0[2]);
                params.put("count", arg0[3]);
                params.put("from_lat_lng", arg0[4]);
                params.put("imei", arg0[5]);
                params.put("deviceName", arg0[6]);
                params.put("manufacturer", arg0[7]);
                params.put("deviceAssignedName", arg0[8]);


                StringBuilder postData = new StringBuilder();
                for (Map.Entry<String,Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDataBytes = postData.toString().getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char) c);
                String responseString = sb.toString();



                return responseString;


            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }
    }



}




