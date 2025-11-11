package com.example.controlsalud;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class RegistrarEjercicioActivity extends AppCompatActivity {

    private TextView tvUsuario, tvNombreEjercicio, tvCaloriasQuemadas;
    private EditText etIdEjercicio, etRepeticiones, etDuracion;
    private Button btnBuscarEjercicio, btnAgregarLista, btnEditarLista, btnEliminarLista, btnGuardarTodo, btnVolver;
    private ListView lvEjercicios;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private int userId;
    private String userName;

    private List<EjercicioDetalle> listaEjercicios;
    private EjercicioDetalleAdapter adapter;
    private int posicionSeleccionada = -1;

    private int idEjercicioActual = -1;
    private String nombreEjercicioActual = "";
    private double caloriasPorMinuto = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_ejercicio);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);
        userName = preferences.getString("userName", "Usuario");

        if (userId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listaEjercicios = new ArrayList<>();

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreEjercicio = findViewById(R.id.tvNombreEjercicio);
        tvCaloriasQuemadas = findViewById(R.id.tvCaloriasQuemadas);

        etIdEjercicio = findViewById(R.id.etIdEjercicio);
        etRepeticiones = findViewById(R.id.etRepeticiones);
        etDuracion = findViewById(R.id.etDuracion);

        btnBuscarEjercicio = findViewById(R.id.btnBuscarEjercicio);
        btnAgregarLista = findViewById(R.id.btnAgregarLista);
        btnEditarLista = findViewById(R.id.btnEditarLista);
        btnEliminarLista = findViewById(R.id.btnEliminarLista);
        btnGuardarTodo = findViewById(R.id.btnGuardarTodo);
        btnVolver = findViewById(R.id.btnVolver);

        lvEjercicios = findViewById(R.id.lvEjercicios);

        tvUsuario.setText("Usuario: " + userName);

        adapter = new EjercicioDetalleAdapter(this, listaEjercicios);
        lvEjercicios.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBuscarEjercicio.setOnClickListener(v -> buscarEjercicio());
        btnAgregarLista.setOnClickListener(v -> agregarALista());
        btnEditarLista.setOnClickListener(v -> editarDeLista());
        btnEliminarLista.setOnClickListener(v -> eliminarDeLista());
        btnGuardarTodo.setOnClickListener(v -> guardarTodo());
        btnVolver.setOnClickListener(v -> finish());

        lvEjercicios.setOnItemClickListener((parent, view, position, id) -> {
            posicionSeleccionada = position;
            EjercicioDetalle detalle = listaEjercicios.get(position);
            cargarDetalleEnFormulario(detalle);

            btnAgregarLista.setEnabled(false);
            btnEditarLista.setEnabled(true);
            btnEliminarLista.setEnabled(true);
        });

        // Calcular calorías al cambiar duración
        etDuracion.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calcularCalorias();
            }
        });
    }

    private void buscarEjercicio() {
        String idStr = etIdEjercicio.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese ID del ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Cursor cursor = dbHelper.buscarEjercicio(id);

            if (cursor.moveToFirst()) {
                idEjercicioActual = id;
                nombreEjercicioActual = cursor.getString(cursor.getColumnIndexOrThrow("nombre_ejercicio"));
                caloriasPorMinuto = cursor.getDouble(cursor.getColumnIndexOrThrow("calorias_por_minuto"));

                tvNombreEjercicio.setText(nombreEjercicioActual);
                calcularCalorias();

                Toast.makeText(this, "Ejercicio encontrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Ejercicio no encontrado", Toast.LENGTH_SHORT).show();
                tvNombreEjercicio.setText("...");
                idEjercicioActual = -1;
            }
            cursor.close();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID debe ser un número", Toast.LENGTH_SHORT).show();
        }
    }

    private void calcularCalorias() {
        if (idEjercicioActual == -1) return;

        String duracionStr = etDuracion.getText().toString().trim();
        if (duracionStr.isEmpty()) {
            tvCaloriasQuemadas.setText("0.0 cal");
            return;
        }

        try {
            int duracion = Integer.parseInt(duracionStr);
            double calorias = caloriasPorMinuto * duracion;
            tvCaloriasQuemadas.setText(String.format("%.1f cal", calorias));
        } catch (NumberFormatException e) {
            tvCaloriasQuemadas.setText("0.0 cal");
        }
    }

    private void agregarALista() {
        if (!validarFormulario()) return;

        int repeticiones = etRepeticiones.getText().toString().isEmpty() ? 0 :
                Integer.parseInt(etRepeticiones.getText().toString().trim());
        int duracion = Integer.parseInt(etDuracion.getText().toString().trim());
        double calorias = caloriasPorMinuto * duracion;

        EjercicioDetalle detalle = new EjercicioDetalle(
                idEjercicioActual,
                nombreEjercicioActual,
                repeticiones,
                duracion,
                calorias
        );

        listaEjercicios.add(detalle);
        adapter.notifyDataSetChanged();

        limpiarFormulario();
        Toast.makeText(this, "Agregado a la lista", Toast.LENGTH_SHORT).show();
    }

    private void editarDeLista() {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, R.string.seleccione_registro, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarFormulario()) return;

        int repeticiones = etRepeticiones.getText().toString().isEmpty() ? 0 :
                Integer.parseInt(etRepeticiones.getText().toString().trim());
        int duracion = Integer.parseInt(etDuracion.getText().toString().trim());
        double calorias = caloriasPorMinuto * duracion;

        EjercicioDetalle detalle = new EjercicioDetalle(
                idEjercicioActual,
                nombreEjercicioActual,
                repeticiones,
                duracion,
                calorias
        );

        listaEjercicios.set(posicionSeleccionada, detalle);
        adapter.notifyDataSetChanged();

        limpiarFormulario();
        Toast.makeText(this, "Registro actualizado", Toast.LENGTH_SHORT).show();
    }

    private void eliminarDeLista() {
        if (posicionSeleccionada == -1) {
            Toast.makeText(this, R.string.seleccione_registro, Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.eliminar))
                .setMessage(R.string.confirmar_eliminar)
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    listaEjercicios.remove(posicionSeleccionada);
                    adapter.notifyDataSetChanged();
                    limpiarFormulario();
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void guardarTodo() {
        if (listaEjercicios.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un ejercicio", Toast.LENGTH_SHORT).show();
            return;
        }

        int registrosGuardados = 0;

        for (EjercicioDetalle detalle : listaEjercicios) {
            long resultado = dbHelper.insertarEjercicioUsuario(
                    userId,
                    detalle.idEjercicio,
                    detalle.repeticiones,
                    detalle.duracion
            );

            if (resultado > 0) {
                registrosGuardados++;
            }
        }

        if (registrosGuardados > 0) {
            Toast.makeText(this, "Se guardaron " + registrosGuardados + " ejercicios", Toast.LENGTH_LONG).show();
            listaEjercicios.clear();
            adapter.notifyDataSetChanged();
            limpiarFormulario();
        } else {
            Toast.makeText(this, R.string.error_operacion, Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDetalleEnFormulario(EjercicioDetalle detalle) {
        etIdEjercicio.setText(String.valueOf(detalle.idEjercicio));
        etRepeticiones.setText(String.valueOf(detalle.repeticiones));
        etDuracion.setText(String.valueOf(detalle.duracion));

        idEjercicioActual = detalle.idEjercicio;
        nombreEjercicioActual = detalle.nombreEjercicio;

        // Buscar calorías por minuto
        Cursor cursor = dbHelper.buscarEjercicio(detalle.idEjercicio);
        if (cursor.moveToFirst()) {
            caloriasPorMinuto = cursor.getDouble(cursor.getColumnIndexOrThrow("calorias_por_minuto"));
        }
        cursor.close();

        tvNombreEjercicio.setText(nombreEjercicioActual);
        tvCaloriasQuemadas.setText(String.format("%.1f cal", detalle.calorias));
    }

    private void limpiarFormulario() {
        etIdEjercicio.setText("");
        etRepeticiones.setText("");
        etDuracion.setText("");
        tvNombreEjercicio.setText("...");
        tvCaloriasQuemadas.setText("0.0 cal");

        idEjercicioActual = -1;
        nombreEjercicioActual = "";
        caloriasPorMinuto = 0;
        posicionSeleccionada = -1;

        btnAgregarLista.setEnabled(true);
        btnEditarLista.setEnabled(false);
        btnEliminarLista.setEnabled(false);
    }

    private boolean validarFormulario() {
        if (idEjercicioActual == -1) {
            Toast.makeText(this, "Busque un ejercicio válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etDuracion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese la duración", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            int duracion = Integer.parseInt(etDuracion.getText().toString().trim());
            if (duracion <= 0) {
                Toast.makeText(this, "La duración debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Duración inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Clase para almacenar detalles del ejercicio
    private static class EjercicioDetalle {
        int idEjercicio;
        String nombreEjercicio;
        int repeticiones;
        int duracion;
        double calorias;

        EjercicioDetalle(int idEjercicio, String nombreEjercicio, int repeticiones, int duracion, double calorias) {
            this.idEjercicio = idEjercicio;
            this.nombreEjercicio = nombreEjercicio;
            this.repeticiones = repeticiones;
            this.duracion = duracion;
            this.calorias = calorias;
        }
    }

    // Adapter para el ListView
    private static class EjercicioDetalleAdapter extends BaseAdapter {
        private Context context;
        private List<EjercicioDetalle> lista;
        private LayoutInflater inflater;

        EjercicioDetalleAdapter(Context context, List<EjercicioDetalle> lista) {
            this.context = context;
            this.lista = lista;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lista.size();
        }

        @Override
        public Object getItem(int position) {
            return lista.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            EjercicioDetalle detalle = lista.get(position);

            TextView text1 = convertView.findViewById(android.R.id.text1);
            TextView text2 = convertView.findViewById(android.R.id.text2);

            text1.setText(detalle.nombreEjercicio);
            text2.setText(String.format("%d min | %.1f cal", detalle.duracion, detalle.calorias));

            return convertView;
        }
    }
}
