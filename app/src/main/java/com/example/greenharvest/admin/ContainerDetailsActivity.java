package com.example.greenharvest.admin;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenharvest.R;
import com.example.greenharvest.model.Container;
import com.example.greenharvest.model.Seller;
import com.example.greenharvest.seller.SellerProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContainerDetailsActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private String containerId;
    private LinearLayout sellerLayout;
    private RecyclerView sellerRecyclerView;
    private SellerAdapter sellerAdapter;
    private List<Seller> sellerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container_details);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        containerId = getIntent().getStringExtra("containerId");
        if (containerId == null) {
            // Handle the case when containerId is not provided
            // ...
            return;
        }

        sellerLayout = findViewById(R.id.linear_layout);
        sellerRecyclerView = findViewById(R.id.seller_recycler_view);
        sellerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sellerList = new ArrayList<>();
        sellerAdapter = new SellerAdapter(sellerList);
        sellerRecyclerView.setAdapter(sellerAdapter);

        fetchContainerDetails();


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


    private void fetchContainerDetails() {
        DatabaseReference containerRef = mDatabase.child("Containers").child(containerId);
        containerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Container container = dataSnapshot.getValue(Container.class);
                if (container != null) {
                    List<String> sellerIds = container.getArrayOfSellers();
                    if (sellerIds != null) {
                        fetchSellers(sellerIds);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }

    private void fetchSellers(List<String> sellerIds) {
        for (String sellerId : sellerIds) {
            DatabaseReference sellerRef = mDatabase.child("Sellers").child(sellerId);
            sellerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Seller seller = dataSnapshot.getValue(Seller.class);
                    if (seller != null) {
                        sellerList.add(seller);
                        sellerAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle database error
                }
            });
        }
    }

    private static class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ViewHolder> {

        private List<Seller> sellerList;

        public SellerAdapter(List<Seller> sellerList) {
            this.sellerList = sellerList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_cardview, parent, false);
            return new ViewHolder(view, sellerList);
        }



        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Seller seller = sellerList.get(position);
            holder.sellerNameTextView.setText(seller.getSellerName());
            holder.sellerNumberTextView.setText(seller.getPhoneNumber());
        }

        @Override
        public int getItemCount() {
            return sellerList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public TextView sellerNameTextView;
            public TextView sellerNumberTextView;
            private final List<Seller> sellerList;

            public ViewHolder(@NonNull View itemView, List<Seller> sellerList) {
                super(itemView);
                this.sellerList = sellerList;
                sellerNameTextView = itemView.findViewById(R.id.textSellerName);
                sellerNumberTextView = itemView.findViewById(R.id.textSellerNumber);

                // Set OnClickListener to the seller item view
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Get the seller at this position
                        Seller seller = sellerList.get(getAdapterPosition());
                        // Start SellerProfileActivity and pass seller id
                        Intent intent = new Intent(itemView.getContext(), SellerProfileActivity.class);
                        intent.putExtra("sellerId", seller.getId());
                        itemView.getContext().startActivity(intent);
                    }
                });
            }
        }


    }

}
