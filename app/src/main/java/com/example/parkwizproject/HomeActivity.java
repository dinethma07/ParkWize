package com.example.parkwizproject;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.parkwizproject.modules.userModel;
import com.example.parkwizproject.services.authService;
import com.example.parkwizproject.services.dbService;
import com.example.parkwizproject.services.userService;
import com.example.parkwizproject.ui.ManageParking;
import com.example.parkwizproject.ui.ManageVehicle;
import com.example.parkwizproject.ui.attendant;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.parkwizproject.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;


    private FirebaseDatabase db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = dbService.getInstance();
        auth = authService.getAuth();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarHome.toolbar);
//        Get reference to the NavigationView
//        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
//        Get the header view from the NavigationView
        View headerView = navigationView.getHeaderView(0);

//        Access the views in the header
        TextView navUsername = headerView.findViewById(R.id.navUsername);
        TextView navUserEmail = headerView.findViewById(R.id.navUserEmail);
        ImageView imageViewProfile = headerView.findViewById(R.id.imageView);
        if (auth.getCurrentUser() != null) {
            userService.getUserById(auth.getCurrentUser().getUid()).addOnSuccessListener(dataSnapshot -> {
                if (dataSnapshot.exists()) {
                    userModel user = dataSnapshot.getValue(userModel.class);
                    if (user != null) {
                        navUsername.setText(user.getUsername());
                        navUserEmail.setText(user.getEmail());
                        Glide.with(this)
                                .load(user.getImage())
                                .circleCrop()
                                .into(imageViewProfile);

                    }
                }
            });
        }


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_ManageVehicle,
                R.id.nav_attendant,
                R.id.nav_ManageParking
        )
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                if (id == R.id.ManageParking) {
//                    Toast.makeText(HomeActivity.this, "Manage Parking", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle("Manage Parking");
                    ManageParking manageParking = new ManageParking();
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_home, manageParking, "Manage Parking")
                            .addToBackStack(null)
                            .commit();
                }
                if (id == R.id.ManageVehicle) {
                    setTitle("Manage Vehicle");
                    Toast.makeText(HomeActivity.this, "Manage Vehicle", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle("Manage Vehicle");
                    ManageVehicle manageVehicle = new ManageVehicle();
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_home, manageVehicle, "Manage Vehicle")
                            .addToBackStack(null)
                            .commit();
                }

                if (id == R.id.History) {
                    Toast.makeText(HomeActivity.this, "Manage History", Toast.LENGTH_SHORT).show();
                    getSupportActionBar().setTitle("Manage History");
                }
                if (id == R.id.Attendant) {
                    getSupportActionBar().setTitle("Park Attendant");
                    attendant attendant = new attendant();
                    fragmentManager.beginTransaction()
                            .replace(R.id.nav_host_fragment_content_home, attendant, "Manage Vehicle")
                            .addToBackStack(null)
                            .commit();
//                    Intent intent = new Intent(HomeActivity.this, Adminpanel.class);
//                    startActivity(intent);
                }
                if (id == R.id.Admin) {
//                    Intent intent = new Intent(HomeActivity.this, Adminpanel.class);
//                    startActivity(intent);
                }

                // Close the drawer after item is clicked
                drawer.closeDrawer(Gravity.START);
                return true; // Return true if the click was handled
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}