package com.works.onpoint.onpoint_basemvp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static android.widget.Toast.LENGTH_SHORT;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private static final int RC_SIGN_IN =9000 ;
    private static final String TAG ="SplashActivity" ;
    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private static final String EMAIL_EXTENSION = "@firebaseAnon.com";
    private static final int REQUEST_LOCATION =1 ;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    FirebaseAuth mAuth;
    FirebaseUser fbU;
    String uid;
    Dialog d;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    AlertDialog dialog;
    Handler handler;
    View GoogleSign,AnonSign,AnonCreate;
    TextView tvWelcome,tvError;
    Button btnMainLogin;
    EditText etMail;
    EditText etPass;
    boolean permission;
    private static String USER_TYPE="userType";
    private static String USER_TYPE_GOOGLE="GoogleAccount";
    private static String USER_TYPE_ANONYMOUS="AnonAccount";
    private static String USER_EMAIL="Email";
    private static String USER_PASSWORD="Password";
    private GoogleSignInClient mGoogleSignInClient;
    private int DELAY_TIME;
    private boolean mLocationPermissionsGranted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        GoogleSign=(View)findViewById(R.id.GoogleSign);
        AnonSign=(View)findViewById(R.id.AnonSign);
        AnonCreate=(View)findViewById(R.id.AnonSignUp);
        sp= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sp.edit();
        mAuth=FirebaseAuth.getInstance();
        fbU=mAuth.getCurrentUser();
        permission=false;
        tvWelcome=(TextView)findViewById(R.id.Welcome);
        mLocationPermissionsGranted=false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        GoogleSign.setVisibility(View.INVISIBLE);
        AnonSign.setVisibility(View.INVISIBLE);
        AnonCreate.setVisibility(View.INVISIBLE);
//        if (fbU == null) {
//            try {
//                fbU = (FirebaseUser) readObject(this, "fbU");
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        if (fbU == null) {
            DELAY_TIME = 50;
            tvWelcome.setText("Welcome");
            GoogleSign.setVisibility(View.VISIBLE);
            GoogleSign.setOnClickListener(this);
            GoogleSign.setOnTouchListener(this);
            AnonSign.setVisibility(View.VISIBLE);
            AnonSign.setOnClickListener(this);
            AnonSign.setOnTouchListener(this);
            AnonCreate.setVisibility(View.VISIBLE);
            AnonCreate.setOnClickListener(this);
            AnonCreate.setOnTouchListener(this);
        } else {
            DELAY_TIME = SPLASH_DISPLAY_LENGTH;
            updateUI();
        }
    }
    //User Sign-In/Up/Delete methods
    public void userAnonSignUp(final String email,final String password)
    {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userAnonSignIn(email,password);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });

    }
    public void userAnonSignIn(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }
    public void userGoogleSignIn()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void userDelete()
    {
        fbU.delete().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("userState","User deleted from firebase");
                Toast.makeText(SplashActivity.this,"Previous user on this device has been deleted",Toast.LENGTH_LONG).show();

            }
        });
        fbU=null;
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            if(task.getException() instanceof FirebaseAuthInvalidUserException)
                                userDelete();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SplashActivity.this,"Authentication Failed.", LENGTH_SHORT).show();
                            //updateUI();
                        }

                        // ...
                    }
                });
    }
    //Screen action functions
    @Override
    public void onClick(View view) {
        if (view == AnonSign) {
            createDialog("SignIn");
        }
        editor.putString(USER_TYPE,USER_TYPE_ANONYMOUS);
        if(view==GoogleSign) {
            userGoogleSignIn();
            editor.putString(USER_TYPE,USER_TYPE_GOOGLE);
        }
        if (view == AnonCreate) {
            createDialog("SignUp");
            editor.putString(USER_TYPE,USER_TYPE_ANONYMOUS);
            }
        editor.apply();
    }
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
                v.invalidate();
                break;
            }
            case MotionEvent.ACTION_UP: {
                v.getBackground().clearColorFilter();
                v.invalidate();
                break;
            }
        }
        return false;
    }
    public void createDialog(final String type)
    {
        d = new Dialog(this);
        d.setContentView(R.layout.register);
        d.setTitle("Registration");
        etMail = (EditText) d.findViewById(R.id.Mail);
        etPass = (EditText) d.findViewById(R.id.Pass);
        btnMainLogin = (Button) d.findViewById(R.id.btnLogin);
        tvError=(TextView)d.findViewById(R.id.Error);
        tvError.setVisibility(View.INVISIBLE);
        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMail.getText().toString().length() > 2 || etPass.getText().toString().length() > 5) {
                    String[] User={etMail.getText().toString(),etPass.getText().toString()};
                    if(type.equals("SignUp"))
                        userAnonSignUp(User[0]+EMAIL_EXTENSION,User[1]);
                    if(type.equals("SignIn"))
                        userAnonSignIn(User[0]+EMAIL_EXTENSION,User[1]);
                    d.dismiss();
                    Log.d(TAG,"Valid user data, proceeding...; mail: "+User[0]+EMAIL_EXTENSION+"|Password: "+User[1]);
                }
                else {
                    Log.d(TAG, "Invalid user data");
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });
        d.setCancelable(true);
        d.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    public void updateUI()
    {
//            try {
//                FirebaseUser f= (FirebaseUser) readObject(this, "fbU");
//            } catch (IOException e) {
//                e.printStackTrace();
//                SaveUser();
//            } catch (ClassNotFoundException e) {
//                e.printStackTrace();
//            }
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent MainIntent=new Intent();
                MainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MainIntent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(MainIntent);
                finish();
            }
        }, DELAY_TIME);
       // GoogleSign.setVisibility(View.INVISIBLE);
       // AnonSign.setVisibility(View.INVISIBLE);
    }
    //Other

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
//    public void SaveUser()
//    {
//        Log.d(TAG,"Saving file to "+this.getFilesDir().getAbsolutePath());
//        String key="fbU";
//        try {
//            writeObject(this,key,fbU);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void writeObject(Context context, String key, Object object) throws IOException {
//        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(object);
//        oos.close();
//        fos.close();
//    }
//    public static Object readObject(Context context, String key) throws IOException,
//            ClassNotFoundException {
//        FileInputStream fis = context.openFileInput(key);
//        ObjectInputStream ois = new ObjectInputStream(fis);
//        Object object = ois.readObject();
//        return object;
//    }
}
