package com.appfest.funkyfotos.signin;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.appfest.funkyfotos.utils.Utility;
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
import com.google.firebase.iid.FirebaseInstanceId;

import com.appfest.funkyfotos.R;
import com.appfest.funkyfotos.config.Constants;
import com.appfest.funkyfotos.login.LoginActivity;
import com.appfest.funkyfotos.ui.NotificationToast;
import com.appfest.funkyfotos.utils.Store;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient
        .OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "SignIn";
    private static final int GOOGLE_SIGN_IN = 9001;

    private Store store;

    private SignInButton mGoogleSignIn;
    private GoogleApiClient mGoogleApiClient;

    private ProgressDialog dialog;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Assign fields
        mGoogleSignIn = (SignInButton) findViewById(R.id.sign_in_button);
        // Set click listeners
        mGoogleSignIn.setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestScopes(new Scope(Scopes.PROFILE))
//                .requestScopes(new Scope(Scopes.PLUS_ME))
//                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Initialize FirebaseAuth.
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        // Others
        store = Store.getInstance(SignInActivity.this);
        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading_please_wait));
        requestSDCardPermission();
    }

    private static final int SD_CARD_REQ_CODE = 12;
    public void requestSDCardPermission() {
        boolean isSDPermissionGranted = Utility.checkPermission(Manifest.permission
                .WRITE_EXTERNAL_STORAGE, this);
        if (!isSDPermissionGranted) {
            Utility.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    SD_CARD_REQ_CODE, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == SD_CARD_REQ_CODE) {
            if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equalsIgnoreCase(permissions[0]) &&
                    grantResults[0] == PackageManager.PERMISSION_DENIED) {
                destroyWithMessage(this.getString(R.string.required_permission_denied));
            }
        }
    }

    public void destroyWithMessage(String message) {
        if (message != null && !message.isEmpty()) {
            NotificationToast.showToast(this, message);
        }
        finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                dialog.show();
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.");
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            saveGoogleUser(acct);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

                            //startActivity(new Intent(SignInActivity.this, LoginActivity.class));

                            finish();
                        }
                    }
                });
    }

    private void loginShortcut(String userName, String password){
        Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
        intent.putExtra("userName", userName);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    private void saveGoogleUser(GoogleSignInAccount acct) {

        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        String userUID = firebaseUser.getUid();
        String picURL = null;

        if (firebaseUser != null && firebaseUser.getPhotoUrl() != null) {
            picURL = firebaseUser.getPhotoUrl().toString();
        }
        String userName = firebaseUser.getDisplayName();
        String userEmail = firebaseUser.getEmail();
        String providerId = firebaseUser.getProviderId();

        String fcmToken = FirebaseInstanceId.getInstance().getToken();

        // Save user info locally
        store.setFcmToken(fcmToken);
        store.setFirebaseUID(userUID);
        store.setUserEmail(userEmail);
        store.setMyName(userName);
        store.setFirebasePicUrl(picURL);

        loginShortcut(userEmail, "123456");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        NotificationToast.showToast(this, "Google Play Services error.");
    }
}
