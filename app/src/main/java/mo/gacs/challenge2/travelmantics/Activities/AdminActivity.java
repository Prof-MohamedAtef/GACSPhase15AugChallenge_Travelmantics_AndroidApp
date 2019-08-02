package mo.gacs.challenge2.travelmantics.Activities;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

        FirebaseUtil.openFbReference(MainRef);
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();
                clean();
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
        String title= txtTitle.getText().toString();
        String desc=txtDescription.getText().toString();
        String price=txtPrice.getText().toString();

        TravelDeal travelDeal=new TravelDeal(title, desc, price,"");
        mDatabaseReference.push().setValue(travelDeal);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.save_menu, menu);
        return true;
    }
}
