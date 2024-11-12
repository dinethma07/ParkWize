package com.example.parkwizproject.services;

import android.widget.Toast;

import com.example.parkwizproject.modules.UserType;
import com.example.parkwizproject.modules.userModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class userService {

    private static FirebaseDatabase db;

    static {
        db = dbService.getInstance();
    }

    public static userModel saveUser(userModel u) {
        // Get reference to the "users" node in the database
        DatabaseReference usersRef = db.getReference("users");
        if (u.getUser_id() != null && !u.getUser_id().isEmpty()) {
            usersRef.child(u.getUser_id()).setValue(u)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            System.out.println("User saved successfully.");
                        } else {
                            System.err.println("Error saving user: " + task.getException().getMessage());
                        }
                    });
        } else {
            System.err.println("Error: User ID is null or empty.");
        }

        return u;  // Return the user model instance
    }

    public static Task<DataSnapshot> getUserById(String id) {
        return db.getReference("users").child(id).get();
    }

    public static Task<DataSnapshot> getUser() {
        return db.getReference("users").get();
    }

    public static void updateUser(userModel u) {
        DatabaseReference userRef = db.getReference("users").child(u.getUser_id());
        userRef.setValue(u);
    }

}
