package mo.gacs.challenge2.travelmantics.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import mo.gacs.challenge2.travelmantics.Activities.AdminActivity;
import mo.gacs.challenge2.travelmantics.Activities.UserActivity;
import mo.gacs.challenge2.travelmantics.FirebaseUtil;
import mo.gacs.challenge2.travelmantics.Models.TravelDeal;
import mo.gacs.challenge2.travelmantics.R;

import static mo.gacs.challenge2.travelmantics.Activities.AdminActivity.MainRef;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{


    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;
    private ImageView imageDeal;
    public static String Deal_KEY="Deal";
    private String BACKGROUND_COLOR="#b4dce4";
    UserActivity activity;
    private String FAKE_PATH=null;


    public DealAdapter(UserActivity caller) {
        FirebaseUtil.openFbReference(MainRef, caller);
        activity=caller;
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        deals=FirebaseUtil.mDeals;
        mChildListener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal travelDeal=dataSnapshot.getValue(TravelDeal.class);
                Log.d("Returned Data", travelDeal.getTitle());
                travelDeal.setId(dataSnapshot.getKey());
                deals.add(travelDeal);
                notifyItemInserted(deals.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addChildEventListener(mChildListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context=viewGroup.getContext();
        View itemview= LayoutInflater.from(context)
                .inflate(R.layout.tv_row, viewGroup, false);
        return new DealViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder dealViewHolder, int i) {
         TravelDeal deal=deals.get(i);
         dealViewHolder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDescription;
        TextView tvPrice;
        TextView tvTitle;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.tvTitle);
            tvDescription=(TextView)itemView.findViewById(R.id.tvDescription);
            tvPrice=(TextView)itemView.findViewById(R.id.tvPrice);
            imageDeal=(ImageView)itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);

        }

        public void bind(TravelDeal travelDeal){
            tvTitle.setText(travelDeal.getTitle());
            tvDescription.setText(travelDeal.getDescription());
            tvPrice.setText(travelDeal.getPrice());
            showImage(travelDeal.getImageUrl());
        }

        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Log.d("Click:", String.valueOf(position));
            TravelDeal selectedDeal=deals.get(position);
            Intent intent=new Intent(v.getContext(), AdminActivity.class);
            intent.putExtra(Deal_KEY,selectedDeal);
            v.getContext().startActivity(intent);
        }


        private void showImage(String url){
            if (url!=null&&url.isEmpty()==false){
                Picasso.with(imageDeal.getContext())
                        .load(url)
                        .resize(250, 250)
                        .centerCrop()
                        .into(imageDeal);
            }else {
                FAKE_PATH = "PlaPlaPla";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    imageDeal.setBackground(imageDeal.getContext().getDrawable(R.drawable.bg_image));
                } else {
                    //
                }
            }
        }
    }
}