package com.example.parkwizproject.services;

import com.example.parkwizproject.modules.vehicleModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class vehicleService {
    private static FirebaseDatabase db = dbService.getInstance();

    public static vehicleModel saveVehicle(vehicleModel v, String user_id) {
        DatabaseReference vehicle_ref = db.getReference("Vehicle/" + user_id);
        String key = vehicle_ref.push().getKey();
        v.setId(key);
        vehicle_ref.child(key).setValue(v);
        return v;
    }

    public static Task<DataSnapshot> getVehicleByuiser_id(String user_id) {
        return db.getReference("Vehicle").child(user_id).get();
    }

    public static Task<DataSnapshot> getVehicleByID(String user_id, String Id) {
        return db.getReference("Vehicle").child(user_id).child(Id).get();
    }

    public static Task<DataSnapshot> getVehicle() {
        return db.getReference("Vehicle").get();
    }


}
