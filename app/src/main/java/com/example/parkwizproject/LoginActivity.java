package com.example.parkwizproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.services.userService;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQ_ONE_TAP = 2;

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private RelativeLayout gsigninBTN;
    private Button signupBTN;
    private Button loginBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize Google One-Tap Client
        oneTapClient = Identity.getSignInClient(this);

        if (mAuth.getCurrentUser() != null) {
            navigateToHome();
//            Toast.makeText(this, mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
        }

        gsigninBTN = findViewById(R.id.googleLoginButton);
        loginBTN = findViewById(R.id.loginButton1);
        loginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });
        gsigninBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a sign-in request
                signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))  // Ensure this matches your Google API client ID
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                        .build();
                // Start the Google Sign-In flow if the user is not already signed in
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    navigateToHome();
                } else {
                    launchGoogleSignIn();
                }
            }
        });
        signupBTN = findViewById(R.id.signupButton1);
        signupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, Signup.class));
                finish();
            }
        });

    }

    private void launchGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        startIntentSenderForResult(result.getPendingIntent().getIntentSender(), REQ_ONE_TAP, null, 0, 0, 0);
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Error in One Tap Sign-In: " + e.getLocalizedMessage());
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                if (idToken != null) {
                    // Authenticate with Firebase using the Google ID token
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Log.e(TAG, "Sign-in failed: " + e.getLocalizedMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign-in success, navigate to home
                            FirebaseUser user = mAuth.getCurrentUser();
                            userService.getUserById(user.getUid()).addOnSuccessListener(dataSnapshot -> {
                                if (dataSnapshot.exists()) {
                                    userModel u = dataSnapshot.getValue(userModel.class);
                                    if (u != null) {
                                        Toast.makeText(LoginActivity.this, "Signed in as : " + u.getUsername() + " (" + u.getUser_type() + ")", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            navigateToHome();
                        } else {
                            // If sign-in fails, display a message to the user
                            Toast.makeText(LoginActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
