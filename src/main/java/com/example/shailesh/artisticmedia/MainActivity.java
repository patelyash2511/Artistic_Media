package com.example.shailesh.artisticmedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button b;

    private EditText editTextMobile;
    private TextView howitworks;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleSignInClient;
    private  static final int RC_SIGN_IN = 9001;
    private ProgressDialog loadingBar;
    private RadioButton radioButton1,radioButton2;
    RadioGroup radioGroup;

    SignInButton googleSignInButton;

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleSignInButton = (SignInButton) findViewById(R.id.google_signin_button);
        loadingBar = new ProgressDialog(this);
        radioButton1=findViewById(R.id.radioButton1);
        howitworks=findViewById(R.id.navigateto);
        radioButton2=findViewById(R.id.radioButton2);
        radioGroup=(RadioGroup)findViewById(R.id.rg);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        //click on textview for imformation
        howitworks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,how_it_works.class);
                startActivity(intent);
            }
        });


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("251221057284-eig9fnsh2juno0c4fteudmlrje8lr8po.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
                    {
                        Toast.makeText(MainActivity.this,"Connection to google sign in is failed",Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }

        });

//        if(currentFirebaseUser != null)
//        {
//            currentFirebaseUser.getUid();
////            Toast.makeText(this, "User Id:" + currentFirebaseUser, Toast.LENGTH_SHORT).show();
//
//            Intent i = new Intent(MainActivity.this,ClientActivity.class);
//            startActivity(i);
//            finish();
//        }
    }



    private void signIn() {
        //Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        mGoogleSignInClient.clearDefaultAccountAndReconnect();
//
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            loadingBar.setTitle("Google Sign In");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                //Toast.makeText(Login.this,"Please wait, while we are getting your auth result...",Toast.LENGTH_SHORT).show();

            }

            else
            {
                Toast.makeText(MainActivity.this,"Failed, Please Try again!",Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();

            }

        }


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            // Sign in success, update UI with the signed-in user's information

                            SendUserToMainActivity();
//                            progressDialog.dismiss();

                        } else
                        {
                            // If sign in fails, display a message to the user.

                            String msg = task.getException().toString();
                            SendUserToLoginActivity();
                            Toast.makeText(MainActivity.this,"Not Authenticated :"+msg, Toast.LENGTH_SHORT).show();

//                            progressDialog.dismiss();
                        }

                        // ...
                    }
                });
    }

    private void SendUserToMainActivity()
    {

        if (radioButton1.isChecked()){
            Toast.makeText(this, "client", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(MainActivity.this, Client_Edit_Profile.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }
        else if (radioButton2.isChecked()){
            Toast.makeText(this, "artist", Toast.LENGTH_SHORT).show();
            Intent mainIntent = new Intent(MainActivity.this, ArtistActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }

    }

    private void SendUserToLoginActivity()
    {
        Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public void onClick(View v)
    {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);

    }



}