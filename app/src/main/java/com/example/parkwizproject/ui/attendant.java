package com.example.parkwizproject.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkwizproject.R;
import com.example.parkwizproject.modules.VehicleType;
import com.example.parkwizproject.modules.parkingLotModelDetails;
import com.example.parkwizproject.modules.priceModel;
import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.modules.vehicleModel;
import com.example.parkwizproject.modules.vehicleParked;
import com.example.parkwizproject.services.authService;
import com.example.parkwizproject.services.dbService;
import com.example.parkwizproject.services.parkVehicleService;
import com.example.parkwizproject.services.parkingLotListService;
import com.example.parkwizproject.services.priceService;
import com.example.parkwizproject.services.userService;
import com.example.parkwizproject.services.vehicleService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class attendant extends Fragment {

    String[] vehicleType = {
            "Bicycle",
            "Motorbike",
            "Tuk Tuk",
            "Bus",
            "Car",
            "Van",
            "Truck"};
    int[] vehicleDrawables = {
            R.drawable.ic_bicycle,
            R.drawable.ic_motorbike,
            R.drawable.ic_tuk_tuk,
            R.drawable.ic_bus,
            R.drawable.ic_car,
            R.drawable.ic_van,
            R.drawable.ic_truck
    };

    //    private static FirebaseDatabase db = dbService.getInstance();
    private static FirebaseAuth auth = authService.getAuth();
    private static String parkingLotId;
    private static String Date;
    private static String selectedVehicleId;

    private static int is = 0;

    static {
        userService.getUserById(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                userModel user = dataSnapshot.getValue(userModel.class);
                parkingLotId = user.getParkingLot_id();
            }
        });
    }

    public attendant() {
        // Required empty public constructor


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendant, container, false);
        LinearLayout linearLayout = view.findViewById(R.id.scrolllinier);
        LinearLayout notification = view.findViewById(R.id.notifications);
        HorizontalScrollView test = view.findViewById(R.id.scrollView2);
        test.setVisibility(View.GONE);


        HashMap<String, Integer> vehicalCount = new HashMap<>();
        for (String v : vehicleType) {
            vehicalCount.put(v, 0);
        }


        userService.getUserById(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshotq -> {
            if (dataSnapshotq.exists()) {
                userModel user = dataSnapshotq.getValue(userModel.class);
                parkingLotId = user.getParkingLot_id();

                parkVehicleService.releaseDetails(parkingLotId).addOnSuccessListener(dataSnapshot -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    for (DataSnapshot child : dataSnapshot.child(sdf.format(new Date())).getChildren()) {
                        vehicleParked parked = child.getValue(vehicleParked.class);
                        if (parked.getStatus().equals("parked")) {
                            vehicleService.getVehicle().addOnSuccessListener(dataSnapshot1 -> {
                                if (dataSnapshot1.exists()) {
                                    for (DataSnapshot child1 : dataSnapshot1.getChildren()) {
                                        for (DataSnapshot childChild : child1.getChildren()) {
                                            vehicleModel value = childChild.getValue(vehicleModel.class);
                                            if (value.getId().equals(parked.getVehicleID())) {
                                                Integer i = vehicalCount.get(value.getType().toString().replace('_', ' '));
                                                vehicalCount.put(value.getType().toString().replace('_', ' '), i + 1);
                                                is++;
                                                for (int i1 = 0; i1 < vehicleType.length; i1++) {
                                                    String vehicle = vehicleType[i1];
                                                    View nodeView = inflater.inflate(R.layout.item_vehicle_count, linearLayout, false);
                                                    ImageView icon = nodeView.findViewById(R.id.vehicle_type);
                                                    TextView vehicle_count = nodeView.findViewById(R.id.vehicle_count);
                                                    icon.setImageResource(vehicleDrawables[i1]);
                                                    TextView count = view.findViewById(R.id.textView7);
                                                    count.setText((is) + "");
//
                                                    vehicle_count.setText(vehicle + " Count : " + vehicalCount.get(vehicle));

                                                    linearLayout.addView(nodeView);
                                                }
                                            }
                                        }

                                    }

                                }
                            });
                        }
                    }

                });
            }
        });
//        for (int i = 0; i < 3; i++) {
//            View nodeView = inflater.inflate(R.layout.item_park_attendent_notification, notification, false);
//            TextView title = nodeView.findViewById(R.id.title);
//            title.setText("Request " + 1);
//
//            notification.addView(nodeView);
//        }


        Switch openSwich = view.findViewById(R.id.switch1);
        Switch fullSwich = view.findViewById(R.id.switch2);
        userService.getUserById(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                userModel user = dataSnapshot.getValue(userModel.class);
                parkingLotId = user.getParkingLot_id();
                parkingLotListService.getParkinglotDetailsByid(parkingLotId).addOnSuccessListener(dataSnapshot1 -> {
                    if (dataSnapshot1.exists()) {
                        parkingLotModelDetails value = dataSnapshot1.getValue(parkingLotModelDetails.class);
                        openSwich.setChecked(value.isOpen());
                        if (openSwich.isChecked()) {
                            openSwich.setText(R.string.open);
                        } else {
                            openSwich.setText(R.string.close);
                        }
                        fullSwich.setChecked(value.isFull());
                        if (fullSwich.isChecked()) {
                            fullSwich.setText(R.string.full);
                        } else {
                            fullSwich.setText(R.string.nfull);
                        }
                    }
                });
            }
        });
        Button park_btn = view.findViewById(R.id.parkBTN);
        Button release_btn = view.findViewById(R.id.releaseBTN);

        park_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowParkVehicle(v);
            }
        });

        release_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowReleaseVehicle(v);
            }
        });
        openSwich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingLotListService.updateOpenAndClose(parkingLotId, openSwich.isChecked());
                if (openSwich.isChecked()) {
                    openSwich.setText(R.string.open);
                } else {
                    openSwich.setText(R.string.close);
                }

            }
        });
        fullSwich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkingLotListService.updateFullOrNot(parkingLotId, fullSwich.isChecked());
                if (fullSwich.isChecked()) {
                    fullSwich.setText(R.string.full);
                } else {
                    fullSwich.setText(R.string.nfull);
                }

            }
        });


        return view;
    }

    private void ShowParkVehicle(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.park_vehicle_popup, null);
        EditText vehicleNumber = popupView.findViewById(R.id.editTextText);
        TextView carType = popupView.findViewById(R.id.textView12);
        vehicleModel[] selectedVehicle = new vehicleModel[1];
        vehicleNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that the text is about to change.
                // You can leave this empty if you don't need to handle this.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that the text is changing.
                // You can leave this empty if you don't need to handle this.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                vehicleService.getVehicle().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        boolean vehicleFound = false;

                        // Iterate over each user node
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            // Iterate over each vehicle under the user
                            for (DataSnapshot snapshot : child.getChildren()) {
                                vehicleModel value = snapshot.getValue(vehicleModel.class);
                                if (value.getVehicleNumber().equals(editable.toString())) {
                                    // Vehicle found, update UI and store selected vehicle
                                    carType.setText(value.getType() + "");
                                    selectedVehicle[0] = value; // Assign selected vehicle
                                    vehicleFound = true;
                                    break; // Exit loop since we found the vehicle
                                }
                            }

                            if (vehicleFound) {
                                break; // Break outer loop if vehicle was found
                            }
                        }
                    }
                });
            }

        });

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
        Button parkBTN = popupView.findViewById(R.id.button3);
        ImageView closeBTN = popupView.findViewById(R.id.imageView4);
        closeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        parkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parkVehicleService.parkVehicle(parkingLotId, selectedVehicle[0].getId());
                popupWindow.dismiss();
            }
        });

    }

    private static VehicleType findVehicleType(String vt) {
        VehicleType v = VehicleType.Bicycle;
        switch (vt) {
            case "Bicycle":
                v = VehicleType.Bicycle;
                break;
            case "Motorbike":
                v = VehicleType.Motorbike;
                break;
            case "Tuk Tuk":
                v = VehicleType.Tuk_Tuk;
                break;
            case "Bus":
                v = VehicleType.Bus;
                break;
            case "Car":
                v = VehicleType.Car;
                break;
            case "Van":
                v = VehicleType.Van;
                break;
            case "Truck":
                v = VehicleType.Truck;
                break;
        }
        return v;
    }

//    private void showPayment(View anchorView, String parkinglotId, String date, String VehicleId) {
//        View popupView = getLayoutInflater().inflate(R.layout.payment_popup, null);
//
//        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        popupWindow.setOutsideTouchable(true);
//        popupWindow.setFocusable(true);
//        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
//    }

    private void ShowReleaseVehicle(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.release_vehicle_popup, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText vehicleNumber = popupView.findViewById(R.id.editTextText2);
        TextView vehicleType = popupView.findViewById(R.id.textView17);
        TextView vehicleNumberView = popupView.findViewById(R.id.textView19);
        TextView timeDuration = popupView.findViewById(R.id.textView21);
        TextView priceview = popupView.findViewById(R.id.textView22);
        ImageView closeBtn = popupView.findViewById(R.id.imageView5);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        TextView paymentMethod = popupView.findViewById(R.id.textView24);

        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
        Button releasedBTN = popupView.findViewById(R.id.button4);
        releasedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showPayment(anchorView,);
//                parkVehicleService.parkVehicle(parkingLotId, selectedVehicle[0].getId());
//                popupWindow.dismiss();
            }
        });
        final vehicleParked[] vp = new vehicleParked[1];
        vehicleNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that the text is about to change.
                // You can leave this empty if you don't need to handle this.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called to notify you that the text is changing.
                // You can leave this empty if you don't need to handle this.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                vehicleService.getVehicle().addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        boolean vehicleFound = false;

                        // Iterate over each user node
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            // Iterate over each vehicle under the user
                            for (DataSnapshot snapshot : child.getChildren()) {
                                vehicleModel value = snapshot.getValue(vehicleModel.class);
                                if (value.getVehicleNumber().equals(editable.toString())) {
                                    // Vehicle found, update UI and store selected vehicle
//                                    carType.setText(value.getType() + "");
//                                    selectedVehicle[0] = value; // Assign selected vehicle
                                    vehicleFound = true;
                                    vehicleType.setText(value.getType() + "");
                                    vehicleNumberView.setText(value.getVehicleNumber());
                                    selectedVehicleId = value.getId();
//                                    timeDuration.setText();
                                    parkVehicleService.releaseDetails(parkingLotId).addOnSuccessListener(dataSnapshot1 -> {
                                        if (dataSnapshot1.exists()) {
                                            for (DataSnapshot datelooper : dataSnapshot1.getChildren()) {
                                                Toast.makeText(getContext(), datelooper.getKey(), Toast.LENGTH_SHORT).show();
                                                Date = datelooper.getKey();
                                                DataSnapshot child1 = datelooper.child(value.getId());
                                                if (child1.exists()) {
                                                    vp[0] = child1.getValue(vehicleParked.class);
                                                    timeDuration.setText(gettimeDution(vp[0].getParkedDate(), new Date().getTime()));
                                                    long l = new Date().getTime() - vp[0].getParkedDate();
                                                    priceService.getPrice(parkingLotId, value.getType()).addOnSuccessListener(dataSnapshot2 -> {
                                                        if (dataSnapshot2.exists()) {
                                                            priceModel price = dataSnapshot2.getValue(priceModel.class);
                                                            double lastprice = 0;
                                                            long i1 = l / (1000 * 60 * 60);
                                                            i1 = i1 - 1;
                                                            lastprice = price.getFirstHour();
                                                            for (int i = 0; i < i1; i++) {
                                                                lastprice += price.getAdditionalHour();
                                                            }
                                                            priceview.setText("LKR " + lastprice);
                                                        }
                                                    });

                                                    break;
                                                }
                                            }
                                        }
                                    });
                                    break; // Exit loop since we found the vehicle
                                }
                            }
                            if (vehicleFound) {
                                break; // Break outer loop if vehicle was found
                            }
                        }
                    }
                });
            }

        });


    }

    private String gettimeDution(long parkedDate, long time) {
        // Calculate the difference in milliseconds
        long difference = time - parkedDate;

        // Convert milliseconds to hours and minutes
        long hours = (difference / (1000 * 60 * 60));
        long minutes = (difference / (1000 * 60)) % 60;

        // Create the duration string based on hours and minutes
        String duration;
        if (hours > 0) {
            duration = hours + "h " + minutes + "min";
        } else {
            duration = minutes + "min";
        }
        return duration;
    }

}