package com.example.parkwizproject.services;

import com.example.parkwizproject.modules.VehicleType;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

public class priceService {

    private static FirebaseDatabase db = dbService.getInstance();

    public static void setPriceToFirst(String parkinglotID, VehicleType type,double price){
        db.getReference("prices").child(parkinglotID).child(type.toString()).child("firstHour").setValue(price);
    }
    public static void setPriceToAdditional(String parkinglotID, VehicleType type,double price){
        db.getReference("prices").child(parkinglotID).child(type.toString()).child("additionalHour").setValue(price);
    }

    public static Task<DataSnapshot> getPrice(String parkinglotID, VehicleType type){
        return db.getReference("prices").child(parkinglotID).child(type.toString()).get();
    }

}
