package com.works.onpoint.onpoint_basemvp;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    Button btSend;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    FirebaseUser fbU;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        btSend=(Button)findViewById(R.id.SEND);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sp.edit();
    }
    @Override
    public void onStart() {
        super.onStart();
        fbU=mAuth.getCurrentUser();
        btSend.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        sendLocation();
    }
    public void sendLocation() {
        Log.d("LocationStatus", "Sending...");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        String path=fbU.getEmail().replace('.','_');
        myRef.child(path);
        myRef.child(path);
        myRef.child(path).child("Name").setValue(fbU.getDisplayName());
        myRef.child(path).child("Mail").setValue(fbU.getEmail());
        myRef.child(path).child("Geo-History");
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm",Locale.ENGLISH);
        //df=DateFormat.getDateInstance(DateFormat.FULL, Locale.UK);

        String date = df.format(Calendar.getInstance().getTime());
        try {
            DatabaseReference UserLocRef = myRef.child(path).child("Geo-History").child(date);
            UserLocRef.child("Longitude").setValue("null");
            UserLocRef.child("Latitude").setValue("null");
            UserLocRef.child("Timestamp").setValue(System.currentTimeMillis());
            UserLocRef.child("Device").setValue(Build.MANUFACTURER+Build.MODEL);
        } catch (Exception e){
        Log.d("failo", e.toString()+"refkey: "+path);
    }


    }
}
