package baman.lankahomes.lk.jaffnatemples;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

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
import baman.lankahomes.lk.jaffnatemples.mainClasses.GPSTracker;


public class MainActivity extends AppCompatActivity {

    public Button btn_search;
    public ImageButton hinduaffirs;
    public Spinner spn_from;
    public Spinner spn_radius;
    public Spinner spn_temple_type;
    public Spinner spn_no_temple;
    public String from_lat_lng = "nolatlng";
    public String json_val;
    private GoogleMap mMap;

    GPSTracker gps;

    public static final String MY_PREFS_NAME = "templeSavedData";

    Domain Api_url;
    public String domain ;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_search = (Button) findViewById(R.id.btn_search);
        spn_from = (Spinner) findViewById(R.id.spinner_from);
        spn_radius = (Spinner) findViewById(R.id.spinner_radius);
        spn_temple_type = (Spinner) findViewById(R.id.spinner_temple_type);
        spn_no_temple = (Spinner) findViewById(R.id.spinner_no_of_temples);
        hinduaffirs = (ImageButton) findViewById(R.id.IB_hinduReligious);

        Api_url = new Domain();
        domain = Api_url.get_main_domain();


        //go to hindu religious affairs website
        hinduaffirs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Uri uri = Uri.parse("http://www.hindudept.gov.lk/web/index.php?lang=ta"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

            }});

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // TODO Auto-generated method stub
                final String from = spn_from.getSelectedItem().toString();
                final String radius = spn_radius.getSelectedItem().toString();
                final String templetype = spn_temple_type.getSelectedItem().toString();
                final String nooftemples = spn_no_temple.getSelectedItem().toString();


                if(haveNetworkConnection()){

                            if(from.equals("My GPS Location")){

                                get_my_gps_location();
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }


                                if(!from_lat_lng.equals("nolatlng")){
                                    boolean isvalue = validate_form(from, radius, templetype, nooftemples);
                                    if (isvalue){

                                                try {
                                                    json_val = new GetTemplesCount().execute(from, radius, templetype, nooftemples, from_lat_lng).get();
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                } catch (ExecutionException e) {
                                                    e.printStackTrace();
                                                }
                                        Log.e("before activity", json_val);
                                                int result_temple = Integer.parseInt(json_val);
                                        Log.e("before activity", String.valueOf(result_temple));
                                                if(result_temple > 0){

                                                    //saving data to shared preferences
                                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                                    editor.putString("from_city", from);
                                                    editor.putString("fromLatLng", from_lat_lng);
                                                    editor.putString("temple_type", templetype);
                                                    editor.putString("temple_radius", radius);
                                                    editor.commit();



                                                    goToNextActivity();
                                                } else {
                                                    show_error_message("Let's adjust the distance radius or temple type and search again.", "No temples found!");
                                                }

                                    }
                                }
                            }else{
                                boolean isvalue = validate_form(from, radius, templetype, nooftemples);
                                if (isvalue){
                                    // get result count


                                    try {
                                        json_val = new GetTemplesCount().execute(from, radius, templetype, nooftemples, from_lat_lng).get();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("before activity", json_val);
                                    int result_temple = Integer.parseInt(json_val);
                                    Log.e("before activity", String.valueOf(result_temple));
                                    if(result_temple > 0){



                                        //saving data to shared preferences
                                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                        editor.putString("from_city", from);
                                        editor.putString("fromLatLng", from_lat_lng);
                                        editor.putString("temple_type", templetype);
                                        editor.putString("temple_radius", radius);
                                        editor.commit();



                                        goToNextActivity();
                                    } else {
                                        show_error_message("Let's adjust the distance radius  or temple type and search again.", "No temples found!");
                                    }





                                }
                            }
                }else {
                    show_error_message("Let's check out internet connection.", "No Connectivity");
                }





            }
        });

    }




    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void get_my_gps_location(){

        gps = new GPSTracker(MainActivity.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            this.from_lat_lng = String.valueOf(latitude)+ ","+String.valueOf(longitude);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }


    public void goToNextActivity(){
        Intent intent = new Intent(getApplicationContext(), SearchResult.class);
        intent.putExtra("from", spn_from.getSelectedItem().toString());
        intent.putExtra("radius", spn_radius.getSelectedItem().toString());
        intent.putExtra("temple_type", spn_temple_type.getSelectedItem().toString());
        intent.putExtra("no_of_temples", spn_no_temple.getSelectedItem().toString());
        intent.putExtra("from_lat_lng", from_lat_lng.toString());

        startActivity(intent);
    }

    public boolean validate_form(String from, String radius, String templetype, String nooftemples){

        if(from.equals("Please Select")){
            show_error_message("Please select the from location", "Error!");
            return false;
        }else if(radius.equals("Please Select")){
            show_error_message("Please select the radius kilometers", "Error!");
            return false;
        }else if(templetype.equals("Please Select")){
            show_error_message("Please select the temple type", "Error!");
            return false;
        }else if(nooftemples.equals("Please Select")){
            show_error_message("Please select the number of temples", "Error!");
            return false;
        }else {
            return true;
        }
    }

    private void show_error_message(String data, String title){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(data)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


































    class GetTemplesCount extends AsyncTask<String, Void, String> {



        @Override
        protected String doInBackground(String... arg0) {

            URL url = null;
            try {
                url = new URL(domain+"countTemples.php");

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("from", arg0[0]);
                params.put("radius", arg0[1]);
                params.put("type", arg0[2]);
                params.put("count", arg0[3]);
                params.put("from_lat_lng", arg0[4]);

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
