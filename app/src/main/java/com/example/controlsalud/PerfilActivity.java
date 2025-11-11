package com.example.controlsalud;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNombrePerfil, tvEmailPerfil;
    private EditText etPeso, etEdad, etAltura, etSexo;
    private Button btnGuardarPerfil, btnVolver;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);

        initViews();
        cargarDatosUsuario();
        setupListeners();
    }

    private void initViews() {
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        etPeso = findViewById(R.id.etPeso);
        etEdad = findViewById(R.id.etEdad);
        etAltura = findViewById(R.id.etAltura);
        etSexo = findViewById(R.id.etSexo);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void cargarDatosUsuario() {
        Cursor cursor = dbHelper.obtenerPerfil(userId);

        if (cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            tvNombrePerfil.setText(nombre);
            tvEmailPerfil.setText(email);

            // Cargar datos opcionales
            int pesoIndex = cursor.getColumnIndexOrThrow("peso");
            int edadIndex = cursor.getColumnIndexOrThrow("edad");
            int alturaIndex = cursor.getColumnIndexOrThrow("altura");
            int sexoIndex = cursor.getColumnIndexOrThrow("sexo");

            if (!cursor.isNull(pesoIndex)) {
                etPeso.setText(String.valueOf(cursor.getDouble(pesoIndex)));
            }
            if (!cursor.isNull(edadIndex)) {
                etEdad.setText(String.valueOf(cursor.getInt(edadIndex)));
            }
            if (!cursor.isNull(alturaIndex)) {
                etAltura.setText(String.valueOf(cursor.getDouble(alturaIndex)));
            }
            if (!cursor.isNull(sexoIndex)) {
                etSexo.setText(cursor.getString(sexoIndex));
            }
        }
        cursor.close();
    }

    private void setupListeners() {
        btnGuardarPerfil.setOnClickListener(v -> guardarPerfil());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void guardarPerfil() {
        String nombre = tvNombrePerfil.getText().toString();

        double peso = 0;
        int edad = 0;
        double altura = 0;
        String sexo = "";

        try {
            if (!etPeso.getText().toString().isEmpty()) {
                peso = Double.parseDouble(etPeso.getText().toString());
            }
            if (!etEdad.getText().toString().isEmpty()) {
                edad = Integer.parseInt(etEdad.getText().toString());
            }
            if (!etAltura.getText().toString().isEmpty()) {
                altura = Double.parseDouble(etAltura.getText().toString());
            }
            sexo = etSexo.getText().toString().trim();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valores inválidos", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean resultado = dbHelper.actualizarPerfil(userId, nombre, peso, edad, sexo, altura);

        if (resultado) {
            Toast.makeText(this, R.string.operacion_exitosa, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.error_operacion, Toast.LENGTH_SHORT).show();
        }
    }
}