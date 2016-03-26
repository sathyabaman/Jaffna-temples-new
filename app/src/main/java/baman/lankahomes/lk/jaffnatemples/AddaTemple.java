package baman.lankahomes.lk.jaffnatemples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import baman.lankahomes.lk.jaffnatemples.mainClasses.Domain;
import baman.lankahomes.lk.jaffnatemples.mainClasses.GPSTracker;

public class AddaTemple extends AppCompatActivity {

    EditText txt_temple_name;
    EditText txt_temple_address;
    EditText txt_latitude;
    EditText txt_longitude;
    EditText txt_description;
    Button   my_coordinates;
    Button   submit;
    ProgressDialog pDialog;
    Spinner spn_temple_type;

    GPSTracker gps;
    Domain Api_url;
    public String domain ;


    public String imei;
    public String deviceAssignedName;
    public String deviceName;
    public String manufacturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adda_temple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_temple_name = (EditText) findViewById(R.id.ET_temple_Name);
        txt_temple_address = (EditText) findViewById(R.id.ET_temple_address);
        txt_latitude        = (EditText) findViewById(R.id.ET_latitude);
        txt_longitude       = (EditText) findViewById(R.id.ET_longitude);
        txt_description     = (EditText) findViewById(R.id.ET_description);
        my_coordinates = (Button) findViewById(R.id.BTN_myCoordinates);
        submit = (Button) findViewById(R.id.BTN_frm_submit);
        spn_temple_type = (Spinner) findViewById(R.id.SPN_Temple_type);

        //getting the domain to post values
        Api_url = new Domain();
        domain = Api_url.get_main_domain();

        imei = getIMEI(this);
        deviceAssignedName = getPhoneName();
        deviceName = android.os.Build.MODEL;
        manufacturer = Build.MANUFACTURER;

        my_coordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                get_my_gps_location();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final String name = txt_temple_name.getText().toString();
                final String address = txt_temple_address.getText().toString();
                final String latitude = txt_latitude.getText().toString();
                final String longitude = txt_longitude.getText().toString();
                final String description = txt_description.getText().toString();
                final String templetype = spn_temple_type.getSelectedItem().toString();

                if(!name.equals("")  && !address.equals("") &&
                        !latitude.equals("")&&!longitude.equals("")&&
                        !description.equals("")&& !templetype.equals("Please Select")){

                    new submitNewTemples().execute(name, address, latitude, longitude, description,
                            imei, deviceAssignedName, deviceName, manufacturer, templetype);

                }else {
                    show_error_message("Let's leave no fields blank.", "Error");
                }

            }
        });


    }


    private void get_my_gps_location() {

        gps = new GPSTracker(AddaTemple.this);

        // check if GPS enabled
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            txt_latitude.setText(String.valueOf(latitude));
            txt_longitude.setText(String.valueOf(longitude));
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

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















    // Async task to load temple details
    class submitNewTemples extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AddaTemple.this);
            pDialog.setMessage("Please wait. Submitting Temple...");
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(domain+"addNewTemple.php");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("name", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("address", arg0[1]));
                nameValuePairs.add(new BasicNameValuePair("latitude", arg0[2]));
                nameValuePairs.add(new BasicNameValuePair("longitude", arg0[3]));
                nameValuePairs.add(new BasicNameValuePair("description", arg0[4]));
                nameValuePairs.add(new BasicNameValuePair("imei", arg0[5]));
                nameValuePairs.add(new BasicNameValuePair("deviceAssignedName", arg0[6]));
                nameValuePairs.add(new BasicNameValuePair("deviceName", arg0[7]));
                nameValuePairs.add(new BasicNameValuePair("manufacturer", arg0[8]));
                nameValuePairs.add(new BasicNameValuePair("templetype", arg0[9]));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                String responseString = EntityUtils.toString(entity, "UTF-8");

                return responseString;
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
            return null;
        }


        protected void onPostExecute(String value) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            show_error_message("Temple submitted successfully.", "Thank you");
            txt_temple_name.setText(null);
            txt_temple_address.setText(null);
            txt_latitude.setText(null);
            txt_longitude.setText(null);
            txt_description.setText(null);
            pDialog.dismiss();
        }


    }


}
