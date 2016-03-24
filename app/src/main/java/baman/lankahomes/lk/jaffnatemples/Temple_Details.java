package baman.lankahomes.lk.jaffnatemples;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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



    String from_city_shared;
    String fromLatLng_shared;
    String from_temple_type;
    String from_radius;
    String from_coordinates;


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

        // secting temple type and coordinates fom the domain class


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




        //get temple id from the intent came from view holder
        temple_id = getIntent().getExtras().getString("temple_id");


        new GetTempleDetails().execute(temple_id);

        new LoadImage().execute("http://static1.squarespace.com/static/55e6d5f9e4b08d00248f3aba/t/56164beae4b0c10192a2e435/1444308253273/IMG_0266.JPG");

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
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(domain+"getTempleDetails.php");

            try {
                // Add your data
                List<BasicNameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("temple_id", arg0[0]));
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

                            if(descr.equals("")){ descr = "No Description Provided."; }
                            String temple_type = Api_url.setTempleType(type);
                            String cord = latitude+","+longitude;
                            templeName.setText(name);
                            templeType.setText(temple_type);
                            address.setText(add);
                            description.setText(descr);
                            templeCoordinates.setText(cord);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
        }


    }




}
