package baman.lankahomes.lk.jaffnatemples;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import baman.lankahomes.lk.jaffnatemples.mainClasses.Domain;

public class SuggestAnEdit extends AppCompatActivity {

    public String temple_id;
    ProgressDialog pDialog;

    TextView templeID;
    EditText name;
    EditText title;
    EditText description;
    Button submit;

    Domain Api_url;
    public String domain;

    public String imei;
    public String deviceAssignedName;
    public String deviceName;
    public String manufacturer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest_an_edit);

        temple_id = getIntent().getExtras().getString("temple_id");

        templeID = (TextView) findViewById(R.id.TV_temple_id);
        name = (EditText) findViewById(R.id.Et_name);
        title = (EditText) findViewById(R.id.Et_title);
        description = (EditText) findViewById(R.id.Et_description);
        submit = (Button) findViewById(R.id.Btn_submit);


        templeID.setText("Temple ID : TEMJF"+temple_id);

        Api_url = new Domain();
        domain = Api_url.get_main_domain();

        imei = getIMEI(this);
        deviceAssignedName = getPhoneName();
        deviceName = android.os.Build.MODEL;
        manufacturer = Build.MANUFACTURER;


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                final String txt_name = name.getText().toString();
                final String txt_title = title.getText().toString();
                final String txt_description = description.getText().toString();

                    if(!txt_name.equals("")  && !txt_title.equals("") &&
                            !txt_description.equals("") ){

                        new SubmitTempleSugessions().execute(txt_name, txt_title, txt_description, imei, deviceAssignedName, deviceName, manufacturer, temple_id);

                    }else {
                        show_error_message("Let's leave no fields blank.", "Error");
                    }

            }
        });

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









    // Async task to load temple details
    class SubmitTempleSugessions extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SuggestAnEdit.this);
            pDialog.setMessage("Please wait. Submitting Suggestions...");
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... arg0) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(domain+"templeSugessions.php");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("txt_name", arg0[0]));
                nameValuePairs.add(new BasicNameValuePair("txt_title", arg0[1]));
                nameValuePairs.add(new BasicNameValuePair("txt_description", arg0[2]));
                nameValuePairs.add(new BasicNameValuePair("imei", arg0[3]));
                nameValuePairs.add(new BasicNameValuePair("deviceAssignedName", arg0[4]));
                nameValuePairs.add(new BasicNameValuePair("deviceName", arg0[5]));
                nameValuePairs.add(new BasicNameValuePair("manufacturer", arg0[6]));
                nameValuePairs.add(new BasicNameValuePair("temple_id", arg0[7]));
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


            show_error_message("We have received your suggestions.", "Thank you");
            name.setText(null);
            title.setText(null);
            description.setText(null);
            pDialog.dismiss();
        }


    }



}
