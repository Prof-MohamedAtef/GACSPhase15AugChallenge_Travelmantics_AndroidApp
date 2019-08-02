package mo.gacs.challenge2.travelmantics.Activities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import mo.gacs.challenge2.travelmantics.Adapter.DealAdapter;
import mo.gacs.challenge2.travelmantics.FirebaseUtil;
import mo.gacs.challenge2.travelmantics.Models.TravelDeal;
import mo.gacs.challenge2.travelmantics.R;

import static mo.gacs.challenge2.travelmantics.Activities.AdminActivity.MainRef;

public class ListActivity extends AppCompatActivity {

    ArrayList<TravelDeal> deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildListener;

    @BindView(R.id.rvDeals)
    RecyclerView rvDeals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.bind(this);

        final DealAdapter dealAdapter=new DealAdapter();
        rvDeals.setAdapter(dealAdapter);
        LinearLayoutManager dealsLayouManager=
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDeals.setLayoutManager(dealsLayouManager);
    }
}