package com.example.parkwizproject.services;


import com.example.parkwizproject.modules.vehicleParked;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class parkVehicleService {
    private static FirebaseDatabase db = dbService.getInstance();

    public static void parkVehicle(String parklotID, String vehicleID) {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        vehicleParked vp = new vehicleParked();
        vp.setParkedDate(d.getTime());
        vp.setParkinglotID(parklotID);
        vp.setVehicleID(vehicleID);
        vp.setStatus("parked");
        db.getReference("parkingLotList").child(parklotID).child("parkDetails").child(sdf.format(d)).child(vehicleID).setValue(vp);
    }

    public static Task<DataSnapshot> releaseDetails(String parklotID) {
        return db.getReference("parkingLotList").child(parklotID).child("parkDetails").get();
    }

}
