package ma.ensa.comptes;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ma.ensa.comptes.adapter.CompteAdapter;
import ma.ensa.comptes.beans.Compte;
import ma.ensa.comptes.retrofitCong.RetrofitInstance;
import ma.ensa.comptes.service.ApiInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Tag for logs
    private RecyclerView recyclerView;
    private boolean isXmlFormat = false; // Default is JSON format

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the Spinner for format selection
        Spinner spinnerFormat = findViewById(R.id.spinner_format);
        spinnerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) { // 1 might correspond to XML
                    isXmlFormat = true;
                    Log.d("Spinner", "Selected format: XML");
                } else {
                    isXmlFormat = false;
                    Log.d("Spinner", "Selected format: JSON");
                }
                fetchComptes(); // Reload data after selection
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("Spinner", "No format selected");
            }
        });

        // Load data by default (with JSON)
        fetchComptes();

        // Set up the "Add Account" button
        findViewById(R.id.btnAddCompte).setOnClickListener(v -> showAddCompteDialog());
    }

    private void fetchComptes() {
        String acceptHeader = isXmlFormat ? "application/xml" : "application/json";

        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);

        api.getAllComptes(acceptHeader).enqueue(new Callback<List<Compte>>() {
            @Override
            public void onResponse(Call<List<Compte>> call, Response<List<Compte>> response) {
                if (response.body() != null) {
                    List<Compte> comptes = response.body();
                    recyclerView.setAdapter(new CompteAdapter(comptes, MainActivity.this));
                } else {
                    Log.e("fetchComptes", "No data received");
                }
            }

            @Override
            public void onFailure(Call<List<Compte>> call, Throwable t) {
                Log.e("fetchComptes", "Error: " + t.getMessage());
            }
        });
    }

    public void updateCompte(Long id, Compte currentCompte) {
        // Create a dialog to update the account
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Update Account");

        // Create a layout for the dialog with EditTexts for each field
        View dialogView = getLayoutInflater().inflate(R.layout.dialoge_update_compte, null);
        EditText editTextSolde = dialogView.findViewById(R.id.editTextSolde);
        EditText editTextDateCreation = dialogView.findViewById(R.id.editTextDateCreation);
        EditText editTextType = dialogView.findViewById(R.id.editTextType);

        // Populate the EditTexts with the current account values
        editTextSolde.setText(String.valueOf(currentCompte.getSolde()));
        editTextDateCreation.setText(currentCompte.getDateCreation());
        editTextType.setText(currentCompte.getType());

        builder.setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    // Get the values entered by the user
                    double newSolde = Double.parseDouble(editTextSolde.getText().toString());
                    String newDateCreation = editTextDateCreation.getText().toString();
                    String newType = editTextType.getText().toString();

                    // Create an account object with the new data
                    Compte updatedCompte = new Compte(id, newSolde, newDateCreation, newType);

                    // Call the method to update the account via the API
                    updateCompteInDatabase(id, updatedCompte);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void updateCompteInDatabase(Long id, Compte updatedCompte) {
        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);

        api.updateCompte(id, updatedCompte).enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Account updated successfully", Toast.LENGTH_SHORT).show();
                    fetchComptes(); // Reload accounts after the update
                } else {
                    Toast.makeText(MainActivity.this, "Error updating account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                Log.e(TAG, "Error updating account: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error updating account", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteCompte(Long id) {
        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);
        api.deleteCompte(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchComptes(); // Reload accounts after deletion
                } else {
                    Toast.makeText(MainActivity.this, "Error deleting account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error deleting account: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error deleting account", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to show the dialog for adding a new account
    private void showAddCompteDialog() {
        // Create a dialog for adding a new account
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add New Account");

        // Create a layout for the dialog with EditTexts for each field
        View dialogView = getLayoutInflater().inflate(R.layout.dialoge_add_compte, null);
        EditText editTextSolde = dialogView.findViewById(R.id.editTextSolde);
        EditText editTextDateCreation = dialogView.findViewById(R.id.editTextDateCreation);
        EditText editTextType = dialogView.findViewById(R.id.editTextType);

        builder.setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    // Get the values entered by the user
                    double newSolde = Double.parseDouble(editTextSolde.getText().toString());
                    String newDateCreation = editTextDateCreation.getText().toString();
                    String newType = editTextType.getText().toString();

                    // Create a new account object
                    Compte newCompte = new Compte(null, newSolde, newDateCreation, newType);

                    // Call the method to add the account via the API
                    addCompteToDatabase(newCompte);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    // Method to add a new account to the database
    private void addCompteToDatabase(Compte newCompte) {
        ApiInterface api = RetrofitInstance.getInstance(isXmlFormat).create(ApiInterface.class);

        api.addCompte(newCompte).enqueue(new Callback<Compte>() {
            @Override
            public void onResponse(Call<Compte> call, Response<Compte> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Account added successfully", Toast.LENGTH_SHORT).show();
                    fetchComptes(); // Reload accounts after adding
                } else {
                    Toast.makeText(MainActivity.this, "Error adding account", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Compte> call, Throwable t) {
                Log.e(TAG, "Error adding account: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error adding account", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
