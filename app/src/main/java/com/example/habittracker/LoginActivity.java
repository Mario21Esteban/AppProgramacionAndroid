package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private Spinner userSpinner;
    private EditText newUserEditText;
    private Button loginButton;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userSpinner = findViewById(R.id.userSpinner);
        newUserEditText = findViewById(R.id.newUserEditText);
        loginButton = findViewById(R.id.loginButton);

        databaseHelper = new DatabaseHelper(this);

        // Cargar usuarios desde la base de datos
        loadUsers();

        loginButton.setOnClickListener(v -> {
            String selectedUser = userSpinner.getSelectedItem().toString();
            if (selectedUser.equals("Registrar nuevo usuario")) {
                String newUserName = newUserEditText.getText().toString().trim();
                if (!newUserName.isEmpty()) {
                    long userId = databaseHelper.addUser(newUserName);
                    if (userId != -1) {
                        goToMainActivity(newUserName);
                    } else {
                        Toast.makeText(LoginActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show();
                }
            } else {
                goToMainActivity(selectedUser);
            }
        });
    }

    private void loadUsers() {
        List<String> users = databaseHelper.getAllUsers();
        users.add("Registrar nuevo usuario");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, users);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSpinner.setAdapter(adapter);
    }

    private void goToMainActivity(String userName) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("username", userName);
        startActivity(intent);
        finish();
    }
}
