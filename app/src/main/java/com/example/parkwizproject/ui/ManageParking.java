package com.example.parkwizproject.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parkwizproject.R;
import com.example.parkwizproject.modules.VehicleType;
import com.example.parkwizproject.modules.parkingLotModel;
import com.example.parkwizproject.modules.parkingLotModelDetails;
import com.example.parkwizproject.modules.priceModel;
import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.modules.vehicleModel;
import com.example.parkwizproject.modules.vehicleParked;
import com.example.parkwizproject.services.alertService;
import com.example.parkwizproject.services.authService;
import com.example.parkwizproject.services.parkVehicleService;
import com.example.parkwizproject.services.parkingLotListService;
import com.example.parkwizproject.services.parkingLotService;
import com.example.parkwizproject.services.priceService;
import com.example.parkwizproject.services.userService;
import com.example.parkwizproject.services.vehicleService;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class ManageParking extends Fragment implements OnMapReadyCallback {
    private LinearLayout linearLayout;
    private View view;
    private GoogleMap googleMap;
    private LatLng currentLocation;
    private Marker currentMarker;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean IsLocationSelected = false;
    private FirebaseAuth auth = authService.getAuth();
    // Declare an ActivityResultLauncher for permission request
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public ManageParking() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the ActivityResultLauncher to handle permission requests
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission granted, enable location
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                    getUserLocation(); // Fetch user location

                }
            } else {
                // Permission denied, show a message
                Toast.makeText(requireContext(), "Please Give Location Services", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_manage_parking, container, false);

        linearLayout = rootView.findViewById(R.id.linearLayout5);

        rootView.setOnClickListener(view -> {
//            showPopupWindow(this.view);
        });

        Button addParkingLotBTN = rootView.findViewById(R.id.button2);
        addParkingLotBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        addParkingLots(); // Adds some default parking lots to the layout

        return rootView;
    }

    private void addParkingLots() {
        parkingLotService.getParkingLotsByUserID(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Map<String, Object> parkingLotsMap = (Map<String, Object>) dataSnapshot.getValue();

                if (parkingLotsMap != null) {
                    for (Map.Entry<String, Object> entry : parkingLotsMap.entrySet()) {
                        // Get the parking lot ID (the key)
                        String parkingLotID = entry.getKey();

                        // Firebase provides this way to convert HashMap into a model class
                        parkingLotModel plm = dataSnapshot.child(parkingLotID).getValue(parkingLotModel.class);
                        View parklot = getLayoutInflater().inflate(R.layout.item_parkinglot, linearLayout, false);
                        TextView nick_name = parklot.findViewById(R.id.nick_name);
                        TextView location = parklot.findViewById(R.id.location);
                        TextView status = parklot.findViewById(R.id.status);
                        TextView manager_name = parklot.findViewById(R.id.manager_name);
                        ImageView dollar = parklot.findViewById(R.id.imageView8);
//                        TextView phone_number = parklot.findViewById(R.id.phone_number);
                        ImageView deleteBTN = parklot.findViewById(R.id.delete_btn);

                        parkingLotListService.getParkinglotDetailsByid(plm.getId()).addOnSuccessListener(dataSnapshot1 -> {
                            if (dataSnapshot1.exists()) {
                                parkingLotModelDetails value = dataSnapshot1.getValue(parkingLotModelDetails.class);
                                if (value.isOpen()) {
                                    status.setText("Open");
                                } else {
                                    status.setText("close");
                                }
                            }
                        });

                        nick_name.setText(plm.getName());
                        location.setText(plm.getCity());
                        userService.getUser().addOnSuccessListener(dataSnapshot1 -> {
                            if (dataSnapshot1.exists()) {
                                for (DataSnapshot child : dataSnapshot1.getChildren()) {
                                    userModel u = child.getValue(userModel.class);
                                    if (u.getEmail().equals(plm.getAttendant_email())) {
                                        manager_name.setText(u.getUsername());
                                        break;
                                    }
                                }

                            }
                        });
//                        phone_number.setText("07********");
//
                        parklot.setOnClickListener(view -> {
                            showPopupWindow(this.view); // Shows popup when clicking on a parking lot
                        });
//
                        deleteBTN.setOnClickListener(view -> {
                            linearLayout.removeView(parklot); // Removes the parking lot when the delete button is clicked
                        });
                        dollar.setOnClickListener(view -> {
                            showPricePopupWindow(this.view, parkingLotID);
                        });
//
                        linearLayout.addView(parklot); // Adds the parking lot view to the layout
//                }
                    }
                }
            }
        });
    }

    private void showPricePopupWindow(View anchorView, String parkingLotID) {
        // Inflate the custom layout/view
        View popupView = getLayoutInflater().inflate(R.layout.price_enting_popup, null);

        // Create a PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set a background drawable to make it dismissible
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Show the PopupWindow at a desired position
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        LinearLayout pricinglistLayout = popupView.findViewById(R.id.pricinglist);
        Button setprice = popupView.findViewById(R.id.button5);
        setprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertService.showAlert(getContext(),"Successfully Set Price","Saved Prices By Vehicle Type");
            }
        });
        for (String vt : vehicleType) {
            View parklot = getLayoutInflater().inflate(R.layout.item_price_entring, pricinglistLayout, false);

            ImageView vehicleIcon = parklot.findViewById(R.id.vehicaleIcon);
            TextView vehicleType = parklot.findViewById(R.id.vehicaleType);
            EditText firstHourView = parklot.findViewById(R.id.firstHour);
            EditText additionalView = parklot.findViewById(R.id.additionalHour);

            vehicleIcon.setImageResource(findVehicleIcon(findVehicleType(vt)));
            vehicleType.setText(vt);
            priceService.getPrice(parkingLotID, findVehicleType(vt)).addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    priceModel price = dataSnapshot.getValue(priceModel.class);
                    firstHourView.setText(price.getFirstHour() + "");
                    additionalView.setText(price.getAdditionalHour() + "");
                }
            });


            firstHourView.addTextChangedListener(new TextWatcher() {
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
                    if (editable.toString().length() > 0) {
                        priceService.setPriceToFirst(parkingLotID, findVehicleType(vt), Double.parseDouble(editable.toString()));
                    }
                }

            });
            additionalView.addTextChangedListener(new TextWatcher() {
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
                    if (editable.toString().length() > 0) {
                        priceService.setPriceToAdditional(parkingLotID, findVehicleType(vt), Double.parseDouble(editable.toString()));
                    }
                }

            });

            pricinglistLayout.addView(parklot);
        }

    }

    String[] vehicleType = {
            "Bicycle",
            "Motorbike",
            "Tuk Tuk",
            "Bus",
            "Car",
            "Van",
            "Truck"};

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

    private void showPopupWindow(View anchorView) {
        // Inflate the custom layout/view
        View popupView = getLayoutInflater().inflate(R.layout.add_parking_lot_popup, null);

        // Create a PopupWindow
        final PopupWindow popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set a background drawable to make it dismissible
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        // Show the PopupWindow at a desired position
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        ImageView closeButton = popupView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        Button getLocationBTN = popupView.findViewById(R.id.getLocationBTN);
        getLocationBTN.setOnClickListener(view -> {
            IsLocationSelected = true;
            Toast.makeText(getContext(), currentMarker.getPosition().latitude + " " + currentMarker.getPosition().longitude, Toast.LENGTH_SHORT).show();
        });

        EditText parking_lot_name = popupView.findViewById(R.id.parking_lot_name);
        EditText attendant_email = popupView.findViewById(R.id.attendant_email);
        EditText city = popupView.findViewById(R.id.city);
        Button addParkingLotBTN = popupView.findViewById(R.id.btn_add_parking_lot);
        addParkingLotBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsLocationSelected) {
                    userService.getUser().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DataSnapshot snapshot = task.getResult();
                            if (snapshot.exists()) {
                                boolean b = false;
                                userModel u = new userModel();

                                for (DataSnapshot user_ids : snapshot.getChildren()) {
                                    u = user_ids.getValue(userModel.class);
                                    if (u.getEmail().equals(attendant_email.getText().toString())) {
                                        b = true;
                                        break;
                                    }
                                }
                                if (b) {
                                    parkingLotModel plm = new parkingLotModel();
                                    plm.setName(parking_lot_name.getText().toString());
                                    plm.setAttendant_email(attendant_email.getText().toString());
                                    plm.setCity(city.getText().toString());
                                    plm.setLatitude(currentMarker.getPosition().latitude);
                                    plm.setLongitude(currentMarker.getPosition().longitude);
                                    plm = parkingLotService.addParkingLot(plm, auth.getCurrentUser().getUid());
                                    parkingLotListService.saveParkingLot(plm.getId());
                                    u.setParkingLot_id(plm.getId());
                                    userService.updateUser(u);
                                } else {
                                    alertService.showAlert(getContext(), "Warning", "Attendant Email Isn`t In Our System,\nPlease Register Him/Her First");
                                }
                            }
                        }
                    });
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(getContext(), "Please Select Location This Parking Lot", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // Set up the Map Fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // Ensuring OnMapReadyCallback is used
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Check if permission is already granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, enable location and fetch user location
            googleMap.setMyLocationEnabled(true);
            getUserLocation(); // Fetch the user's current location
        } else {
            // Permission is not granted, request permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    // Function to get user's current location
    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Remove the previous marker if it exists
                    if (currentMarker != null) {
                        currentMarker.remove();
                    }

                    // Add a new draggable marker at the current location
                    currentMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Your Parking Lot").draggable(true)); // Set draggable to true

                    // Move the camera to the current location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                } else {
                    Toast.makeText(getContext(), "Something Went Wrong Unable to get current location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
