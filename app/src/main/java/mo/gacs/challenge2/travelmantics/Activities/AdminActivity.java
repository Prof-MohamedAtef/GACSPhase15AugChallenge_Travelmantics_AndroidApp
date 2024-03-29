package mo.gacs.challenge2.travelmantics.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

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
    private String title;
    private String description;
    private String price;
    private boolean filled;
    private String KEY_TITLE="KEY_TITLE";
    private String KEY_DESCRIP="KEY_DESCRIP";
    private String KEY_PRICE="KEY_PRICE";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==INTENT_REQUEST_CODE&&resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            final StorageReference ref=FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(AdminActivity.this, getApplicationContext().getString(R.string.upload_success), Toast.LENGTH_LONG).show();
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String Url=uri.toString();
                            String pictureName=taskSnapshot.getStorage().getPath();
                            deal.setImageUrl(Url);
                            deal.setImageName(pictureName);
                            Log.d("Name", pictureName);
                            Log.d("URL", Url);
                            showImage(Url);
                        }
                    });
                }
            });
        }
    }

    @BindView(R.id.EditPrice)
    EditText txtPrice;

    @BindView(R.id.EditDesc)
    EditText txtDescription;

    @BindView(R.id.BTNUpload)
    Button BTNUpload;

    @BindView(R.id.imageDealUpload)
    ImageView image;

    private TravelDeal deal;
    private String IMAGE_TYPE="image/jpeg";
    private int INTENT_REQUEST_CODE=42;
    private CharSequence INTENT_TITLE="Insert Picture";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        ButterKnife.bind(this);

//        FirebaseUtil.openFbReference(MainRef, this);
        mFirebaseDatabase=FirebaseUtil.mFirebaseDatabase;
        mDatabaseReference=FirebaseUtil.mDatabaseReference;
        final Intent intent=getIntent();
        TravelDeal travelDeal=(TravelDeal)intent.getSerializableExtra(Deal_KEY);
        if (travelDeal==null){
            travelDeal=new TravelDeal();
        }
        this.deal=travelDeal;
        setData(this.deal);

        showImage(deal.getImageUrl());
        BTNUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filled=checkEnteredValues();
                if (filled) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType(IMAGE_TYPE);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    startActivityForResult(intent.createChooser(intent,
                            INTENT_TITLE), INTENT_REQUEST_CODE);
                }else {
                    Toast.makeText(getApplicationContext(), R.string.fill_first, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setData(TravelDeal travelDeal) {
        txtTitle.setText(travelDeal.getTitle());
        txtDescription.setText(travelDeal.getDescription());
        txtPrice.setText(travelDeal.getPrice());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                filled= checkEnteredValues();
                if (filled){
                    saveDeal();
                    clean();
                    backToList();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.fill_first, Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.delete:
                if (deal.getId()!=null&&deal.getId().isEmpty()==false){
                    deleteDeal();
                    Toast.makeText(getApplicationContext(), R.string.deleted, Toast.LENGTH_LONG).show();
                    backToList();
                }else {
                    Toast.makeText(getApplicationContext(), R.string.no_delete, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkEnteredValues() {
        title=txtTitle.getText().toString();
        description=txtDescription.getText().toString();
        price=txtPrice.getText().toString();
        if (title.length()>0&&description.length()>0&&price.length()>0){
            return true;
        }else {
            return false;
        }
    }

    private void clean() {
        txtTitle.setText("");
        txtPrice.setText("");
        txtDescription.setText("");
        txtTitle.requestFocus();
    }

    private void saveDeal() {
        deal.setTitle(title);
        deal.setDescription(description);
        deal.setPrice(price);
        if (deal.getId() == null) {
            mDatabaseReference.push().setValue(deal);
        } else {
            mDatabaseReference.child(deal.getId()).setValue(deal);
        }
        Toast.makeText(getApplicationContext(), R.string.saved, Toast.LENGTH_LONG).show();
    }

    private void deleteDeal(){
        if (deal==null){
            Toast.makeText(getApplicationContext(), getString(R.string.save_before_del), Toast.LENGTH_LONG).show();
            return;
        }else if (deal!=null){
                if (deal.getId()!=null) {
                    mDatabaseReference.child(deal.getId()).removeValue();
                    if (deal.getImageName() != null && deal.getImageName().isEmpty() == false) {
                        StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
                        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Delete Image", "Image Successfully Deleted!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Error!", e.getMessage());
                            }
                        });
                    }
                }
        }
    }

    private void backToList(){
        Intent intent=new Intent(this, UserActivity.class);
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
            BTNUpload.setEnabled(true);
        }else {
            menu.findItem(R.id.delete).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableEditText(false);
            BTNUpload.setEnabled(false);
            BTNUpload.setText(getApplicationContext().getString(R.string.write_permission_denied));
        }
        return true;
    }

    private void enableEditText(boolean isEnabled){
        txtTitle.setEnabled(isEnabled);
        txtDescription.setEnabled(isEnabled);
        txtPrice.setEnabled(isEnabled);
    }

    private void showImage(String url){
        if (url!=null&&url.isEmpty()==false){
            int width= Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(image);
        }
    }
}
