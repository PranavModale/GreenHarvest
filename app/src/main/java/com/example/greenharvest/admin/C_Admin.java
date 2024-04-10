package com.example.greenharvest.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Fertilizer;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class C_Admin extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private TextInputLayout typeDropdownLayout, subtypeDropdownLayout, pricePerKgLayout,
            quantityLayout, manufacturingDateLayout, expiryDateLayout;
    private AutoCompleteTextView typeDropdown, subtypeDropdown;
    private EditText pricePerKgInput, quantityInput, manufacturingDateInput, expiryDateInput;
    private Button submitButton;

    private String[] types = {"Organic", "Inorganic"};
    private String[][] subtypes = {
            // Organic fertilizers
            {"Compost", "Manure (e.g., cow, chicken, horse)", "Fish emulsion", "Seaweed extract", "Bone meal", "Blood meal"},
            // Inorganic fertilizers
            {"Nitrogenous fertilizers", "Phosphatic fertilizers", "Potassic fertilizers", "Micronutrient fertilizers"}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_c__admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Fertilizers");

        typeDropdownLayout = view.findViewById(R.id.typeDropdownLayout);
        subtypeDropdownLayout = view.findViewById(R.id.subtypeDropdownLayout);
        pricePerKgLayout = view.findViewById(R.id.pricePerKgLayout);
        quantityLayout = view.findViewById(R.id.quantityLayout);
        manufacturingDateLayout = view.findViewById(R.id.manufacturingDateLayout);
        expiryDateLayout = view.findViewById(R.id.expiryDateLayout);

        typeDropdown = view.findViewById(R.id.typeDropdown);
        subtypeDropdown = view.findViewById(R.id.subtypeDropdown);
        pricePerKgInput = view.findViewById(R.id.pricePerKgInput);
        quantityInput = view.findViewById(R.id.quantityInput);
        manufacturingDateInput = view.findViewById(R.id.manufacturingDateInput);
        expiryDateInput = view.findViewById(R.id.expiryDateInput);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        typeDropdown.setAdapter(typeAdapter);

        typeDropdown.setOnItemClickListener((parent, view1, position, id) -> {
            ArrayAdapter<String> subtypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, subtypes[position]);
            subtypeDropdown.setAdapter(subtypeAdapter);
        });

        manufacturingDateInput.setOnClickListener(v -> showManufacturingDatePickerDialog());
        expiryDateInput.setOnClickListener(v -> showExpiryDatePickerDialog());

        submitButton = view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> addFertilizer(currentUser.getUid()));
    }

    private void addFertilizer(String adminId) {
        String type = typeDropdown.getText().toString();
        String subtype = subtypeDropdown.getText().toString();
        double pricePerKg = Double.parseDouble(pricePerKgInput.getText().toString());
        int quantity = Integer.parseInt(quantityInput.getText().toString());
        String manufacturingDate = manufacturingDateInput.getText().toString();
        String expiryDate = expiryDateInput.getText().toString();

        String fertilizerId = mDatabase.push().getKey();
        Fertilizer fertilizer = new Fertilizer(fertilizerId, adminId, type, subtype, pricePerKg, quantity, manufacturingDate, expiryDate);

        if (fertilizerId != null) {
            mDatabase.child(fertilizerId).setValue(fertilizer)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Fertilizer added successfully", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear fields after success
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to add fertilizer", Toast.LENGTH_SHORT).show();

                    });
        }

    }

    private void showManufacturingDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1;
                    manufacturingDateInput.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showExpiryDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year1, monthOfYear, dayOfMonth1) -> {
                    String selectedDate = dayOfMonth1 + "/" + (monthOfYear + 1) + "/" + year1;
                    expiryDateInput.setText(selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }


    private void clearFields() {
        typeDropdown.setText("");
        subtypeDropdown.setText("");
        pricePerKgInput.setText("");
        quantityInput.setText("");
        manufacturingDateInput.setText("");
        expiryDateInput.setText("");
    }

}
