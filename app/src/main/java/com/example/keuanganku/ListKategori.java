package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListKategori extends AppCompatActivity {

    String jenis, nominal, keterangan, tanggal;
    int pengguna_id;

    MyDatabaseHelper myDB;
    ArrayList<String> kategori_nama, kategori_jenis;
    ArrayList<Integer> kategori_id;

    KategoriListAdapter adapter;
    RecyclerView listKategori;

    boolean isInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kategori);

        myDB = new MyDatabaseHelper(this);
        listKategori = findViewById(R.id.listKategori);
        kategori_id = new ArrayList<>();
        kategori_nama = new ArrayList<>();
        kategori_jenis = new ArrayList<>();

        // Mengambil jenis kategori dari masukancatatan
        getAndSetIntentData();
        storeDataInArray();

        // Create the adapter
        adapter = new KategoriListAdapter(ListKategori.this, this, kategori_id, kategori_nama, kategori_jenis, isInput);
        listKategori.setAdapter(adapter);
        listKategori.setLayoutManager(new LinearLayoutManager(ListKategori.this));
    }

    void storeDataInArray() {
        Cursor cursor;
        if (jenis.equals("pemasukan")) {
            cursor = myDB.readPemasukanKategori();
        } else{
            cursor = myDB.readPengeluaranKategori();
        }

        kategori_id.clear();
        kategori_nama.clear();
        kategori_jenis.clear();

        while (cursor.moveToNext()) {
            kategori_id.add(cursor.getInt(0));
            kategori_nama.add(cursor.getString(1));
            kategori_jenis.add(cursor.getString(2));
        }
        cursor.close();  // Jangan lupa menutup cursor setelah tidak digunakan lagi
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("jenis")){
            jenis = getIntent().getStringExtra("jenis");
            nominal = getIntent().getStringExtra("nominal");
            keterangan = getIntent().getStringExtra("keterangan");
            tanggal = getIntent().getStringExtra("tanggal");
            pengguna_id = getIntent().getIntExtra("pengguna_id", 0);
            isInput = getIntent().getBooleanExtra("isInput", false);
        } else {
            Toast.makeText(this, "Tidak ada jenis kategori yang terkirim", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToAddCatatan(View view) {
        Intent intent = new Intent(this, MasukanCatatan.class);
        intent.putExtra("jenis", jenis);
        intent.putExtra("pengguna_id", pengguna_id);
        startActivity(intent);
    }

    public void toAddKategori(View view) {
        Intent intent = new Intent(this, MasukanKategori.class);
        intent.putExtra("jenis", jenis);
        intent.putExtra("nominal", nominal);
        intent.putExtra("keterangan", keterangan);
        intent.putExtra("tanggal", tanggal);
        intent.putExtra("pengguna_id", pengguna_id);
        startActivity(intent);
    }
}