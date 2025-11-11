package com.example.controlsalud;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;

public class EjerciciosActivity extends AppCompatActivity {

    private EditText etId, etNombre, etDescripcion, etCalorias, etCategoria, etBuscar;
    private Button btnAgregar, btnEditar, btnEliminar, btnLimpiar, btnBuscar, btnVolver;
    private ListView lvEjercicios;
    private DatabaseHelper dbHelper;
    private SimpleCursorAdapter adapter;
    private int idSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_ejercicios);
            dbHelper = new DatabaseHelper(this);

            initViews();
            setupListeners();
            cargarLista();

        } catch (Exception e) {
            Toast.makeText(this, "Error al iniciar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private void initViews() {
        etId = findViewById(R.id.etId);
        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etCalorias = findViewById(R.id.etCalorias);
        etCategoria = findViewById(R.id.etCategoria);
        etBuscar = findViewById(R.id.etBuscar);

        btnAgregar = findViewById(R.id.btnAgregar);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVolver = findViewById(R.id.btnVolver);

        lvEjercicios = findViewById(R.id.lvEjercicios);
    }

    private void setupListeners() {
        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregar();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editar();
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmarEliminar();
            }
        });

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limpiarCampos();
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscar();
            }
        });

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lvEjercicios.setOnItemClickListener((parent, view, position, id) -> {
            Cursor cursor = (Cursor) adapter.getItem(position);
            if (cursor != null) {
                cargarDatos(cursor);
            }
        });
    }

    private void cargarLista() {
        try {
            Cursor cursor = dbHelper.obtenerTodosEjercicios();

            String[] from = {"nombre_ejercicio", "calorias_por_minuto"};
            int[] to = {android.R.id.text1, android.R.id.text2};

            adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    from,
                    to,
                    0
            );

            adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(android.view.View view, Cursor cursor, int columnIndex) {
                    if (view.getId() == android.R.id.text2) {
                        double cal = cursor.getDouble(columnIndex);
                        ((android.widget.TextView) view).setText(String.format("%.1f cal/min", cal));
                        return true;
                    }
                    return false;
                }
            });

            lvEjercicios.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void cargarDatos(Cursor cursor) {
        try {
            idSeleccionado = cursor.getInt(cursor.getColumnIndexOrThrow("id_ejercicio"));

            etId.setText(String.valueOf(idSeleccionado));
            etNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombre_ejercicio")));

            int descIndex = cursor.getColumnIndexOrThrow("descripcion");
            if (!cursor.isNull(descIndex)) {
                etDescripcion.setText(cursor.getString(descIndex));
            }

            etCalorias.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("calorias_por_minuto"))));

            int catIndex = cursor.getColumnIndexOrThrow("categoria");
            if (!cursor.isNull(catIndex)) {
                etCategoria.setText(cursor.getString(catIndex));
            }

            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void agregar() {
        if (!validarCampos()) return;

        try {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            double calorias = Double.parseDouble(etCalorias.getText().toString().trim());
            String categoria = etCategoria.getText().toString().trim();

            long resultado = dbHelper.insertarEjercicio(nombre, descripcion, calorias, categoria);

            if (resultado > 0) {
                Toast.makeText(this, "Ejercicio agregado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarLista();
            } else {
                Toast.makeText(this, "Error al agregar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void editar() {
        if (!validarCampos() || idSeleccionado == -1) return;

        try {
            String nombre = etNombre.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();
            double calorias = Double.parseDouble(etCalorias.getText().toString().trim());
            String categoria = etCategoria.getText().toString().trim();

            int resultado = dbHelper.actualizarEjercicio(idSeleccionado, nombre, descripcion, calorias, categoria);

            if (resultado > 0) {
                Toast.makeText(this, "Ejercicio actualizado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarLista();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void confirmarEliminar() {
        if (idSeleccionado == -1) {
            Toast.makeText(this, "Seleccione un registro", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Está seguro de eliminar este registro?")
                .setPositiveButton("Sí", (dialog, which) -> eliminar())
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminar() {
        try {
            int resultado = dbHelper.eliminarEjercicio(idSeleccionado);

            if (resultado > 0) {
                Toast.makeText(this, "Ejercicio eliminado", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                cargarLista();
            } else {
                Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void buscar() {
        String busqueda = etBuscar.getText().toString().trim();

        if (busqueda.isEmpty()) {
            cargarLista();
            return;
        }

        try {
            Cursor cursor = dbHelper.buscarEjercicioPorNombre(busqueda);

            String[] from = {"nombre_ejercicio", "calorias_por_minuto"};
            int[] to = {android.R.id.text1, android.R.id.text2};

            adapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_2,
                    cursor,
                    from,
                    to,
                    0
            );

            lvEjercicios.setAdapter(adapter);
        } catch (Exception e) {
            Toast.makeText(this, "Error en búsqueda", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void limpiarCampos() {
        etId.setText("");
        etNombre.setText("");
        etDescripcion.setText("");
        etCalorias.setText("");
        etCategoria.setText("");
        etBuscar.setText("");

        idSeleccionado = -1;

        btnAgregar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }

    private boolean validarCampos() {
        if (etNombre.getText().toString().trim().isEmpty() ||
                etCalorias.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            Double.parseDouble(etCalorias.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Calorías debe ser un número", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
    }
}