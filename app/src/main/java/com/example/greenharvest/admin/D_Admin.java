package com.example.greenharvest.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.greenharvest.MainActivity;
import com.example.greenharvest.R;
import com.example.greenharvest.common.EditProfile;
import com.example.greenharvest.logins.LoginActivity;
import com.example.greenharvest.model.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class D_Admin extends Fragment {

    private TextView adminNameTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView phoneNumberTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setHasOptionsMenu(true); // Enable options menu for this fragment
    }

    // Inflate the options menu layout
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu);
    }

    // Handle options menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_edit) {
            // Handle Edit Profile menu item
            Intent editProfileIntent = new Intent(getActivity(), EditProfile.class);
            startActivity(editProfileIntent);
            return true;
        } else if (id == R.id.menu_logout) {
            // Handle Log out menu item
            showLogoutConfirmationDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Display AlertDialog to confirm logout
    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked Yes, perform logout
                logoutUser();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked No, dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Perform logout action
    private void logoutUser() {
        // Clear user role preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("rolepref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Sign out from Firebase Authentication
        FirebaseAuth.getInstance().signOut();

        // Start the Welcome activity
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_d__admin, container, false);

        adminNameTextView = view.findViewById(R.id.adminName);
        cityTextView = view.findViewById(R.id.city);
        stateTextView = view.findViewById(R.id.state);
        phoneNumberTextView = view.findViewById(R.id.phoneNumber);

        // Retrieve and display admin's data
        displayAdminData();

        return view;
    }

    private void displayAdminData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String adminId = currentUser.getUid();
            DatabaseReference adminRef = mDatabase.child("Admins").child(adminId);
            adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Admin admin = snapshot.getValue(Admin.class);
                        if (admin != null) {
                            adminNameTextView.setText(admin.getAdminName());
                            cityTextView.setText(admin.getCity());
                            stateTextView.setText(admin.getState());
                            phoneNumberTextView.setText(admin.getPhoneNumber());
                        }
                    } else {
                        Toast.makeText(requireContext(), "Admin data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(requireContext(), "Failed to retrieve admin data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
