package com.example.wifinavindoor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NavigateToDestinationActivity extends AppCompatActivity {

    private SearchView locationSearchView;
    private ListView locationListView;
    private Button navigateButton;

    private List<String> allLocations;
    private List<String> displayedLocations;
    private ArrayAdapter<String> locationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_to_destination);

        locationSearchView = findViewById(R.id.locationSearchView);
        locationListView = findViewById(R.id.locationListView);
        navigateButton = findViewById(R.id.navigateButton);

        allLocations = new ArrayList<>();
        displayedLocations = new ArrayList<>();
        locationAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayedLocations);
        locationListView.setAdapter(locationAdapter);

        // Retrieve all locations from Firebase and populate the list
        FirebaseDatabase.getInstance().getReference("locations")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                            LocationData locationData = locationSnapshot.getValue(LocationData.class);
                            if (locationData != null) {
                                String locationName = locationData.getName();
                                allLocations.add(locationName);
                            }
                        }
                        displayedLocations.addAll(allLocations);
                        locationAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle database error
                    }
                });


        // Set search functionality
        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Handle search query submission
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Filter the displayed locations based on search query
                displayedLocations.clear();
                for (String location : allLocations) {
                    if (location.toLowerCase().contains(newText.toLowerCase())) {
                        displayedLocations.add(location);
                    }
                }
                locationAdapter.notifyDataSetChanged();
                return true;
            }
        });

        // Set click listener for ListView items
        locationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedLocation = displayedLocations.get(position);
                navigateToLocation(selectedLocation);
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredLocation = locationSearchView.getQuery().toString();
                navigateToLocation(enteredLocation);
            }
        });
    }

    private void navigateToLocation(String location) {
        // Pass the location to the MapActivity using an Intent
        Intent intent = new Intent(NavigateToDestinationActivity.this, MapActivity.class);
        intent.putExtra("location", location);
        startActivity(intent);
    }
}

