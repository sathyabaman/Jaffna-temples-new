package baman.lankahomes.lk.jaffnatemples;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
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

public class Temple_Details extends AppCompatActivity {

    Bitmap bitmap;
    ProgressDialog pDialog;
    ImageView temple_logo;
    TextView description;
    TextView templeName;
    TextView address;
    Button btn_directions;
    TextView templeType;
    TextView templeCoordinates;
    Button suggestAnEdit;
    Button add_temple;


    String from_city_shared;
    String fromLatLng_shared;
    String from_temple_type;
    String from_radius;
    String from_coordinates;
    String from_temple_name;
    String temple_coordinates;
    String temple_image_name;



    public static final String MY_PREFS_NAME = "templeSavedData";



    Domain Api_url;
    public String domain;
    String temple_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temple__details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Api_url = new Domain();
        domain = Api_url.get_main_domain();



        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        from_city_shared = prefs.getString("from_city", "No name defined");//"No name defined" is the default value.
        fromLatLng_shared = prefs.getString("fromLatLng", "No default"); //0 is the default value.
        from_temple_type = prefs.getString("temple_type", "No default");
        from_radius = prefs.getString("temple_radius", "No default");

        // getting temple type and coordinates fom the domain class
        if(!from_city_shared.equals("My GPS Location")){
            Api_url.Set_latng_City(from_city_shared);
            from_coordinates = Api_url.getFrom_Lat_LNG();
        }else {
            from_coordinates = fromLatLng_shared;
        }

       temple_logo = (ImageView) findViewById(R.id.IV_temple_logo);
       description = (TextView) findViewById(R.id.TV_Description);
       templeName = (TextView) findViewById(R.id.TV_title);
       address = (TextView) findViewById(R.id.TV_address);
       templeType = (TextView) findViewById(R.id.TV_templeType);
       templeCoordinates = (TextView) findViewById(R.id.TV_coordinates);
       btn_directions = (Button) findViewById(R.id.Btn_Directions);
       suggestAnEdit = (Button) findViewById(R.id.Btn_suggest_edit);
       add_temple = (Button) findViewById(R.id.Btn_temple_details);


        //get temple id from the intent came from view holder
        temple_id = getIntent().getExtras().getString("temple_id");


        new GetTempleDetails().execute(temple_id);



        btn_directions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), GetDirections.class);
                intent.putExtra("center", from_coordinates);
                intent.putExtra("radius", from_radius);
                intent.putExtra("temple_name", from_temple_name);
                intent.putExtra("temple_coordinates", temple_coordinates);
                startActivity(intent);
            }
        });

        suggestAnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), SuggestAnEdit.class);
                intent.putExtra("temple_id", temple_id);
                startActivity(intent);
            }
        });

        add_temple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), AddaTemple.class);
                startActivity(intent);
            }
        });

        temple_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getApplicationContext(), ViewImage.class);
                intent.putExtra("temple_image_name", temple_image_name);
                startActivity(intent);
            }
        });

    }















    // Async task to update temple image
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Temple_Details.this);
            pDialog.setMessage("Please wait. Loading data...");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                temple_logo.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(Temple_Details.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }


    // Async task to load temple details
    class GetTempleDetails extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {

           URL url = null;
            try {
                url = new URL(domain+"getTempleDetails.php");

                Map<String,Object> params = new LinkedHashMap<>();
                params.put("temple_id", arg0[0]);

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
                    JSONArray mJsonArray = null;
                    try {
                        mJsonArray = new JSONArray(value);
                        JSONObject mJsonObject = new JSONObject();
                        for (int i = 0; i < mJsonArray.length(); i++) {
                            mJsonObject = mJsonArray.getJSONObject(i);
                            String name = mJsonObject.getString("name");
                            String type = mJsonObject.getString("type");
                            String descr = mJsonObject.getString("description");
                            String latitude = mJsonObject.getString("latitude");
                            String longitude = mJsonObject.getString("longitude");
                            String add = mJsonObject.getString("address");
                            String image = mJsonObject.getString("image");

                            if(descr.equals("")){ descr = "No Description Provided."; }
                            String temple_type = Api_url.setTempleType(type);
                            String cord = latitude+","+longitude;
                            templeName.setText(name);
                            temple_coordinates = cord;

                            from_temple_name = name;
                            templeType.setText(temple_type);
                            address.setText(add);
                            description.setText(descr);
                            templeCoordinates.setText(cord);

                            temple_image_name = image;

                            new LoadImage().execute(domain + "temple_images/" + image );

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        }


    }




}
