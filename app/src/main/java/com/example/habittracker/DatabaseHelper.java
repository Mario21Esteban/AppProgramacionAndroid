package com.example.habittracker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "HabitTracker.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla de usuarios
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";

    // Tabla de hábitos
    private static final String TABLE_HABITS = "habits";
    private static final String COLUMN_HABIT_ID = "id";
    private static final String COLUMN_HABIT_NAME = "name";
    private static final String COLUMN_USER_ID_FK = "user_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_NAME + " TEXT" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Crear tabla de hábitos
        String CREATE_HABITS_TABLE = "CREATE TABLE " + TABLE_HABITS + "("
                + COLUMN_HABIT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_HABIT_NAME + " TEXT,"
                + COLUMN_USER_ID_FK + " INTEGER,"
                + " FOREIGN KEY (" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(CREATE_HABITS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Métodos CRUD para usuarios
    public long addUser(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, userName);
        return db.insert(TABLE_USERS, null, values);
    }

    public List<String> getAllUsers() {
        List<String> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
        if (cursor.moveToFirst()) {
            do {
                users.add(cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return users;
    }

    // Métodos CRUD para hábitos
    public long addHabit(String habitName, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_HABIT_NAME, habitName);
        values.put(COLUMN_USER_ID_FK, userId);
        return db.insert(TABLE_HABITS, null, values);
    }

    public List<Habit> getHabitsForUser(int userId) {
        List<Habit> habits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_HABITS + " WHERE " + COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                String habitName = cursor.getString(cursor.getColumnIndex(COLUMN_HABIT_NAME));
                // Crear un objeto Habit con el nombre obtenido y agregarlo a la lista
                Habit habit = new Habit(habitName);
                habits.add(habit);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return habits;  // Devolver la lista de objetos Habit
    }


    public int getUserId(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_NAME + " = ?", new String[]{userName});
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID));
            cursor.close();
            return userId;
        } else {
            cursor.close();
            return -1;  // Usuario no encontrado
        }
    }
}

