package com.example.greenharvest.admin;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class B_Admin extends Fragment {

    private LinearLayout linearLayout;
    private DatabaseReference containerRef;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Database reference
        containerRef = FirebaseDatabase.getInstance().getReference().child("Containers");
        // Get current user
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
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

        // Fetch container data from Firebase Database for the current admin
        fetchContainerData();

        return view;
    }

    private void fetchContainerData() {
        // Query to fetch containers with the current admin's id
        Query query = containerRef.orderByChild("adminId").equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Container container = snapshot.getValue(Container.class);
                    if (container != null) {
                        createContainerCardView(container);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
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
        TextView capacityTextView = itemView.findViewById(R.id.capacity);
        TextView totalWeightTextView = itemView.findViewById(R.id.total_weight);
        TextView daysToConvertTextView = itemView.findViewById(R.id.days_to_convert);
        TextView creationDateTextView = itemView.findViewById(R.id.creation_date);

        // Format the creation date as "dd-MM-yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(container.getCreationDate());

        // Set values from container object to the card view
        containerNumberTextView.setText("Container Number: " + container.getContainerNumber());
        capacityTextView.setText("Capacity: " + container.getCapacity());
        totalWeightTextView.setText("Total Weight: " + container.getTotalWeight());
        daysToConvertTextView.setText("Days to Convert: " + container.getDaysToConvert());
        creationDateTextView.setText("Creation Date: " + formattedDate);

        linearLayout.addView(itemView);
    }
}
