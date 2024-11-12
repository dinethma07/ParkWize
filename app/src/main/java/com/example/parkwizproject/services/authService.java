package com.example.parkwizproject.services;

import com.google.firebase.auth.FirebaseAuth;

public class authService {
    private static FirebaseAuth auth = null;

    static {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
    }

    public static FirebaseAuth getAuth() {
        return auth;
    }
}
