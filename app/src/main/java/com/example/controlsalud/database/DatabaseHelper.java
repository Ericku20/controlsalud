package com.example.controlsalud.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ControlSaludDB.db";
    private static final int DATABASE_VERSION = 1;

    // ==================== TABLA USUARIOS ====================
    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COL_USER_ID = "id_usuario";
    private static final String COL_USER_NOMBRE = "nombre";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";
    private static final String COL_USER_PESO = "peso";
    private static final String COL_USER_EDAD = "edad";
    private static final String COL_USER_SEXO = "sexo";
    private static final String COL_USER_ALTURA = "altura";

    // ==================== TABLA EJERCICIOS (CATÁLOGO - CRUD 1) ====================
    private static final String TABLE_EJERCICIOS = "ejercicios";
    private static final String COL_EJER_ID = "id_ejercicio";
    private static final String COL_EJER_NOMBRE = "nombre_ejercicio";
    private static final String COL_EJER_DESCRIPCION = "descripcion";
    private static final String COL_EJER_CAL_MIN = "calorias_por_minuto";
    private static final String COL_EJER_CATEGORIA = "categoria";

    // ==================== TABLA ALIMENTOS (CATÁLOGO - CRUD 2) ====================
    private static final String TABLE_ALIMENTOS = "alimentos";
    private static final String COL_ALIM_ID = "id_alimento";
    private static final String COL_ALIM_NOMBRE = "nombre_alimento";
    private static final String COL_ALIM_DESCRIPCION = "descripcion";
    private static final String COL_ALIM_CAL_100G = "calorias_por_100g";
    private static final String COL_ALIM_CATEGORIA = "categoria";

    // ==================== TABLA EJERCICIOS_USUARIO (MAESTRO-DETALLE) ====================
    private static final String TABLE_EJERCICIOS_USUARIO = "ejercicios_usuario";
    private static final String COL_EU_ID = "id_registro";
    private static final String COL_EU_USER_ID = "id_usuario";
    private static final String COL_EU_EJER_ID = "id_ejercicio";
    private static final String COL_EU_REPETICIONES = "repeticiones";
    private static final String COL_EU_DURACION = "duracion_minutos";
    private static final String COL_EU_CALORIAS = "calorias_quemadas";
    private static final String COL_EU_FECHA = "fecha";
    private static final String COL_EU_HORA = "hora";

    // ==================== TABLA ALIMENTOS_USUARIO ====================
    private static final String TABLE_ALIMENTOS_USUARIO = "alimentos_usuario";
    private static final String COL_AU_ID = "id_consumo";
    private static final String COL_AU_USER_ID = "id_usuario";
    private static final String COL_AU_ALIM_ID = "id_alimento";
    private static final String COL_AU_TIPO_COMIDA = "tipo_comida";
    private static final String COL_AU_CANTIDAD = "cantidad_gramos";
    private static final String COL_AU_CALORIAS = "calorias_consumidas";
    private static final String COL_AU_FECHA = "fecha";
    private static final String COL_AU_HORA = "hora";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla USUARIOS
        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NOMBRE + " TEXT NOT NULL, " +
                COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_PESO + " REAL, " +
                COL_USER_EDAD + " INTEGER, " +
                COL_USER_SEXO + " TEXT, " +
                COL_USER_ALTURA + " REAL)";
        db.execSQL(createUsuarios);

        // Crear tabla EJERCICIOS (CRUD 1)
        String createEjercicios = "CREATE TABLE " + TABLE_EJERCICIOS + " (" +
                COL_EJER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EJER_NOMBRE + " TEXT NOT NULL UNIQUE, " +
                COL_EJER_DESCRIPCION + " TEXT, " +
                COL_EJER_CAL_MIN + " REAL NOT NULL, " +
                COL_EJER_CATEGORIA + " TEXT)";
        db.execSQL(createEjercicios);

        // Crear tabla ALIMENTOS (CRUD 2)
        String createAlimentos = "CREATE TABLE " + TABLE_ALIMENTOS + " (" +
                COL_ALIM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ALIM_NOMBRE + " TEXT NOT NULL UNIQUE, " +
                COL_ALIM_DESCRIPCION + " TEXT, " +
                COL_ALIM_CAL_100G + " REAL NOT NULL, " +
                COL_ALIM_CATEGORIA + " TEXT)";
        db.execSQL(createAlimentos);

        // Crear tabla EJERCICIOS_USUARIO (MAESTRO-DETALLE)
        String createEjerciciosUsuario = "CREATE TABLE " + TABLE_EJERCICIOS_USUARIO + " (" +
                COL_EU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EU_USER_ID + " INTEGER NOT NULL, " +
                COL_EU_EJER_ID + " INTEGER NOT NULL, " +
                COL_EU_REPETICIONES + " INTEGER, " +
                COL_EU_DURACION + " INTEGER NOT NULL, " +
                COL_EU_CALORIAS + " REAL, " +
                COL_EU_FECHA + " TEXT NOT NULL, " +
                COL_EU_HORA + " TEXT, " +
                "FOREIGN KEY(" + COL_EU_USER_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_EU_EJER_ID + ") REFERENCES " + TABLE_EJERCICIOS + "(" + COL_EJER_ID + "))";
        db.execSQL(createEjerciciosUsuario);

        // Crear tabla ALIMENTOS_USUARIO
        String createAlimentosUsuario = "CREATE TABLE " + TABLE_ALIMENTOS_USUARIO + " (" +
                COL_AU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AU_USER_ID + " INTEGER NOT NULL, " +
                COL_AU_ALIM_ID + " INTEGER NOT NULL, " +
                COL_AU_TIPO_COMIDA + " TEXT NOT NULL, " +
                COL_AU_CANTIDAD + " REAL NOT NULL, " +
                COL_AU_CALORIAS + " REAL, " +
                COL_AU_FECHA + " TEXT NOT NULL, " +
                COL_AU_HORA + " TEXT, " +
                "FOREIGN KEY(" + COL_AU_USER_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USER_ID + "), " +
                "FOREIGN KEY(" + COL_AU_ALIM_ID + ") REFERENCES " + TABLE_ALIMENTOS + "(" + COL_ALIM_ID + "))";
        db.execSQL(createAlimentosUsuario);

        // Insertar datos iniciales
        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALIMENTOS_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EJERCICIOS_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALIMENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EJERCICIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        // Insertar ejercicios de ejemplo
        insertarEjercicioInicial(db, "Correr", "Carrera continua", 8.0, "Cardio");
        insertarEjercicioInicial(db, "Caminar", "Caminata ligera", 3.5, "Cardio");
        insertarEjercicioInicial(db, "Nadar", "Natación", 10.0, "Cardio");
        insertarEjercicioInicial(db, "Pesas", "Levantamiento de pesas", 6.0, "Fuerza");
        insertarEjercicioInicial(db, "Yoga", "Yoga y estiramientos", 2.5, "Flexibilidad");
        insertarEjercicioInicial(db, "Ciclismo", "Bicicleta", 7.5, "Cardio");

        // Insertar alimentos de ejemplo
        insertarAlimentoInicial(db, "Arroz Blanco", "Arroz cocido", 130.0, "Cereales");
        insertarAlimentoInicial(db, "Pollo", "Pechuga de pollo", 165.0, "Proteínas");
        insertarAlimentoInicial(db, "Ensalada", "Ensalada verde", 20.0, "Vegetales");
        insertarAlimentoInicial(db, "Manzana", "Manzana roja", 52.0, "Frutas");
        insertarAlimentoInicial(db, "Pan Integral", "Pan de trigo", 247.0, "Cereales");
        insertarAlimentoInicial(db, "Huevo", "Huevo cocido", 155.0, "Proteínas");
    }

    private void insertarEjercicioInicial(SQLiteDatabase db, String nombre, String desc, double cal, String cat) {
        ContentValues values = new ContentValues();
        values.put(COL_EJER_NOMBRE, nombre);
        values.put(COL_EJER_DESCRIPCION, desc);
        values.put(COL_EJER_CAL_MIN, cal);
        values.put(COL_EJER_CATEGORIA, cat);
        db.insert(TABLE_EJERCICIOS, null, values);
    }

    private void insertarAlimentoInicial(SQLiteDatabase db, String nombre, String desc, double cal, String cat) {
        ContentValues values = new ContentValues();
        values.put(COL_ALIM_NOMBRE, nombre);
        values.put(COL_ALIM_DESCRIPCION, desc);
        values.put(COL_ALIM_CAL_100G, cal);
        values.put(COL_ALIM_CATEGORIA, cat);
        db.insert(TABLE_ALIMENTOS, null, values);
    }

    // ==================== MÉTODOS USUARIOS ====================
    public long registrarUsuario(String nombre, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NOMBRE, nombre);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, password);
        return db.insert(TABLE_USUARIOS, null, values);
    }

    public Cursor login(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USUARIOS, null,
                COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
    }

    public Cursor obtenerPerfil(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USUARIOS, null, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)}, null, null, null);
    }

    public boolean actualizarPerfil(int userId, String nombre, double peso, int edad, String sexo, double altura) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NOMBRE, nombre);
        if (peso > 0) values.put(COL_USER_PESO, peso);
        if (edad > 0) values.put(COL_USER_EDAD, edad);
        if (!sexo.isEmpty()) values.put(COL_USER_SEXO, sexo);
        if (altura > 0) values.put(COL_USER_ALTURA, altura);
        int rows = db.update(TABLE_USUARIOS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        return rows > 0;
    }

    // ==================== MÉTODOS EJERCICIOS (CRUD 1) ====================
    public long insertarEjercicio(String nombre, String descripcion, double calMin, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EJER_NOMBRE, nombre);
        values.put(COL_EJER_DESCRIPCION, descripcion);
        values.put(COL_EJER_CAL_MIN, calMin);
        values.put(COL_EJER_CATEGORIA, categoria);
        return db.insert(TABLE_EJERCICIOS, null, values);
    }

    public Cursor obtenerTodosEjercicios() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EJERCICIOS, null, null, null, null, null, COL_EJER_NOMBRE);
    }

    public Cursor buscarEjercicio(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EJERCICIOS, null, COL_EJER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor buscarEjercicioPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_EJERCICIOS, null, COL_EJER_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"}, null, null, null);
    }

    public int actualizarEjercicio(int id, String nombre, String descripcion, double calMin, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EJER_NOMBRE, nombre);
        values.put(COL_EJER_DESCRIPCION, descripcion);
        values.put(COL_EJER_CAL_MIN, calMin);
        values.put(COL_EJER_CATEGORIA, categoria);
        return db.update(TABLE_EJERCICIOS, values, COL_EJER_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarEjercicio(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EJERCICIOS, COL_EJER_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ==================== MÉTODOS ALIMENTOS (CRUD 2) ====================
    public long insertarAlimento(String nombre, String descripcion, double cal100g, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ALIM_NOMBRE, nombre);
        values.put(COL_ALIM_DESCRIPCION, descripcion);
        values.put(COL_ALIM_CAL_100G, cal100g);
        values.put(COL_ALIM_CATEGORIA, categoria);
        return db.insert(TABLE_ALIMENTOS, null, values);
    }

    public Cursor obtenerTodosAlimentos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ALIMENTOS, null, null, null, null, null, COL_ALIM_NOMBRE);
    }

    public Cursor buscarAlimento(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ALIMENTOS, null, COL_ALIM_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null);
    }

    public Cursor buscarAlimentoPorNombre(String nombre) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ALIMENTOS, null, COL_ALIM_NOMBRE + " LIKE ?",
                new String[]{"%" + nombre + "%"}, null, null, null);
    }

    public int actualizarAlimento(int id, String nombre, String descripcion, double cal100g, String categoria) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ALIM_NOMBRE, nombre);
        values.put(COL_ALIM_DESCRIPCION, descripcion);
        values.put(COL_ALIM_CAL_100G, cal100g);
        values.put(COL_ALIM_CATEGORIA, categoria);
        return db.update(TABLE_ALIMENTOS, values, COL_ALIM_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public int eliminarAlimento(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ALIMENTOS, COL_ALIM_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    // ==================== MÉTODOS EJERCICIOS_USUARIO (MAESTRO-DETALLE) ====================
    public long insertarEjercicioUsuario(int userId, int ejercicioId, int repeticiones, int duracion) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Obtener calorías por minuto del ejercicio
        Cursor c = buscarEjercicio(ejercicioId);
        double calMin = 0;
        if (c.moveToFirst()) {
            calMin = c.getDouble(c.getColumnIndexOrThrow(COL_EJER_CAL_MIN));
        }
        c.close();

        double caloriasQuemadas = calMin * duracion;

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(COL_EU_USER_ID, userId);
        values.put(COL_EU_EJER_ID, ejercicioId);
        values.put(COL_EU_REPETICIONES, repeticiones);
        values.put(COL_EU_DURACION, duracion);
        values.put(COL_EU_CALORIAS, caloriasQuemadas);
        values.put(COL_EU_FECHA, fecha);
        values.put(COL_EU_HORA, hora);

        return db.insert(TABLE_EJERCICIOS_USUARIO, null, values);
    }

    public Cursor obtenerEjerciciosUsuario(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT eu.*, e." + COL_EJER_NOMBRE +
                " FROM " + TABLE_EJERCICIOS_USUARIO + " eu " +
                "INNER JOIN " + TABLE_EJERCICIOS + " e ON eu." + COL_EU_EJER_ID + " = e." + COL_EJER_ID +
                " WHERE eu." + COL_EU_USER_ID + " = ?" +
                " ORDER BY eu." + COL_EU_FECHA + " DESC, eu." + COL_EU_HORA + " DESC";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    public double calcularCaloriasQuemadasHoy(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String query = "SELECT SUM(" + COL_EU_CALORIAS + ") FROM " + TABLE_EJERCICIOS_USUARIO +
                " WHERE " + COL_EU_USER_ID + " = ? AND " + COL_EU_FECHA + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId), fecha});
        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }

    // ==================== MÉTODOS ALIMENTOS_USUARIO ====================
    public long insertarAlimentoUsuario(int userId, int alimentoId, String tipoComida, double cantidad) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Obtener calorías por 100g del alimento
        Cursor c = buscarAlimento(alimentoId);
        double cal100g = 0;
        if (c.moveToFirst()) {
            cal100g = c.getDouble(c.getColumnIndexOrThrow(COL_ALIM_CAL_100G));
        }
        c.close();

        double caloriasConsumidas = (cantidad / 100.0) * cal100g;

        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(COL_AU_USER_ID, userId);
        values.put(COL_AU_ALIM_ID, alimentoId);
        values.put(COL_AU_TIPO_COMIDA, tipoComida);
        values.put(COL_AU_CANTIDAD, cantidad);
        values.put(COL_AU_CALORIAS, caloriasConsumidas);
        values.put(COL_AU_FECHA, fecha);
        values.put(COL_AU_HORA, hora);

        return db.insert(TABLE_ALIMENTOS_USUARIO, null, values);
    }

    public double calcularCaloriasConsumidasHoy(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String query = "SELECT SUM(" + COL_AU_CALORIAS + ") FROM " + TABLE_ALIMENTOS_USUARIO +
                " WHERE " + COL_AU_USER_ID + " = ? AND " + COL_AU_FECHA + " = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(userId), fecha});
        double total = 0;
        if (c.moveToFirst()) {
            total = c.getDouble(0);
        }
        c.close();
        return total;
    }
}