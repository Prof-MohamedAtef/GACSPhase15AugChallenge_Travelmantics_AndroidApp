package mo.gacs.challenge2.travelmantics;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mo.gacs.challenge2.travelmantics.Activities.ListActivity;
import mo.gacs.challenge2.travelmantics.Models.TravelDeal;

public class FirebaseUtil {
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mDatabaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;
    public static ArrayList<TravelDeal> mDeals;
    private static final int RC_SIGN_IN=123;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    private static ListActivity Caller;
    public static boolean isAdmin;
    private static String ADMINS_KEY="administrators";
    private static String STORAGE_FOLDER="deals_pictures";

    private FirebaseUtil(){}

    public static void openFbReference(String ref, final mo.gacs.challenge2.travelmantics.Activities.ListActivity callerActivity){
        if (firebaseUtil==null){
            firebaseUtil=new FirebaseUtil();
            mFirebaseDatabase=FirebaseDatabase.getInstance();
            mFirebaseAuth=FirebaseAuth.getInstance();
            Caller=callerActivity;
            mAuthListener   =new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser()==null){
                        FirebaseUtil.signIn();
                    }else {
                        //space
                        String uID=firebaseAuth.getCurrentUser().getUid();
                        checkAdmin(uID);
                    }
                    Toast.makeText(callerActivity.getBaseContext(), callerActivity.getBaseContext().getString(R.string.welcome_back), Toast.LENGTH_LONG).show();
                }
            };
            connectStorage();
        }
        mDeals=new ArrayList<TravelDeal>();
        mDatabaseReference=mFirebaseDatabase.getReference().child(ref);
    }

    private static void checkAdmin(String uID) {
        FirebaseUtil.isAdmin=false;
        DatabaseReference ref=mFirebaseDatabase.getReference().child(ADMINS_KEY)
                .child(uID);
        ChildEventListener listener=new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin=true;
                Caller.showMenu();
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
        ref.addChildEventListener(listener);
    }

    private static void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


// Create and launch sign-in intent

        Caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    public static void connectStorage(){
        mStorage=FirebaseStorage.getInstance();
        mStorageRef=mStorage.getReference().child(STORAGE_FOLDER);

    }
}