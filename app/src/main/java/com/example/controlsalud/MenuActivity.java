package com.example.controlsalud;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    private ListView lvMenu;
    private List<MenuItem> menuItems;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = preferences.getString("userName", "Usuario");

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        tvWelcome.setText(getString(R.string.menu_title) + "\n" + userName);

        lvMenu = findViewById(R.id.lvMenu);
        setupMenu();
    }

    private void setupMenu() {
        menuItems = new ArrayList<>();

        // Agregar opciones del menú con iconos (usando iconos del sistema)
        menuItems.add(new MenuItem(
                getString(R.string.menu_ejercicios),
                android.R.drawable.ic_menu_compass
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_alimentos),
                android.R.drawable.ic_menu_gallery
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_registrar_ejercicio),
                android.R.drawable.ic_menu_add
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_registrar_alimento),
                android.R.drawable.ic_menu_edit
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_resumen),
                android.R.drawable.ic_menu_info_details
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_perfil),
                android.R.drawable.ic_menu_myplaces
        ));

        menuItems.add(new MenuItem(
                getString(R.string.menu_salir),
                android.R.drawable.ic_menu_close_clear_cancel
        ));

        MenuAdapter adapter = new MenuAdapter(this, menuItems);
        lvMenu.setAdapter(adapter);

        lvMenu.setOnItemClickListener((parent, view, position, id) -> {
            handleMenuClick(position);
        });
    }

    private void handleMenuClick(int position) {
        Intent intent;

        switch (position) {
            case 0: // Catálogo Ejercicios
                try {
                    intent = new Intent(this, EjerciciosActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al abrir Ejercicios: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;

            case 1: // Catálogo Alimentos
                try {
                    intent = new Intent(this, AlimentosActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al abrir Alimentos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;

            case 2: // Registrar Mi Ejercicio
                try {
                    intent = new Intent(this, RegistrarEjercicioActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al abrir Registrar Ejercicio: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;

            case 3: // Registrar Mi Alimento
                try {
                    intent = new Intent(this, RegistrarAlimentoActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Error al abrir Registrar Alimento: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;

            case 4: // Ver Resumen
                try {
                    intent = new Intent(this, ResumenActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Módulo Ver Resumen en desarrollo", Toast.LENGTH_SHORT).show();
                }
                break;

            case 5: // Mi Perfil
                try {
                    intent = new Intent(this, PerfilActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(this, "Módulo Mi Perfil en desarrollo", Toast.LENGTH_SHORT).show();
                }
                break;

            case 6: // Salir
                mostrarDialogoSalir();
                break;
        }
    }

    private void mostrarDialogoSalir() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_salir))
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton(R.string.si, (dialog, which) -> {
                    // Cerrar sesión
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();

                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    // Clase interna para los items del menú
    private static class MenuItem {
        String title;
        int icon;

        MenuItem(String title, int icon) {
            this.title = title;
            this.icon = icon;
        }
    }

    // Adapter personalizado para el ListView
    private static class MenuAdapter extends BaseAdapter {
        private Context context;
        private List<MenuItem> items;
        private LayoutInflater inflater;

        MenuAdapter(Context context, List<MenuItem> items) {
            this.context = context;
            this.items = items;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_menu, parent, false);
            }

            MenuItem item = items.get(position);

            ImageView ivIcon = convertView.findViewById(R.id.ivIcon);
            TextView tvTitle = convertView.findViewById(R.id.tvTitle);

            ivIcon.setImageResource(item.icon);
            tvTitle.setText(item.title);

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        mostrarDialogoSalir();
    }
}