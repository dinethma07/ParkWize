package com.example.parkwizproject.services;

import com.google.firebase.database.FirebaseDatabase;

public class dbService {

    private static FirebaseDatabase db = null;

    static {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
        }
    }


    public static FirebaseDatabase getInstance() {
        return db;
    }

}
