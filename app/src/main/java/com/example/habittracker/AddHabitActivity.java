package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddHabitActivity extends AppCompatActivity {

    private EditText habitNameEditText;
    private Button saveHabitButton;
    private DatabaseHelper databaseHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);

        habitNameEditText = findViewById(R.id.habitNameEditText);
        saveHabitButton = findViewById(R.id.saveHabitButton);
        databaseHelper = new DatabaseHelper(this);

        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }

        saveHabitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String habitName = habitNameEditText.getText().toString().trim();
                if (!habitName.isEmpty()) {
                    long habitId = databaseHelper.addHabit(habitName, userId);
                    if (habitId != -1) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AddHabitActivity.this, "Error al agregar el hábito", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddHabitActivity.this, "Ingrese un nombre para el hábito", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
