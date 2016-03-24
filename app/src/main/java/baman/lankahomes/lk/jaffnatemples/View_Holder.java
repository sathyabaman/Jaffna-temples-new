package baman.lankahomes.lk.jaffnatemples;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by baman on 3/4/16.
 */
public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

    CardView cv;
    TextView title;
    TextView description;
    ImageView imageView;
    TextView temple_id;
    private final Context context;

    View_Holder(View itemView) {
        super(itemView);
        context = itemView.getContext();
        cv = (CardView) itemView.findViewById(R.id.cardView);
        title = (TextView) itemView.findViewById(R.id.title);
        description = (TextView) itemView.findViewById(R.id.description);
        imageView = (ImageView) itemView.findViewById(R.id.imageView);
        temple_id = (TextView) itemView.findViewById(R.id.temple_id);

        //added by me
        temple_id.setOnClickListener(this);
        title.setOnClickListener(this);
        description.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    //added by me

    public ImageView getImageView() {
        return imageView;
    }
    public TextView getTitle() {
        return title;
    }
    public TextView getDescription() {
        return description;
    }

    @Override
    public void onClick(View v) {
        Log.i("positon-of-clicked-item", String.valueOf(getAdapterPosition()));
        Log.i("positon-of-clicked-item", String.valueOf(title.getText()));
        Log.i("positon-of-clicked-item", String.valueOf(temple_id.getText()));



        final Intent intent;
        intent =  new Intent(context, Temple_Details.class);
        intent.putExtra("temple_id", String.valueOf(temple_id.getText()));
        context.startActivity(intent);
    }



}