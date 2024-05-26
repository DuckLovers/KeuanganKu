package com.example.keuanganku;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    // Nama database dan versi
    private static final String DATABASE_NAME = "KeuanganKu.db";
    private static final int DATABASE_VERSION = 1;

    // Nama-nama tabel dan kolom
    // Tabel Pengguna
    private static final String TABLE_USER = "Pengguna";
    private static final String COLUMN_USER_ID = "idPengguna";
    private static final String COLUMN_USER_NAME = "nama";
    private static final String COLUMN_USER_AGE = "umur";

    // Tabel Kategori
    private static final String TABLE_CATEGORY = "Kategori";
    private static final String COLUMN_CATEGORY_ID = "idKategori";
    private static final String COLUMN_CATEGORY_NAME = "namaKategori";
    private static final String COLUMN_CATEGORY_TYPE = "jenis";

    // Tabel Transaksi
    private static final String TABLE_TRANSACTION = "Transaksi";
    private static final String COLUMN_TRANSACTION_ID = "idTransaksi";
    private static final String COLUMN_TRANSACTION_USER_ID = "idPengguna";
    private static final String COLUMN_TRANSACTION_NAME = "namaTransaksi";
    private static final String COLUMN_TRANSACTION_DATE = "tanggal";
    private static final String COLUMN_TRANSACTION_CATEGORY_ID = "idKategori";
    private static final String COLUMN_TRANSACTION_AMOUNT = "nominal";
    private static final String COLUMN_TRANSACTION_TYPE = "jenis";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel Pengguna
        String createUserTableQuery = "CREATE TABLE " + TABLE_USER + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_NAME + " TEXT NOT NULL, " +
                COLUMN_USER_AGE + " INTEGER NOT NULL)";
        db.execSQL(createUserTableQuery);

        // Membuat tabel Kategori
        String createCategoryTableQuery = "CREATE TABLE " + TABLE_CATEGORY + " (" +
                COLUMN_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_CATEGORY_NAME + " TEXT NOT NULL, " +
                COLUMN_CATEGORY_TYPE + " TEXT CHECK (" + COLUMN_CATEGORY_TYPE + " IN ('pemasukan', 'pengeluaran')) NOT NULL)";
        db.execSQL(createCategoryTableQuery);

        // Membuat tabel Transaksi
        String createTransactionTableQuery = "CREATE TABLE " + TABLE_TRANSACTION + " (" +
                COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TRANSACTION_USER_ID + " INTEGER, " +
                COLUMN_TRANSACTION_NAME + " TEXT NOT NULL, " +
                COLUMN_TRANSACTION_DATE + " DATE NOT NULL, " +
                COLUMN_TRANSACTION_CATEGORY_ID + " INTEGER, " +
                COLUMN_TRANSACTION_AMOUNT + " INTEGER NOT NULL, " +
                COLUMN_TRANSACTION_TYPE + " TEXT CHECK (" + COLUMN_TRANSACTION_TYPE + " IN ('pemasukan', 'pengeluaran')) NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_TRANSACTION_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_TRANSACTION_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COLUMN_CATEGORY_ID + "))";
        db.execSQL(createTransactionTableQuery);

        // Menambahkan data default ke tabel Kategori
        addDefaultKategori(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tabel-tabel jika sudah ada versi sebelumnya
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // Method untuk tabel user
    public long addUser(String nama, int umur) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, nama);
        values.put(COLUMN_USER_AGE, umur);
        long userId = db.insert(TABLE_USER, null, values);
        db.close();
        return userId;
    }

    public Cursor getUserData() {
        String query = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor getFirstUserData() {
        String query = "SELECT * FROM " + TABLE_USER + " LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // Eksekusi kueri dan kembalikan kursor
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // Membuat metode publik untuk mendapatkan nama kolom user
    public static String getColumnNameUser() {
        return COLUMN_USER_NAME;
    }

    // Membuat metode publik untuk mendapatkan nama kolom ID user
    public static String getColumnIdUser() {
        return COLUMN_USER_ID;
    }

    // Method untuk tabel kategori
    public void addKategori(String nama, String jenis){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY_NAME, nama);
        cv.put(COLUMN_CATEGORY_TYPE, jenis);
        long result = db.insert(TABLE_CATEGORY, null, cv);
        if(result == -1){
            Toast.makeText(context, "Kategori Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Kategori Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    Cursor readPemasukanKategori(){
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_TYPE + " = 'pemasukan'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor readPengeluaranKategori(){
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_TYPE + " = 'pengeluaran'";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    void updateKategori(int row_id, String nama, String jenis){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_CATEGORY_NAME, nama);
        cv.put(COLUMN_CATEGORY_TYPE, jenis);
        long result = db.update(TABLE_CATEGORY, cv, COLUMN_CATEGORY_ID + "=?", new String[]{String.valueOf(row_id)});
        if(result == -1){
            Toast.makeText(context, "Gagal Memperbarui", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Berhasil Memperbarui", Toast.LENGTH_SHORT).show();
            Log.d("UpdateData", "Kategori dengan ID " + row_id + " diperbarui dengan nama " + nama);
        }
    }

    String getKategoriNameFromId(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_CATEGORY_NAME + " FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_ID + " = ?", new String[]{String.valueOf(id)});
        String categoryName = "";
        if (cursor != null && cursor.moveToFirst()) {
            categoryName = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME));
            Log.d("KategoriName", "Nama kategori dengan ID " + id + " adalah " + categoryName);
        } else {
            Log.d("KategoriName", "Tidak ada kategori dengan ID " + id);
        }
        if(cursor != null) {
            cursor.close();
        }
        return categoryName;
    }

    Cursor getFirstKategoriPemasukan(){
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_TYPE + " = 'pemasukan' LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor getFirstKategoriPengeluaran(){
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + COLUMN_CATEGORY_TYPE + " = 'pengeluaran' LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    // Metode untuk menambahkan data default ke tabel Kategori
    private void addDefaultKategori(SQLiteDatabase db) {
        ContentValues values = new ContentValues();

        // Menambahkan kategori default untuk pemasukan
        values.put(COLUMN_CATEGORY_NAME, "Gaji");
        values.put(COLUMN_CATEGORY_TYPE, "pemasukan");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Penjualan");
        values.put(COLUMN_CATEGORY_TYPE, "pemasukan");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Investasi");
        values.put(COLUMN_CATEGORY_TYPE, "pemasukan");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Pemasukan Lainnya");
        values.put(COLUMN_CATEGORY_TYPE, "pemasukan");
        db.insert(TABLE_CATEGORY, null, values);

        // Menambahkan kategori default untuk pengeluaran
        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Makanan dan Minuman");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Belanja");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Transportasi");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Hiburan");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Kesehatan");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Teknologi");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);

        values.clear();
        values.put(COLUMN_CATEGORY_NAME, "Pengeluaran Lainnya");
        values.put(COLUMN_CATEGORY_TYPE, "pengeluaran");
        db.insert(TABLE_CATEGORY, null, values);
    }

    // Method untuk tabel transaksi
    public void addTransaksi(int idUser, String nama, String tanggal, int idKategori, int nominal, String jenis){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_USER_ID, idUser);
        cv.put(COLUMN_TRANSACTION_NAME, nama);
        cv.put(COLUMN_TRANSACTION_DATE, tanggal);
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, idKategori);
        cv.put(COLUMN_TRANSACTION_AMOUNT, nominal);
        cv.put(COLUMN_TRANSACTION_TYPE, jenis);
        long result = db.insert(TABLE_TRANSACTION, null, cv);
        if(result == -1){
            Toast.makeText(context, "Transaksi Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Transaksi Berhasil Ditambahkan", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    Cursor readAllTransaksi(){
        String query = "SELECT *, " +
                "strftime('%Y-%m-%d', " + COLUMN_TRANSACTION_DATE + ") AS date_formatted " +
                "FROM " + TABLE_TRANSACTION +
                " ORDER BY date_formatted DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    Cursor readTransaksiBetweenDate(String tanggal1, String tanggal2){
        String query = "SELECT *, " +
                "strftime('%Y-%m-%d', " + COLUMN_TRANSACTION_DATE + ") AS date_formatted " +
                "FROM " + TABLE_TRANSACTION +
                " WHERE " + COLUMN_TRANSACTION_DATE + " BETWEEN ? AND ? " +
                "ORDER BY date_formatted DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{tanggal1, tanggal2});
        }
        return cursor;
    }

    Cursor searchTransaksiByName(String name){
        String query = "SELECT *, " +
                "strftime('%Y-%m-%d', " + COLUMN_TRANSACTION_DATE + ") AS date_formatted " +
                "FROM " + TABLE_TRANSACTION +
                " WHERE " + COLUMN_TRANSACTION_NAME +
                " LIKE ? " +
                "ORDER BY date_formatted DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{"%" + name + "%"});
        }
        return cursor;
    }

    Cursor searchTransaksibyMonth(String tanggal){
        // Ambil tahun dan bulan dari tanggal yang diberikan
        String yearMonth = tanggal.substring(0, 7);

        // Query untuk mencari transaksi berdasarkan tahun dan bulan
        String query = "SELECT *, " +
                "strftime('%Y-%m-%d', " + COLUMN_TRANSACTION_DATE + ") AS date_formatted " +
                "FROM " + TABLE_TRANSACTION +
                " WHERE strftime('%Y-%m', " + COLUMN_TRANSACTION_DATE + ") = ?" +
                "ORDER BY date_formatted DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{yearMonth});
        }
        return cursor;
    }

    Cursor searchTransaksibyDate(String tanggal){
        String query = "SELECT *, " +
                "strftime('%Y-%m-%d', " + COLUMN_TRANSACTION_DATE + ") AS date_formatted " +
                "FROM " + TABLE_TRANSACTION +
                " WHERE " + COLUMN_TRANSACTION_DATE +
                " = ? " +
                "ORDER BY date_formatted DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, new String[]{tanggal});
        }
        return cursor;
    }


    public void updateTransaksi(int idTransaksi,int idUser, String nama, String tanggal, int idKategori, int nominal, String jenis) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TRANSACTION_USER_ID, idUser);
        cv.put(COLUMN_TRANSACTION_NAME, nama);
        cv.put(COLUMN_TRANSACTION_DATE, tanggal);
        cv.put(COLUMN_TRANSACTION_CATEGORY_ID, idKategori);
        cv.put(COLUMN_TRANSACTION_AMOUNT, nominal);
        cv.put(COLUMN_TRANSACTION_TYPE, jenis);

        long result = db.update(TABLE_TRANSACTION, cv, COLUMN_TRANSACTION_ID + "=?", new String[]{String.valueOf(idTransaksi)});
        if(result == -1){
            Toast.makeText(context, "Gagal Memperbarui", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Berhasil Memperbarui", Toast.LENGTH_SHORT).show();
            Log.d("UpdateData", "Kategori dengan ID " + idTransaksi + " diperbarui dengan nama " + nama);
        }
    }

    public void deleteTransaksi(int id){
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(TABLE_TRANSACTION, COLUMN_TRANSACTION_ID + "=?", new String[]{String.valueOf(id)});
        if (result == -1){
            Toast.makeText(context, "Gagal Menghapus Data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Berhasil Menghapus Data", Toast.LENGTH_SHORT).show();
        }
    }
}
