package baman.lankahomes.lk.jaffnatemples;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

import baman.lankahomes.lk.jaffnatemples.mainClasses.GMapV2Direction;

public class GetDirections extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public String center_cordinates;
    public String radius;
    public String temple_name;
    public String temple_coordinates;

    Button viewOnMap;

    GMapV2Direction md;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_directions);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        viewOnMap = (Button) findViewById(R.id.Btn_onGoogleMap);

        center_cordinates = getIntent().getExtras().getString("center");
        String tmp_radius = getIntent().getExtras().getString("radius");
        temple_name = getIntent().getExtras().getString("temple_name");
        temple_coordinates = getIntent().getExtras().getString("temple_coordinates");


        String[] tempp_radius = tmp_radius.split(" ");
        radius = tempp_radius[0];


        // View on Google map
        viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?   saddr=" + center_cordinates + "&daddr=" + temple_coordinates));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });


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
        String[] location = center_cordinates.split(",");
        Double cen_latitude = Double.parseDouble(location[0]);
        Double cen_longitude = Double.parseDouble(location[1]);
        float zoom = 14;



        String[] location2 = temple_coordinates.split(",");
        Double To_latitude = Double.parseDouble(location2[0]);
        Double To_longitude = Double.parseDouble(location2[1]);

        // Add a marker in Sydney and move the camera
        LatLng centerloc = new LatLng(cen_latitude, cen_longitude);
        mMap.addMarker(new MarkerOptions().position(centerloc).title("My Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centerloc));

        LatLng ll = new LatLng(cen_latitude, cen_longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
        mMap.moveCamera(update);




        drawcircle(Integer.parseInt(radius), cen_latitude, cen_longitude);

        draw_road_directions(cen_latitude, cen_longitude, To_latitude, To_longitude);

        displaymarkers(To_latitude, To_longitude, temple_name);

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


    public void displaymarkers(double lati, double longi, String templename){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(templename);
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_icon));
        //marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(marker);

    }

    public void draw_road_directions(double lati1, double longi1, double lat2, double long2){
        double lat1 = Double.parseDouble(String.valueOf(lati1));
        double long1 = Double.parseDouble(String.valueOf(longi1));

        LatLng fromPosition = new LatLng(lat1, long1);
        LatLng toPosition = new LatLng(lat2, long2);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        md = new GMapV2Direction();
        mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        //location for opening the camera
        LatLng coordinates = new LatLng(lat1, long1);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));

        //mMap.addMarker(new MarkerOptions().position(fromPosition).title("Start"));
        // mMap.addMarker(new MarkerOptions().position(toPosition).title("End"));

        Document doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);
        int duration = md.getDurationValue(doc);
        String distance = md.getDistanceText(doc);
        String start_address = md.getStartAddress(doc);
        String copy_right = md.getCopyRights(doc);

        ArrayList<LatLng> directionPoint = md.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.DKGRAY);

        for(int i = 0 ; i < directionPoint.size() ; i++) {
            rectLine.add(directionPoint.get(i));
        }

        mMap.addPolyline(rectLine);
    }
}
