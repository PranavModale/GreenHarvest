package com.example.greenharvest.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Import Log class
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Container;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class B_Admin extends Fragment {

    private static final String TAG = "B_Admin"; // Define a tag for logging

    private LinearLayout linearLayout;
    private DatabaseReference containerRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database reference
        containerRef = FirebaseDatabase.getInstance().getReference().child("Containers");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_b__admin, container, false);

        linearLayout = view.findViewById(R.id.linear_layout);

        Button button = view.findViewById(R.id.createContainerButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the MainActivity
                Intent intent = new Intent(getActivity(), CreateContainer.class);
                startActivity(intent);
            }
        });

        // Fetch container data from Firebase Database
        fetchContainerData();

        return view;
    }

    private void fetchContainerData() {
        containerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Total containers found: " + dataSnapshot.getChildrenCount()); // Log the total number of containers found
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Container container = snapshot.getValue(Container.class);
                    if (container != null) {
                        Log.d(TAG, "onDataChange: Container retrieved: " + container.getContainerNumber()); // Log the container retrieved
                        // Create a new card view for each container
                        createContainerCardView(container);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e(TAG, "onCancelled: Failed to fetch containers: " + databaseError.getMessage()); // Log the error message
                Toast.makeText(getContext(), "Failed to fetch containers: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createContainerCardView(Container container) {
        if (getContext() == null) {
            return;
        }

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_container, null);

        TextView containerNumberTextView = itemView.findViewById(R.id.container_number);
        TextView containerNameTextView = itemView.findViewById(R.id.container_name);
        TextView daysToConvertTextView = itemView.findViewById(R.id.days_to_convert);
        TextView categoryTextView = itemView.findViewById(R.id.category);

        // Set values from container object to the card view
        containerNumberTextView.setText("Container Number: " + container.getContainerNumber());
        containerNameTextView.setText("Container Name: " + container.getContainerName());
        daysToConvertTextView.setText("Days to Convert: " + container.getDaysToConvert());
        categoryTextView.setText("Category: " + container.getCategory());

        linearLayout.addView(itemView);
    }
}
