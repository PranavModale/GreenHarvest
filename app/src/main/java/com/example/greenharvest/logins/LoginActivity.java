package com.example.greenharvest.logins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.example.greenharvest.admin.AdminMain;
import com.example.greenharvest.buyer.BuyerMain;
import com.example.greenharvest.seller.SellerMain;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        TextView btnSignup = findViewById(R.id.signup);
        Spinner userTypeSpinner = findViewById(R.id.userTypeSpinner);

        // Check if ActionBar is not null before hiding it
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        });

        Button btnLogin = findViewById(R.id.login_btn);
        btnLogin.setOnClickListener(v -> {
            showProgressBar();
            String selectedUserType = userTypeSpinner.getSelectedItem().toString();
            if (selectedUserType.equals("Admin")) {
                loginAdmin();
            } else if (selectedUserType.equals("Donor")) {
                loginSeller();
            } else if (selectedUserType.equals("Buyer")) {
                loginBuyer();
            }
        });
    }

    private void loginAdmin() {
        TextInputLayout emailInput = findViewById(R.id.login_email);
        String email = emailInput.getEditText().getText().toString();
        TextInputLayout passInput = findViewById(R.id.login_pass);
        String password = passInput.getEditText().getText().toString();

        // Validate email and password fields
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        } else {
            // Query the database to check if the email exists in the Admin users data
            DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("Admins");
            adminRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Email exists in the Admin database
                        loginAdminWithEmail(email, password);
                    } else {
                        // Email does not exist in the Admin database
                        Toast.makeText(LoginActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            });
        }
    }

    private void loginAdminWithEmail(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Admin login successful
                        // Save the user role to SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("role", "admin");
                        editor.apply();

                        // Redirect to the Admin main activity
                        Intent intent = new Intent(LoginActivity.this, AdminMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Admin login failed
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressBar();
                });
    }


    private void loginSeller() {
        TextInputLayout emailInput = findViewById(R.id.login_email);
        String email = emailInput.getEditText().getText().toString();
        TextInputLayout passInput = findViewById(R.id.login_pass);
        String password = passInput.getEditText().getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        } else {
            DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference().child("Sellers");
            sellersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Email exists in the seller database
                        loginSellerWithEmail(email, password);
                    } else {
                        // Email does not exist in the seller database
                        Toast.makeText(LoginActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            });
        }
    }


    private void loginSellerWithEmail(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Seller login successful
                        SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("role", "seller");
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, SellerMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Seller login failed
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressBar();
                });
    }


    private void loginBuyer() {
        TextInputLayout emailInput = findViewById(R.id.login_email);
        String email = emailInput.getEditText().getText().toString();
        TextInputLayout passInput = findViewById(R.id.login_pass);
        String password = passInput.getEditText().getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
            hideProgressBar();
        } else {
            DatabaseReference buyersRef = FirebaseDatabase.getInstance().getReference().child("Buyers");
            buyersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Email exists in the buyer database
                        loginBuyerWithEmail(email, password);
                    } else {
                        // Email does not exist in the buyer database
                        Toast.makeText(LoginActivity.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                        hideProgressBar();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            });
        }
    }

    private void loginBuyerWithEmail(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Buyer login successful
                        SharedPreferences sharedPreferences = getSharedPreferences("rolepref", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("role", "buyer");
                        editor.apply();
                        Intent intent = new Intent(LoginActivity.this, BuyerMain.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Buyer login failed
                        Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                    hideProgressBar();
                });
    }


    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
}
