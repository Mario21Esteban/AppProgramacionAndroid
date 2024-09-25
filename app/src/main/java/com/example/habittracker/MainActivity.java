package com.example.habittracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewHabits);
        FloatingActionButton fabAddHabit = findViewById(R.id.fabAddHabit);

        databaseHelper = new DatabaseHelper(this);
        currentUsername = getIntent().getStringExtra("username");

        userId = databaseHelper.getUserId(currentUsername);
        if (userId == -1) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Cargar los hábitos del usuario
        loadHabits();

        fabAddHabit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddHabitActivity.class);
            intent.putExtra("userId", userId);
            startActivityForResult(intent, 1);
        });
    }

    private void loadHabits() {
        // Obtener la lista de hábitos para el usuario actual
        List<Habit> habits = databaseHelper.getHabitsForUser(userId);

        // Inicializar el adaptador pasándole la lista de hábitos, el contexto y el nombre del usuario
        habitAdapter = new HabitAdapter(habits, this, currentUsername);  // Pasa los 3 parámetros requeridos
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(habitAdapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadHabits();
        }
    }
}
