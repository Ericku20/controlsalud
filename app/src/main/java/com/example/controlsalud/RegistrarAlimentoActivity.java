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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.controlsalud.database.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class RegistrarAlimentoActivity extends AppCompatActivity {

    private TextView tvUsuario, tvNombreAlimento, tvCaloriasConsumidas;
    private EditText etIdAlimento, etCantidad;
    private Spinner spinnerTipoComida;
    private Button btnBuscarAlimento, btnAgregarLista, btnEditarLista, btnEliminarLista, btnGuardarTodo, btnVolver;
    private ListView lvAlimentos;

    private DatabaseHelper dbHelper;
    private SharedPreferences preferences;
    private int userId;
    private String userName;

    private List<AlimentoDetalle> listaAlimentos;
    private AlimentoDetalleAdapter adapter;
    private int posicionSeleccionada = -1;

    private int idAlimentoActual = -1;
    private String nombreAlimentoActual = "";
    private double caloriasPor100g = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_alimento);

        dbHelper = new DatabaseHelper(this);
        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = preferences.getInt("userId", -1);
        userName = preferences.getString("userName", "Usuario");

        if (userId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        listaAlimentos = new ArrayList<>();

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvUsuario = findViewById(R.id.tvUsuario);
        tvNombreAlimento = findViewById(R.id.tvNombreAlimento);
        tvCaloriasConsumidas = findViewById(R.id.tvCaloriasConsumidas);

        etIdAlimento = findViewById(R.id.etIdAlimento);
        etCantidad = findViewById(R.id.etCantidad);
        spinnerTipoComida = findViewById(R.id.spinnerTipoComida);

        btnBuscarAlimento = findViewById(R.id.btnBuscarAlimento);
        btnAgregarLista = findViewById(R.id.btnAgregarLista);
        btnEditarLista = findViewById(R.id.btnEditarLista);
        btnEliminarLista = findViewById(R.id.btnEliminarLista);
        btnGuardarTodo = findViewById(R.id.btnGuardarTodo);
        btnVolver = findViewById(R.id.btnVolver);

        lvAlimentos = findViewById(R.id.lvAlimentos);

        tvUsuario.setText("Usuario: " + userName);

        adapter = new AlimentoDetalleAdapter(this, listaAlimentos);
        lvAlimentos.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBuscarAlimento.setOnClickListener(v -> buscarAlimento());
        btnAgregarLista.setOnClickListener(v -> agregarALista());
        btnEditarLista.setOnClickListener(v -> editarDeLista());
        btnEliminarLista.setOnClickListener(v -> eliminarDeLista());
        btnGuardarTodo.setOnClickListener(v -> guardarTodo());
        btnVolver.setOnClickListener(v -> finish());

        lvAlimentos.setOnItemClickListener((parent, view, position, id) -> {
            posicionSeleccionada = position;
            AlimentoDetalle detalle = listaAlimentos.get(position);
            cargarDetalleEnFormulario(detalle);

            btnAgregarLista.setEnabled(false);
            btnEditarLista.setEnabled(true);
            btnEliminarLista.setEnabled(true);
        });

        // Calcular calorías al cambiar cantidad
        etCantidad.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calcularCalorias();
            }
        });
    }

    private void buscarAlimento() {
        String idStr = etIdAlimento.getText().toString().trim();

        if (idStr.isEmpty()) {
            Toast.makeText(this, "Ingrese ID del alimento", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            Cursor cursor = dbHelper.buscarAlimento(id);

            if (cursor.moveToFirst()) {
                idAlimentoActual = id;
                nombreAlimentoActual = cursor.getString(cursor.getColumnIndexOrThrow("nombre_alimento"));
                caloriasPor100g = cursor.getDouble(cursor.getColumnIndexOrThrow("calorias_por_100g"));

                tvNombreAlimento.setText(nombreAlimentoActual);
                calcularCalorias();

                Toast.makeText(this, "Alimento encontrado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Alimento no encontrado", Toast.LENGTH_SHORT).show();
                tvNombreAlimento.setText("...");
                idAlimentoActual = -1;
            }
            cursor.close();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID debe ser un número", Toast.LENGTH_SHORT).show();
        }
    }

    private void calcularCalorias() {
        if (idAlimentoActual == -1) return;

        String cantidadStr = etCantidad.getText().toString().trim();
        if (cantidadStr.isEmpty()) {
            tvCaloriasConsumidas.setText("0.0 cal");
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            double calorias = (cantidad / 100.0) * caloriasPor100g;
            tvCaloriasConsumidas.setText(String.format("%.1f cal", calorias));
        } catch (NumberFormatException e) {
            tvCaloriasConsumidas.setText("0.0 cal");
        }
    }

    private void agregarALista() {
        if (!validarFormulario()) return;

        String tipoComida = spinnerTipoComida.getSelectedItem().toString();
        double cantidad = Double.parseDouble(etCantidad.getText().toString().trim());
        double calorias = (cantidad / 100.0) * caloriasPor100g;

        AlimentoDetalle detalle = new AlimentoDetalle(
                idAlimentoActual,
                nombreAlimentoActual,
                tipoComida,
                cantidad,
                calorias
        );

        listaAlimentos.add(detalle);
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

        String tipoComida = spinnerTipoComida.getSelectedItem().toString();
        double cantidad = Double.parseDouble(etCantidad.getText().toString().trim());
        double calorias = (cantidad / 100.0) * caloriasPor100g;

        AlimentoDetalle detalle = new AlimentoDetalle(
                idAlimentoActual,
                nombreAlimentoActual,
                tipoComida,
                cantidad,
                calorias
        );

        listaAlimentos.set(posicionSeleccionada, detalle);
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
                    listaAlimentos.remove(posicionSeleccionada);
                    adapter.notifyDataSetChanged();
                    limpiarFormulario();
                    Toast.makeText(this, "Registro eliminado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    private void guardarTodo() {
        if (listaAlimentos.isEmpty()) {
            Toast.makeText(this, "Agregue al menos un alimento", Toast.LENGTH_SHORT).show();
            return;
        }

        int registrosGuardados = 0;

        for (AlimentoDetalle detalle : listaAlimentos) {
            long resultado = dbHelper.insertarAlimentoUsuario(
                    userId,
                    detalle.idAlimento,
                    detalle.tipoComida,
                    detalle.cantidad
            );

            if (resultado > 0) {
                registrosGuardados++;
            }
        }

        if (registrosGuardados > 0) {
            Toast.makeText(this, "Se guardaron " + registrosGuardados + " alimentos", Toast.LENGTH_LONG).show();
            listaAlimentos.clear();
            adapter.notifyDataSetChanged();
            limpiarFormulario();
        } else {
            Toast.makeText(this, R.string.error_operacion, Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarDetalleEnFormulario(AlimentoDetalle detalle) {
        etIdAlimento.setText(String.valueOf(detalle.idAlimento));
        etCantidad.setText(String.valueOf(detalle.cantidad));

        // Seleccionar tipo de comida en el spinner
        for (int i = 0; i < spinnerTipoComida.getCount(); i++) {
            if (spinnerTipoComida.getItemAtPosition(i).toString().equals(detalle.tipoComida)) {
                spinnerTipoComida.setSelection(i);
                break;
            }
        }

        idAlimentoActual = detalle.idAlimento;
        nombreAlimentoActual = detalle.nombreAlimento;

        // Buscar calorías por 100g
        Cursor cursor = dbHelper.buscarAlimento(detalle.idAlimento);
        if (cursor.moveToFirst()) {
            caloriasPor100g = cursor.getDouble(cursor.getColumnIndexOrThrow("calorias_por_100g"));
        }
        cursor.close();

        tvNombreAlimento.setText(nombreAlimentoActual);
        tvCaloriasConsumidas.setText(String.format("%.1f cal", detalle.calorias));
    }

    private void limpiarFormulario() {
        etIdAlimento.setText("");
        etCantidad.setText("");
        spinnerTipoComida.setSelection(0);
        tvNombreAlimento.setText("...");
        tvCaloriasConsumidas.setText("0.0 cal");

        idAlimentoActual = -1;
        nombreAlimentoActual = "";
        caloriasPor100g = 0;
        posicionSeleccionada = -1;

        btnAgregarLista.setEnabled(true);
        btnEditarLista.setEnabled(false);
        btnEliminarLista.setEnabled(false);
    }

    private boolean validarFormulario() {
        if (idAlimentoActual == -1) {
            Toast.makeText(this, "Busque un alimento válido", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etCantidad.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Ingrese la cantidad", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double cantidad = Double.parseDouble(etCantidad.getText().toString().trim());
            if (cantidad <= 0) {
                Toast.makeText(this, "La cantidad debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Clase para almacenar detalles del alimento
    private static class AlimentoDetalle {
        int idAlimento;
        String nombreAlimento;
        String tipoComida;
        double cantidad;
        double calorias;

        AlimentoDetalle(int idAlimento, String nombreAlimento, String tipoComida, double cantidad, double calorias) {
            this.idAlimento = idAlimento;
            this.nombreAlimento = nombreAlimento;
            this.tipoComida = tipoComida;
            this.cantidad = cantidad;
            this.calorias = calorias;
        }
    }

    // Adapter para el ListView
    private static class AlimentoDetalleAdapter extends BaseAdapter {
        private Context context;
        private List<AlimentoDetalle> lista;
        private LayoutInflater inflater;

        AlimentoDetalleAdapter(Context context, List<AlimentoDetalle> lista) {
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

            AlimentoDetalle detalle = lista.get(position);

            TextView text1 = convertView.findViewById(android.R.id.text1);
            TextView text2 = convertView.findViewById(android.R.id.text2);

            text1.setText(detalle.nombreAlimento + " - " + detalle.tipoComida);
            text2.setText(String.format("%.0fg | %.1f cal", detalle.cantidad, detalle.calorias));

            return convertView;
        }
    }
}