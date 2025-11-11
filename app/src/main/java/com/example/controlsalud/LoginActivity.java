package com.example.controlsalud;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Verificar si ya hay sesión activa
        if (preferences.contains("userId")) {
            irAlMenu();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> login());
        tvRegister.setOnClickListener(v -> irARegistro());
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.error_campos_vacios, Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = dbHelper.login(email, password);
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndexOrThrow("id_usuario"));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));

            // Guardar sesión
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("userId", userId);
            editor.putString("userName", nombre);
            editor.apply();

            cursor.close();
            Toast.makeText(this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();
            irAlMenu();
        } else {
            cursor.close();
            Toast.makeText(this, R.string.error_login, Toast.LENGTH_SHORT).show();
        }
    }

    private void irARegistro() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

   private void irAlMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}