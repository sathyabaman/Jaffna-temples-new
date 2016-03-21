package baman.lankahomes.lk.jaffnatemples;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import baman.lankahomes.lk.jaffnatemples.mainClasses.GPSTracker;

public class SearchOnMap extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    GPSTracker gps;

    public double center_lat;
    public double center_lng;

    public int from_location = 1;
    public int radius_KM;
    public int zoom_level;
    public String jsonValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_on_map);

        //get from intent
        String radius = getIntent().getExtras().getString("radius");
        jsonValue = getIntent().getExtras().getString("json_value");

        //Setting the intent radius
        String res[] = radius.split("\\s+");
        radius_KM = Integer.parseInt(res[0]);
        Log.i("Radius Km : ", String.valueOf(radius_KM));

        //set zoom level
        if(radius_KM < 3){
            zoom_level = 14;
        }else if (radius_KM > 2 && radius_KM < 6) {
            zoom_level = 12;
        } else{
            zoom_level = 11;
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //set center lat,lng here

//        double center_lat;
//        double center_lng;


        //get users GPS location
        show_my_gps_location();

        //Draw circle for the given radius
        drawcircle(radius_KM, center_lat, center_lng);

        try {
            decode_json(jsonValue);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void decode_json(String value) throws JSONException {

        JSONArray mJsonArray = new JSONArray(value);
        JSONObject mJsonObject = new JSONObject();
        for (int i = 0; i < mJsonArray.length(); i++) {
            mJsonObject = mJsonArray.getJSONObject(i);
            String id = mJsonObject.getString("id");
            String name = mJsonObject.getString("name");
            String description = mJsonObject.getString("description");
            String latitude = mJsonObject.getString("latitude");
            String Longitude = mJsonObject.getString("longitude");
            String image = mJsonObject.getString("image");


            String description2 = String.format("%.150s", description)+ "";
            int leng = description2.length();
            if(leng == 0){ description2 = "No descriptions provided."; }

            displaymarkers(Double.parseDouble(latitude), Double.parseDouble(Longitude), name);

        }
    }


    public void drawcircle(int rad, double lat, double lng){

        int value = rad*1000;

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(value)
                .strokeColor(Color.RED)
                        //.fillColor(Color.WHITE)
                .fillColor(Color.TRANSPARENT));
    }

    private void show_my_gps_location(){

        gps = new GPSTracker(SearchOnMap.this);

        // check if GPS enabled
        if(gps.canGetLocation()){

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            this.center_lat = latitude;
            this.center_lng = longitude;

            gotoLocation(latitude, longitude, zoom_level);

            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("My Location");
            mMap.addMarker(marker);

        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

    }

    private void gotoLocation(double lat, double lng,
                              float zoom) {

        Log.i("my location latitude : ", String.valueOf(lat));
        Log.i("my location longitude : ", String.valueOf(lng));

        LatLng ll = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);
    }

    public void displaymarkers(double lati, double longi, String templename){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(templename);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(marker);

    }



}
