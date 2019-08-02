package mo.gacs.challenge2.travelmantics.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import mo.gacs.challenge2.travelmantics.FirebaseUtil;
import mo.gacs.challenge2.travelmantics.R;
import mo.gacs.challenge2.travelmantics.Models.TravelDeal;

import static mo.gacs.challenge2.travelmantics.Adapter.DealAdapter.Deal_KEY;

public class AdminActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    public static String MainRef="travelmantics-26b72";

    @BindView(R.id.EditName)
    EditText txtTitle;

    @BindView(R.id.EditPrice)
    EditText txtPrice;

    @BindView(R.id.EditDesc)
    EditText txtDescription;
    private TravelDeal deal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

//        FirebaseUtil.openFbReference(MainRef, this);
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;

        Intent intent=getIntent();
        TravelDeal travelDeal=(TravelDeal)intent.getSerializableExtra(Deal_KEY);
        if (travelDeal==null){
            travelDeal=new TravelDeal();
        }
        this.deal=travelDeal;
        txtTitle.setText(travelDeal.getTitle());
        txtDescription.setText(travelDeal.getDescription());
        txtPrice.setText(travelDeal.getPrice());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete:
                deleteDeal();
                Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle( txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());
        if (deal.getId()==null){
            mDatabaseReference.push().setValue(deal);
        }else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
    }

    private void deleteDeal(){
        if (deal==null){
            Toast.makeText(getApplicationContext(), getString(R.string.save_before_del), Toast.LENGTH_LONG).show();
            return;
        }
        mDatabaseReference.child(deal.getId()).removeValue();
    }

    private void backToList(){
        Intent intent=new Intent(this, ListActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        if (FirebaseUtil.isAdmin){
            menu.findItem(R.id.delete).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableEditText(true);
        }else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
        }
        return true;
    }

    private void enableEditText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }
}
