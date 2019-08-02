package mo.gacs.challenge2.travelmantics.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import mo.gacs.challenge2.travelmantics.FirebaseUtil;
import mo.gacs.challenge2.travelmantics.Models.TravelDeal;
import mo.gacs.challenge2.travelmantics.R;

import static mo.gacs.challenge2.travelmantics.Activities.AdminActivity.MainRef;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.DealViewHolder>{


    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;


    public DealAdapter() {
        FirebaseUtil.openFbReference(MainRef);
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

    public class DealViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle=(TextView)itemView.findViewById(R.id.tvTitle);
        }

        public void bind(TravelDeal travelDeal){
            tvTitle.setText(travelDeal.getTitle());
        }
    }
}
