package com.example.controlsalud;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;

public class ResumenActivity extends AppCompatActivity {

    private TextView tvCaloriasHoy, tvCaloriasSemana, tvCaloriasMes;
    private TextView tvConsumidasHoy, tvConsumidasSemana, tvConsumidasMes;
    private TextView tvBalanceHoy, tvBalanceSemana, tvBalanceMes;
    private Button btnVolver;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);

        initViews();
        cargarDatos();
        setupListeners();
    }

    private void initViews() {
        // Hoy
        tvCaloriasHoy = findViewById(R.id.tvCaloriasHoy);
        tvConsumidasHoy = findViewById(R.id.tvConsumidasHoy);
        tvBalanceHoy = findViewById(R.id.tvBalanceHoy);

        // Semana
        tvCaloriasSemana = findViewById(R.id.tvCaloriasSemana);
        tvConsumidasSemana = findViewById(R.id.tvConsumidasSemana);
        tvBalanceSemana = findViewById(R.id.tvBalanceSemana);

        // Mes
        tvCaloriasMes = findViewById(R.id.tvCaloriasMes);
        tvConsumidasMes = findViewById(R.id.tvConsumidasMes);
        tvBalanceMes = findViewById(R.id.tvBalanceMes);

        btnVolver = findViewById(R.id.btnVolver);
    }

    private void cargarDatos() {
        // Calorías de hoy
        double caloriasQuemadasHoy = dbHelper.calcularCaloriasQuemadasHoy(userId);
        double caloriasConsumidasHoy = dbHelper.calcularCaloriasConsumidasHoy(userId);
        double balanceHoy = caloriasConsumidasHoy - caloriasQuemadasHoy;

        tvCaloriasHoy.setText(String.format("%.0f cal", caloriasQuemadasHoy));
        tvConsumidasHoy.setText(String.format("%.0f cal", caloriasConsumidasHoy));
        tvBalanceHoy.setText(String.format("%.0f cal", balanceHoy));

        // Color del balance
        if (balanceHoy > 0) {
            tvBalanceHoy.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvBalanceHoy.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        // TODO: Implementar cálculos de semana y mes
        tvCaloriasSemana.setText("0 cal");
        tvConsumidasSemana.setText("0 cal");
        tvBalanceSemana.setText("0 cal");

        tvCaloriasMes.setText("0 cal");
        tvConsumidasMes.setText("0 cal");
        tvBalanceMes.setText("0 cal");
    }

    private void setupListeners() {
        btnVolver.setOnClickListener(v -> finish());
    }
}