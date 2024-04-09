package com.example.greenharvest.seller;

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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenharvest.R;
import com.example.greenharvest.model.AvailableWaste;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class B_Seller extends Fragment {

    private AutoCompleteTextView categoryAutoCompleteTextView;
    private EditText cropWeightEditText;
    private Button datePickerButton;
    private Button submitRequestButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_b__seller, container, false);

        // Initialize views
        categoryAutoCompleteTextView = rootView.findViewById(R.id.categoryAutoCompleteTextView);
        cropWeightEditText = rootView.findViewById(R.id.weightt);
        datePickerButton = rootView.findViewById(R.id.datePickerButton);
        submitRequestButton = rootView.findViewById(R.id.submitRequestButton);

        // Initialize dropdown adapter for categories
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.category_array));
        categoryAutoCompleteTextView.setAdapter(categoryAdapter);

        // Handle date picker button click
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());

        // Handle submit request button click
        submitRequestButton.setOnClickListener(v -> submitRequest());

        return rootView;
    }

    // Show date picker dialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, year1, monthOfYear, dayOfMonth1) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, monthOfYear, dayOfMonth1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            String formattedDate = dateFormat.format(selectedDate.getTime());
            datePickerButton.setText(formattedDate);
        }, year, month, dayOfMonth);

        datePickerDialog.show();
    }

    // Method to submit the request
    // Method to submit the request
    private void submitRequest() {
        String category = categoryAutoCompleteTextView.getText().toString();
        String cropWeightStr = cropWeightEditText.getText().toString();
        String selectedDateStr = datePickerButton.getText().toString();

        if (category.isEmpty() || cropWeightStr.isEmpty() || selectedDateStr.equals(getString(R.string.select_date))) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double cropWeight = Double.parseDouble(cropWeightStr);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String sellerId = user.getUid();
            // Parsing date string to Date object
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date selectedDate;
            try {
                selectedDate = sdf.parse(selectedDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error parsing date", Toast.LENGTH_SHORT).show();
                return;
            }

            AvailableWaste availableWaste = new AvailableWaste(sellerId, category, cropWeight, selectedDate);
            mDatabase.child("AvailableWaste").push().setValue(availableWaste)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Request submitted successfully", Toast.LENGTH_SHORT).show();
                        clearFields();
                    })
                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to submit request: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }


    // Method to clear input fields
    private void clearFields() {
        categoryAutoCompleteTextView.setText("");
        cropWeightEditText.setText("");
        datePickerButton.setText(getString(R.string.select_date));
    }
}