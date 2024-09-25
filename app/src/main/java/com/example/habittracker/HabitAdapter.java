package com.example.habittracker;

import static com.example.habittracker.R.id.*;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;
import java.util.List;



public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {

    private static final int REQUEST_CODE = 1001; // Define un request code constante
    private List<Habit> habitList;
    private Context context;
    private String currentUserName; // Para las notificaciones personalizadas

    public HabitAdapter(List<Habit> habitList, Context context, String currentUserName) {
        this.habitList = habitList;
        this.context = context;
        this.currentUserName = currentUserName;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);
        holder.habitNameTextView.setText(habit.getName());

        // Mostrar la descripción si existe
        if (!habit.getDescription().isEmpty()) {
            holder.habitDescriptionTextView.setText(habit.getDescription());
            holder.habitDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.habitDescriptionTextView.setVisibility(View.GONE);
        }

        // Añadir listener para la selección de un hábito
        holder.itemView.setOnClickListener(v -> {
            // Mostrar un menú con las opciones solicitadas
            PopupMenu popupMenu = new PopupMenu(context, v);
            popupMenu.getMenuInflater().inflate(R.menu.habit_menu, popupMenu.getMenu());

            // Acción para programar notificación
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();  // Capturar el ID del menú seleccionado

                if (id == R.id.menu_notify) {
                    openNotificationDialog(habit.getName(), currentUserName);  // Método para programar notificaciones
                    return true;
                } else if (id == R.id.menu_edit_description) {
                    openEditDescriptionDialog(habit);  // Método para agregar descripción
                    return true;
                } else if (id == R.id.menu_delete) {
                    deleteHabit(habit);  // Método para eliminar el hábito
                    return true;
                } else {
                    return false;
                }
            });


            popupMenu.show();
        });
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    // Método para abrir el diálogo de notificaciones
    private void openNotificationDialog(String habitName, String userName) {
        // Mostrar un diálogo para que el usuario seleccione la hora de la notificación
        Calendar calendar = Calendar.getInstance();

        // Crear un TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // Programar la notificación usando AlarmManager
            scheduleNotification(calendar, habitName, userName);

        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        timePickerDialog.show();
    }

    // Método para programar la notificación usando AlarmManager
    private void scheduleNotification(Calendar calendar, String habitName, String userName) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("habitName", habitName);
        intent.putExtra("userName", userName);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(context, "Notificación programada para " + habitName, Toast.LENGTH_SHORT).show();
    }

    // Método para abrir el diálogo de edición de descripción
    private void openEditDescriptionDialog(Habit habit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Agregar Descripción");

        // Configurar un EditText para la descripción
        final EditText input = new EditText(context);
        input.setText(habit.getDescription());
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String description = input.getText().toString();
            habit.setDescription(description);
            notifyDataSetChanged();  // Actualizar la lista de hábitos con la descripción
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Método para eliminar un hábito
    private void deleteHabit(Habit habit) {
        habitList.remove(habit);
        notifyDataSetChanged();
        Toast.makeText(context, "Hábito eliminado", Toast.LENGTH_SHORT).show();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView habitNameTextView;
        TextView habitDescriptionTextView;

        public HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            habitNameTextView = itemView.findViewById(R.id.habitNameTextView);
            habitDescriptionTextView = itemView.findViewById(R.id.habitDescriptionTextView);
        }
    }
}
