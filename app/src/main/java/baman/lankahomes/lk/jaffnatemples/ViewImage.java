package baman.lankahomes.lk.jaffnatemples;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;

import baman.lankahomes.lk.jaffnatemples.mainClasses.Domain;

public class ViewImage extends AppCompatActivity {


    Bitmap bitmap;
    ProgressDialog pDialog;

    Domain Api_url;
    public String domain;


    public String temple_image_name;
    ImageView detailImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Api_url = new Domain();
        domain = Api_url.get_main_domain();

        detailImage = (ImageView) findViewById(R.id.IV_View_image);


        temple_image_name = getIntent().getExtras().getString("temple_image_name");

        new LoadImage().execute(domain + "temple_images/" + temple_image_name );

    }





    // Async task to update temple image
    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ViewImage.this);
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
                detailImage.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(ViewImage.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }

}
