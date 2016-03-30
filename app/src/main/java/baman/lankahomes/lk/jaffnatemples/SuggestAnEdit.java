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

            URL url = null;
            try {
                url = new URL(domain+"templeSugessions.php");

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("txt_name", arg0[0]);
                params.put("txt_title", arg0[1]);
                params.put("txt_description", arg0[2]);
                params.put("imei", arg0[3]);
                params.put("deviceAssignedName", arg0[4]);
                params.put("deviceName", arg0[5]);
                params.put("manufacturer", arg0[6]);
                params.put("temple_id", arg0[7]);



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
