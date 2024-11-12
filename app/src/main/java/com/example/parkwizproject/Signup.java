package com.example.parkwizproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwizproject.modules.UserType;
import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.services.userService;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private static final int REQ_ONE_TAP = 2; // Can be any integer unique to the Activity

    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        oneTapClient = Identity.getSignInClient(this);

        RelativeLayout GsignupBTN = findViewById(R.id.googleSignupButton);
        GsignupBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                // Initialize Google One-Tap Client
//                oneTapClient = Identity.getSignInClient(this);

                // Create a sign-in request
                signInRequest = BeginSignInRequest.builder()
                        .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setServerClientId(getString(R.string.default_web_client_id))  // Ensure this matches your Google API client ID
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                        .build();

                // Handle Google Sign-Up
                launchGoogleSignIn();
            }
        });
    }

    private void launchGoogleSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        Toast.makeText(this, "Popup singup", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, idToken, Toast.LENGTH_SHORT).show();
                if (idToken != null) {
                    // Authenticate with Firebase using the Google ID token
                    firebaseAuthWithGoogle(idToken);
                }
            } catch (ApiException e) {
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
                            userModel u = new userModel();
                            u.setUsername(user.getDisplayName());
                            u.setUser_id(user.getUid());
                            u.setEmail(user.getEmail());
                            u.setImage(user.getPhotoUrl().toString());
                            u.setUser_type(UserType.USER);
                            userService.saveUser(u);
                            userService.getUserById(user.getUid())
                                    .addOnSuccessListener(dataSnapshot -> {
                                        if (dataSnapshot.exists()) {
                                            userModel userm = dataSnapshot.getValue(userModel.class);
                                            if (userm != null) {
                                                System.out.println("User retrieved: " + userm.getUsername());
                                            }
                                        } else {
                                            System.out.println("User not found.");
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle the error
                                        System.err.println("Error fetching user: " + e.getMessage());
                                    });
                            navigateToHome();
                        } else {
                            // If sign-in fails, display a message to the user
                            Toast.makeText(Signup.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToHome() {
        Intent intent = new Intent(Signup.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
