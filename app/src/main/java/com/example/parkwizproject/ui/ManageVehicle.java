package com.example.parkwizproject.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkwizproject.R;
import com.example.parkwizproject.modules.VehicleType;
import com.example.parkwizproject.modules.parkingLotModel;
import com.example.parkwizproject.modules.vehicleModel;
import com.example.parkwizproject.services.authService;
import com.example.parkwizproject.services.dbService;
import com.example.parkwizproject.services.vehicleService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class ManageVehicle extends Fragment {

    private PopupWindow popupWindow;
    private LinearLayout linearLayout;

    private FirebaseDatabase db = dbService.getInstance();
    private FirebaseAuth auth = authService.getAuth();


    public ManageVehicle() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_vehicle, container, false);

        // Find the LinearLayout in the fragment layout
        linearLayout = rootView.findViewById(R.id.linearLayout4);
        Button showPopupButton = rootView.findViewById(R.id.addvehicalbtn);

        // Setup the button click listener to show the PopupWindow
        showPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        // Dynamically add items to the LinearLayout
        addVehicleItems();

        return rootView;
    }

    private View view;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;


    }

    private void showPopupWindow(View anchorView) {

        // Inflate the popup layout
        View popupView = getLayoutInflater().inflate(R.layout.add_vehicle_popup, null);

        ImageView icon = popupView.findViewById(R.id.imageView2);
        // Create the PopupWindow
        popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set a background drawable to make it dismissible
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Show the PopupWindow at a desired position
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);


        // Find the Spinner in the popupView (not anchorView)
        Spinner vehicleTypeSpinner = popupView.findViewById(R.id.vehicleTypeSpinner);

        // Set up the adapter for the Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.vehicle_types, R.layout.spinner_item); // Use custom layout for items
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // Dropdown style
        vehicleTypeSpinner.setAdapter(adapter);

        vehicleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                icon.setImageResource(findVehicleIcon(findVehicleType(vehicleTypeSpinner.getSelectedItem().toString())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        View addvehicleBTN = popupView.findViewById(R.id.button);
        addvehicleBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String vehicaltype = vehicleTypeSpinner.getSelectedItem().toString();
                String vehicle_number = ((EditText) popupView.findViewById(R.id.vehicle_number)).getText().toString();
                String nickname = ((EditText) popupView.findViewById(R.id.nicknameVehicle)).getText().toString();
                vehicleModel v = new vehicleModel();
                v.setType(vehicaltype);
                v.setVehicleNumber(vehicle_number);
                v.setNickName(nickname);
                v = vehicleService.saveVehicle(v, auth.getCurrentUser().getUid());

                popupWindow.dismiss();
            }
        });
        // Set up close button in the popup window
        ImageView closeButton = popupView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
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

    private int findVehicleIcon(VehicleType vt) {
        int i = 0;
        switch (vt) {
            case Bicycle:
                i = R.drawable.ic_bicycle;
                break;
            case Bus:
                i = R.drawable.ic_bus;
                break;
            case Car:
                i = R.drawable.ic_car;
                break;
            case Motorbike:
                i = R.drawable.ic_motorbike;
                break;
            case Tuk_Tuk:
                i = R.drawable.ic_tuk_tuk;
                break;
            case Truck:
                i = R.drawable.ic_truck;
                break;
            case Van:
                i = R.drawable.ic_van;
                break;
        }
        return i;
    }

    private void addVehicleItems() {
        vehicleService.getVehicleByuiser_id(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Map<String, Object> vehicleMap = (Map<String, Object>) dataSnapshot.getValue();
                if (vehicleMap != null) {
                    for (Map.Entry<String, Object> entry : vehicleMap.entrySet()) {
                        vehicleModel vehicle = dataSnapshot.child(entry.getKey()).getValue(vehicleModel.class);
                        View vehicleItemView = getLayoutInflater().inflate(R.layout.item_vehicle, linearLayout, false);

                        // Find the components in the inflated layout
                        ImageView vehicleImage = vehicleItemView.findViewById(R.id.vehicle_image);
                        ImageView deleteBtn = vehicleItemView.findViewById(R.id.delete_btn);
                        TextView vehicleTitle = vehicleItemView.findViewById(R.id.vehicle_title);
                        TextView vehicleNumber = vehicleItemView.findViewById(R.id.vehicle_number);
                        TextView vehicleType = vehicleItemView.findViewById(R.id.vehicle_type);

                        // Set the image, title, and type (you can customize this with real data)
                        vehicleImage.setImageResource(findVehicleIcon(vehicle.getType())); // Placeholder image
                        vehicleTitle.setText(vehicle.getNickName());
                        vehicleType.setText(vehicle.getType().toString());
                        vehicleNumber.setText(vehicle.getVehicleNumber());
                        vehicleItemView.setOnClickListener(view -> {
//                            showPopupWindow(this.view);
                        });
                        deleteBtn.setOnClickListener(view -> {
                        });

                        // Add the inflated item to the LinearLayout
                        linearLayout.addView(vehicleItemView);
                    }
                }
            }
        });

        // Loop to add multiple items (you can add data from an array or list)
//        for (
//                int i = 0;
//                i < 3; i++) {
//            // Inflate the item layout
//            View vehicleItemView = getLayoutInflater().inflate(R.layout.item_vehicle, linearLayout, false);
//
//            // Find the components in the inflated layout
//            ImageView vehicleImage = vehicleItemView.findViewById(R.id.vehicle_image);
//            ImageView deleteBtn = vehicleItemView.findViewById(R.id.delete_btn);
//            TextView vehicleTitle = vehicleItemView.findViewById(R.id.vehicle_title);
//            TextView vehicleNumber = vehicleItemView.findViewById(R.id.vehicle_number);
//            TextView vehicleType = vehicleItemView.findViewById(R.id.vehicle_type);
//
//            // Set the image, title, and type (you can customize this with real data)
//            vehicleImage.setImageResource(R.drawable.icon_car); // Placeholder image
//            vehicleTitle.setText("Vehicle " + (i + 1));
//            vehicleType.setText("Type " + (i + 1));
//            vehicleNumber.setText("BIW 0577");
//            vehicleItemView.setOnClickListener(view -> {
//                showPopupWindow(this.view);
//            });
//            deleteBtn.setOnClickListener(view -> {
//            });
//
//            // Add the inflated item to the LinearLayout
//            linearLayout.addView(vehicleItemView);
//        }
    }


}