package com.example.greenharvest.logins;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Admin;
import com.example.greenharvest.model.Buyer;
import com.example.greenharvest.model.Seller;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private AutoCompleteTextView stateDropdown;
    private AutoCompleteTextView cityDropdown;
    private Button signupButton;
    private TextView loginLink;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference adminDB;
    private DatabaseReference buyerDB;
    private DatabaseReference sellerDB;
    private ProgressDialog progressDialog;

    private Spinner userTypeSpinner;

    private static final int DEFAULT_PROFILE_IMAGE_RESOURCE_ID = R.drawable.defaultprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        stateDropdown = findViewById(R.id.f_auto_complete_text_state);
        cityDropdown = findViewById(R.id.f_auto_complete_text_city);
        firebaseAuth = FirebaseAuth.getInstance();
        adminDB = FirebaseDatabase.getInstance().getReference().child("Admins");
        buyerDB = FirebaseDatabase.getInstance().getReference().child("Buyers");
        sellerDB = FirebaseDatabase.getInstance().getReference().child("Sellers");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");

        signupButton = findViewById(R.id.f_signup);
        loginLink = findViewById(R.id.login);

        // Check if ActionBar is not null before hiding it
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, Location.state);
        stateDropdown.setAdapter(stateAdapter);

        stateDropdown.setOnItemClickListener((adapterView, view, position, l) -> {
            int selectedStateIndex = position;
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_dropdown_item_1line, Location.cities[selectedStateIndex]);
            cityDropdown.setAdapter(cityAdapter);
        });

        cityDropdown.setOnItemClickListener((adapterView, view, position, l) -> {
            String selectedCity = (String) adapterView.getItemAtPosition(position);
            Toast.makeText(SignUpActivity.this, "Selected city: " + selectedCity, Toast.LENGTH_SHORT).show();
        });

        // Initialize userTypeSpinner
        userTypeSpinner = findViewById(R.id.userTypeSpinner);

        signupButton.setOnClickListener(v -> {
            String selectedUserType = userTypeSpinner.getSelectedItem().toString();
            if (selectedUserType.equals("Admin")) {
                registerAdmin(DEFAULT_PROFILE_IMAGE_RESOURCE_ID);
            } else if (selectedUserType.equals("Buyer")) {
                registerBuyer(DEFAULT_PROFILE_IMAGE_RESOURCE_ID);
            } else if (selectedUserType.equals("Seller")) {
                registerSeller(DEFAULT_PROFILE_IMAGE_RESOURCE_ID);
            }
        });

        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerAdmin(int defaultProfileImageResourceId) {
        TextInputLayout emailLayout = findViewById(R.id.sign_email);
        TextInputLayout passwordLayout = findViewById(R.id.sign_pass);
        TextInputLayout nameLayout = findViewById(R.id.sign_user);
        TextInputLayout contactLayout = findViewById(R.id.sign_mobile);

        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();
        String name = nameLayout.getEditText().getText().toString().trim();
        String contact = contactLayout.getEditText().getText().toString().trim();
        String state = stateDropdown.getText().toString().trim();
        String city = cityDropdown.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty() || state.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            String defaultProfileImageUrl = "android.resource://" + getPackageName() + "/" + defaultProfileImageResourceId;
                            Admin admin = new Admin(uid, name, email, password, state, city, contact, defaultProfileImageUrl);
                            adminDB.child(uid).setValue(admin)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("role", "admin");
                                            editor.apply();
                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            // Redirect to Admin activity or any desired activity
                                        } else {
                                            String errorMessage = task1.getException().getMessage(); // Get the error message
                                            Log.e("Registration Failed", errorMessage); // Log the error message
                                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            String errorMessage = task.getException().getMessage(); // Get the error message
                            Log.e("Registration Failed", errorMessage); // Log the error message
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                        }
                    });
        }
    }


    private void registerBuyer(int defaultProfileImageResourceId) {
        TextInputLayout emailLayout = findViewById(R.id.sign_email);
        TextInputLayout passwordLayout = findViewById(R.id.sign_pass);
        TextInputLayout nameLayout = findViewById(R.id.sign_user);
        TextInputLayout contactLayout = findViewById(R.id.sign_mobile);

        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();
        String name = nameLayout.getEditText().getText().toString().trim();
        String contact = contactLayout.getEditText().getText().toString().trim();
        String state = stateDropdown.getText().toString().trim();
        String city = cityDropdown.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty() || state.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            String defaultProfileImageUrl = "android.resource://" + getPackageName() + "/" + defaultProfileImageResourceId;
                            Buyer buyer = new Buyer(uid, name, email, password, state, city, contact, defaultProfileImageUrl);
                            buyerDB.child(uid).setValue(buyer)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("role", "buyer");
                                            editor.apply();
                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            // Redirect to Buyer activity or any desired activity
                                        } else {
                                            String errorMessage = task1.getException().getMessage(); // Get the error message
                                            Log.e("Registration Failed", errorMessage); // Log the error message
                                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            String errorMessage = task.getException().getMessage(); // Get the error message
                            Log.e("Registration Failed", errorMessage); // Log the error message
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                        }
                    });
        }
    }

    private void registerSeller(int defaultProfileImageResourceId) {
        TextInputLayout emailLayout = findViewById(R.id.sign_email);
        TextInputLayout passwordLayout = findViewById(R.id.sign_pass);
        TextInputLayout nameLayout = findViewById(R.id.sign_user);
        TextInputLayout contactLayout = findViewById(R.id.sign_mobile);

        String email = emailLayout.getEditText().getText().toString().trim();
        String password = passwordLayout.getEditText().getText().toString().trim();
        String name = nameLayout.getEditText().getText().toString().trim();
        String contact = contactLayout.getEditText().getText().toString().trim();
        String state = stateDropdown.getText().toString().trim();
        String city = cityDropdown.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty() || name.isEmpty() || contact.isEmpty() || state.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String uid = firebaseAuth.getCurrentUser().getUid();
                            String defaultProfileImageUrl = "android.resource://" + getPackageName() + "/" + defaultProfileImageResourceId;
                            Seller seller = new Seller(uid, name, email, password, state, city, contact, defaultProfileImageUrl);
                            sellerDB.child(uid).setValue(seller)
                                    .addOnCompleteListener(task1 -> {
                                        progressDialog.dismiss();
                                        if (task1.isSuccessful()) {
                                            SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("role", "seller");
                                            editor.apply();
                                            Toast.makeText(SignUpActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            // Redirect to Seller activity or any desired activity
                                        } else {
                                            String errorMessage = task1.getException().getMessage(); // Get the error message
                                            Log.e("Registration Failed", errorMessage); // Log the error message
                                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                                        }
                                    });
                        } else {
                            progressDialog.dismiss();
                            String errorMessage = task.getException().getMessage(); // Get the error message
                            Log.e("Registration Failed", errorMessage); // Log the error message
                            Toast.makeText(SignUpActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show(); // Display error message in toast
                        }
                    });
        }
    }

}
