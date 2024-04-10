package com.example.greenharvest.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.greenharvest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ContainerDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String containerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        containerId = getIntent().getStringExtra("containerId");
        if (containerId == null) {
            // Handle the case when containerId is not provided
            Toast.makeText(this, "Container ID not provided", Toast.LENGTH_SHORT).show();
            finish();
        }

        Button deleteContainerButton = findViewById(R.id.deleteContainerButton);
        deleteContainerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Container")
                .setMessage("Are you sure you want to delete this container?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteContainer(containerId);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteContainer(String containerId) {
        DatabaseReference containerRef = mDatabase.child("Containers").child(containerId);
        containerRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(ContainerDetailsActivity.this, "Container deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity after deleting the container
                } else {
                    Toast.makeText(ContainerDetailsActivity.this, "Failed to delete container: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
