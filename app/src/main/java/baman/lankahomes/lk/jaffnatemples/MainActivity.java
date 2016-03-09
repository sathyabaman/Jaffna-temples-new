package baman.lankahomes.lk.jaffnatemples;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    public Button btn_search;
    public Spinner spn_from;
    public Spinner spn_radius;
    public Spinner spn_temple_type;
    public Spinner spn_no_temple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_search = (Button) findViewById(R.id.btn_search);
        spn_from = (Spinner) findViewById(R.id.spinner_from);
        spn_radius = (Spinner) findViewById(R.id.spinner_radius);
        spn_temple_type = (Spinner) findViewById(R.id.spinner_temple_type);
        spn_no_temple = (Spinner) findViewById(R.id.spinner_no_of_temples);


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                // TODO Auto-generated method stub
                String from = spn_from.getSelectedItem().toString();
                String radius = spn_radius.getSelectedItem().toString();
                String templetype = spn_temple_type.getSelectedItem().toString();
                String nooftemples = spn_no_temple.getSelectedItem().toString();

                boolean isvalue = validate_form(from, radius, templetype, nooftemples);

                if (isvalue){
                    goToNextActivity();
                }

            }
        });

    }

    public void goToNextActivity(){
        Intent intent = new Intent(getApplicationContext(), SearchResult.class);
        intent.putExtra("from", spn_from.getSelectedItem().toString());
        intent.putExtra("radius", spn_radius.getSelectedItem().toString());
        intent.putExtra("temple_type", spn_temple_type.getSelectedItem().toString());
        intent.putExtra("no_of_temples", spn_no_temple.getSelectedItem().toString());
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

}
