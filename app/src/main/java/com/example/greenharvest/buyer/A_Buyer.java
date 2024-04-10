package com.example.greenharvest.buyer;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenharvest.R;
import com.example.greenharvest.admin.adminProfileActivity;
import com.example.greenharvest.model.Fertilizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class A_Buyer extends Fragment {

    private RecyclerView recyclerView;
    private FertilizerAdapter adapter;
    private List<Fertilizer> fertilizerList;
    private DatabaseReference databaseReference;

    // Array to hold fertilizer types
    private String[] types = {"All", "Organic", "Inorganic"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a__buyer, container, false);

        recyclerView = view.findViewById(R.id.products_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fertilizerList = new ArrayList<>();
        adapter = new FertilizerAdapter(fertilizerList);
        recyclerView.setAdapter(adapter);

        // Set up the dropdown menu for selecting the type
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        AutoCompleteTextView typeDropdown = view.findViewById(R.id.typeDropdown);
        typeDropdown.setAdapter(typeAdapter);

        typeDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedType = types[position];
            filterFertilizerList(selectedType);
        });

        // Retrieve fertilizer data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Fertilizers");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fertilizerList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Fertilizer fertilizer = snapshot.getValue(Fertilizer.class);
                    if (fertilizer != null) {
                        fertilizerList.add(fertilizer);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void filterFertilizerList(String selectedType) {
        List<Fertilizer> filteredList = new ArrayList<>();
        if (selectedType.equals("All")) {
            filteredList.addAll(fertilizerList);
        } else {
            for (Fertilizer fertilizer : fertilizerList) {
                if (fertilizer.getType().equals(selectedType)) {
                    filteredList.add(fertilizer);
                }
            }
        }
        adapter.setFilteredList(filteredList);
    }

    private static class FertilizerAdapter extends RecyclerView.Adapter<FertilizerAdapter.ViewHolder> {

        private List<Fertilizer> fertilizerList;

        public FertilizerAdapter(List<Fertilizer> fertilizerList) {
            this.fertilizerList = fertilizerList;
        }

        public void setFilteredList(List<Fertilizer> filteredList) {
            this.fertilizerList = filteredList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fertilizer_card_view, parent, false);

            // Set bottom margin to create space between card views
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            layoutParams.bottomMargin = 16; // Adjust the value as needed
            view.setLayoutParams(layoutParams);

            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Fertilizer fertilizer = fertilizerList.get(position);
            holder.bind(fertilizer);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), adminProfileActivity.class);
                    intent.putExtra("adminId", fertilizer.getAdminId());
                    v.getContext().startActivity(intent);
                }
            });
        }


        @Override
        public int getItemCount() {
            return fertilizerList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            private TextView typeTextView;
            private TextView subtypeTextView;
            private TextView pricePerKgTextView;
            private TextView quantityTextView;
            private TextView manufacturingDateTextView;
            private TextView expiryDateTextView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                typeTextView = itemView.findViewById(R.id.typeTextView);
                subtypeTextView = itemView.findViewById(R.id.subtypeTextView);
                pricePerKgTextView = itemView.findViewById(R.id.pricePerKgTextView);
                quantityTextView = itemView.findViewById(R.id.quantityTextView);
                manufacturingDateTextView = itemView.findViewById(R.id.manufacturingDateTextView);
                expiryDateTextView = itemView.findViewById(R.id.expiryDateTextView);
            }

            public void bind(Fertilizer fertilizer) {
                typeTextView.setText("Type: " + fertilizer.getType());
                subtypeTextView.setText("Subtype: " + fertilizer.getSubtype());
                pricePerKgTextView.setText("Price per kg: " + fertilizer.getPricePerKg());
                quantityTextView.setText("Quantity: " + fertilizer.getQuantity());
                manufacturingDateTextView.setText("Manufacturing Date: " + fertilizer.getManufacturingDate());
                expiryDateTextView.setText("Expiry Date: " + fertilizer.getExpiryDate());
            }
        }
    }
}
