package com.example.parkwizproject.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.parkwizproject.R;
import com.example.parkwizproject.modules.VehicleType;
import com.example.parkwizproject.modules.parkingLotModel;
import com.example.parkwizproject.modules.parkingLotModelDetails;
import com.example.parkwizproject.modules.priceModel;
import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.modules.vehicleModel;
import com.example.parkwizproject.modules.vehicleParked;
import com.example.parkwizproject.services.authService;
import com.example.parkwizproject.services.dbService;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class FirstPage extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private LatLng currentLocation;
    private FusedLocationProviderClient fusedLocationClient;
    private View view;
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
    private String selectedVehicle = "";

    private static FirebaseDatabase db = dbService.getInstance();
    private static FirebaseAuth auth = authService.getAuth();

    // Request permission launcher for location access
    private ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    enableLocation();
                } else {
                    Toast.makeText(requireContext(), "Location permission is required to show your position", Toast.LENGTH_SHORT).show();
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first_page, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
//
        addVehicles();
        loadParkedVehicle();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Check for location permission
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableLocation();
        } else {
            // Request location permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void enableLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            getUserLocation();
        }
    }

    private void getUserLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    // Add predefined markers
                    addMarkers();

                    // Move the camera to the user's location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));

                    // Set marker click listener to show details and directions
                    googleMap.setOnMarkerClickListener(marker -> {
                        showMarkerDetails(marker);
                        return true;
                    });

                } else {
                    Toast.makeText(requireContext(), "Unable to retrieve current location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addMarkers() {
        parkingLotService.getParkingLots().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    for (DataSnapshot firstLevelChild : snapshot.getChildren()) {
                        for (DataSnapshot secondLevelChild : firstLevelChild.getChildren()) {
                            parkingLotModel value = secondLevelChild.getValue(parkingLotModel.class);
                            googleMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(value.getLatitude(), value.getLongitude()))
                                            .title(value.getName()))
                                    .setTag(value.getId());
                        }
                    }
                }
            }
        });
    }

    // Method to show marker details using BottomSheetDialog
    private void showMarkerDetails(Marker marker) {
        LatLng destination = marker.getPosition();
        String locationName = marker.getTitle();

        // Create BottomSheetDialog to display marker details
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.marker_details_layout, null);

        TextView locationTitle = bottomSheetView.findViewById(R.id.location_title);
        Button directionsButton = bottomSheetView.findViewById(R.id.btn_directions);
        TextView OpenAndfull = bottomSheetView.findViewById(R.id.textView29);
        parkingLotListService.getParkinglotDetailsByid(marker.getTag() + "").addOnCompleteListener(task -> {
            String open = "", full = "";
            if (task.isSuccessful()) {
                parkingLotModelDetails value = task.getResult().getValue(parkingLotModelDetails.class);
                if (value.isOpen()) {
                    open = "Open";
                } else {
                    open = "Close";
                }

                if (value.isFull()) {
                    full = "Full";
                } else {
                    full = "Available Spase";
                }
                if (value.isOpen() && !value.isFull()) {
                    OpenAndfull.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                } else if (value.isOpen() && value.isFull()) {
                    OpenAndfull.setTextColor(ContextCompat.getColor(getContext(), R.color.yellow));
                } else {
                    OpenAndfull.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                }

                OpenAndfull.setText(open + " | " + full);
            }
        });

        locationTitle.setText(locationName);

        // Set up the button to launch directions in Google Maps
        directionsButton.setOnClickListener(v -> showDirections(destination));

        // Show the bottom sheet
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    private void showDirections(LatLng destination) {
        if (currentLocation != null) {
            // Create a Uri for Google Maps directions
            String uri = "http://maps.google.com/maps?saddr=" + currentLocation.latitude + "," + currentLocation.longitude
                    + "&daddr=" + destination.latitude + "," + destination.longitude;

            // Create an Intent to open Google Maps with the direction request
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setPackage("com.google.android.apps.maps"); // Optional: ensures only Google Maps app handles the Intent
            startActivity(intent);
        } else {
            Toast.makeText(requireContext(), "Unable to get current location.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadParkedVehicle() {
        LinearLayout PvehiclesLayout = view.findViewById(R.id.packed_vehicles);
        View pvehicleItem = LayoutInflater.from(getContext()).inflate(R.layout.item_parked_vehicle, PvehiclesLayout, false);


        vehicleService.getVehicleByuiser_id(auth.getCurrentUser().getUid()).addOnSuccessListener(ds -> {
            if (ds.exists()) {
                for (DataSnapshot child : ds.getChildren()) {
                    vehicleModel vehicle = child.getValue(vehicleModel.class);
                    parkingLotListService.getParkinglotDetails().addOnSuccessListener(ds1 -> {
                        if (ds1.exists()) {
                            for (DataSnapshot ds1Child : ds1.getChildren()) {
                                for (DataSnapshot parkDetails : ds1Child.child("parkDetails").getChildren()) {
                                    if (parkDetails.child(vehicle.getId()).exists()) {
                                        vehicleParked parked = parkDetails.child(vehicle.getId()).getValue(vehicleParked.class);
                                        if (parked.getStatus().equals("parked")) {
                                            ImageView vehicle_type = pvehicleItem.findViewById(R.id.parked_vehicle_type);
                                            TextView vehicle_number = pvehicleItem.findViewById(R.id.vehicle_number);
                                            TextView date_time = pvehicleItem.findViewById(R.id.parked_date_time);
                                            TextView time_duration = pvehicleItem.findViewById(R.id.time_duration);
                                            TextView parked_price = pvehicleItem.findViewById(R.id.parked_price);

                                            vehicle_type.setImageResource(findVehicleIcon(vehicle.getType()));
                                            vehicle_number.setText(vehicle.getVehicleNumber());
                                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm");
                                            date_time.setText(sdf.format(new Date(parked.getParkedDate())));
                                            time_duration.setText(gettimeDution(parked.getParkedDate(), new Date().getTime()));
                                            priceService.getPrice(parked.getParkinglotID(), vehicle.getType()).addOnSuccessListener(dataSnapshot2 -> {
                                                long l = new Date().getTime() - parked.getParkedDate();
                                                if (dataSnapshot2.exists()) {
                                                    priceModel price = dataSnapshot2.getValue(priceModel.class);
                                                    double lastprice = 0;
                                                    long i1 = l / (1000 * 60 * 60);
                                                    i1 = i1 - 1;
                                                    lastprice = price.getFirstHour();
                                                    for (int i = 0; i < i1; i++) {
                                                        lastprice += price.getAdditionalHour();
                                                    }
                                                    parked_price.setText("" + lastprice);
                                                    PvehiclesLayout.addView(pvehicleItem);
                                                }
                                            });
                                        }

                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
//
//        for (int i = 0; i < vehicleType.length; i++) {
//            String vehicle = vehicleType[i];
//            ImageView vehicle_type = pvehicleItem.findViewById(R.id.parked_vehicle_type);
////            TextView vehicle_number = pvehicleItem.findViewById(R.id.vehicle_number);
////            TextView date_time = pvehicleItem.findViewById(R.id.parked_date_time);
////            TextView time_duration = pvehicleItem.findViewById(R.id.time_duration);
//            TextView parked_price = pvehicleItem.findViewById(R.id.parked_price);
////
//            vehicle_type.setImageResource(vehicleDrawables[i]);
//            parked_price.setText(1000 + i + ".00");
//            PvehiclesLayout.addView(pvehicleItem);
//        }
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

    private void addVehicles() {
        LinearLayout vehiclesLayout = view.findViewById(R.id.vehicles);
//        vehicleService.getVehicleByID

        vehicleService.getVehicleByuiser_id(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Map<String, Object> vehicleMap = (Map<String, Object>) dataSnapshot.getValue();
                if (vehicleMap != null) {
                    boolean b = false;

                    View vehicleItem = LayoutInflater.from(getContext()).inflate(R.layout.item_added_vehicle, vehiclesLayout, false);
                    TextView vehicleNameTextView = vehicleItem.findViewById(R.id.textView28);
                    TextView vehicleNumberTextView = vehicleItem.findViewById(R.id.textView27);
                    ImageView vehicleImageView = vehicleItem.findViewById(R.id.imageView7);
                    ImageView corrctImageView = vehicleItem.findViewById(R.id.correct_icon);
                    if (selectedVehicle != "") {
                        vehicleModel selectedVehicleO = dataSnapshot.child(selectedVehicle).getValue(vehicleModel.class);
                        vehicleNameTextView.setText(selectedVehicleO.getType().toString());
                        vehicleNumberTextView.setText(selectedVehicleO.getVehicleNumber());
                        vehicleImageView.setImageResource(findVehicleIcon(selectedVehicleO.getType()));
                        vehiclesLayout.addView(vehicleItem);
                    }

                    for (Map.Entry<String, Object> entry : vehicleMap.entrySet()) {
                        if (entry.getKey() != selectedVehicle) {
                            vehicleModel vehicle = dataSnapshot.child(entry.getKey()).getValue(vehicleModel.class);
                            vehicleNameTextView.setText(vehicle.getType().toString());
                            vehicleNumberTextView.setText(vehicle.getVehicleNumber());
                            vehicleImageView.setImageResource(findVehicleIcon(vehicle.getType()));
                            if (b) {
                                corrctImageView.setVisibility(View.INVISIBLE);
                                vehicleItem.setOnClickListener(view1 -> {
                                    this.selectedVehicle = vehicle.getId();
                                    vehiclesLayout.removeAllViews();
                                    addVehicles();
                                });
                                vehiclesLayout.addView(vehicleItem);
                            } else {
                                b = true;
                            }
                            vehiclesLayout.addView(vehicleItem);

                        }
                    }
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
}
