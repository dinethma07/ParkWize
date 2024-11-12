package com.example.parkwizproject.services;

import com.example.parkwizproject.modules.parkingLotModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class parkingLotService {
    private static FirebaseDatabase db;

    static {
        db = dbService.getInstance();
    }

    public static parkingLotModel addParkingLot(parkingLotModel plm, String user_id) {
        DatabaseReference pl = db.getReference("parkLot/" + user_id);
        String key = pl.push().getKey();
        plm.setId(key);
        pl.child(plm.getId()).setValue(plm).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("User saved successfully.");
            } else {
                System.err.println("Error saving user: " + task.getException().getMessage());
            }
        });
        return plm;
    }

    public static Task<DataSnapshot> getParkingLotsByUserID(String user_id) {
        return db.getReference("parkLot").child(user_id).get();
    }
    public static Task<DataSnapshot> getParkingLots() {
        return db.getReference("parkLot").get();
    }

}