package com.example.parkwizproject.services;

import com.example.parkwizproject.modules.parkingLotModelDetails;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class parkingLotListService {
    private static FirebaseDatabase db = dbService.getInstance();

    public static void saveParkingLot(String parkinglot_id) {
        DatabaseReference pll = db.getReference("parkingLotList");
        parkingLotModelDetails plmd = new parkingLotModelDetails();
        plmd.setOpen(false);
        plmd.setFull(false);

        pll.child(parkinglot_id).setValue(plmd).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                System.out.println("User saved successfully.");
            } else {
                System.err.println("Error saving user: " + task.getException().getMessage());
            }
        });
    }

    public static Task<DataSnapshot> getParkinglotDetailsByid(String parkinglot_id) {
        return db.getReference("parkingLotList").child(parkinglot_id).get();
    }

    public static Task<DataSnapshot> getParkinglotDetails() {
        return db.getReference("parkingLotList").get();
    }

    public static void updateOpenAndClose(String id, boolean b) {
        db.getReference("parkingLotList").child(id).child("open").setValue(b);
    }
    public static void updateFullOrNot(String id, boolean b) {
        db.getReference("parkingLotList").child(id).child("full").setValue(b);
    }

}
