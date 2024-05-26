package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MasukanKategori extends AppCompatActivity {

    EditText inputKategori;
    Button inputButton;
    String jenis, keterangan, nominal, tanggal;
    int pengguna_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masukan_kategori);
        inputKategori = findViewById(R.id.inputNamaKategori);
        inputButton = findViewById(R.id.tombolInputKategori);

        getAndSetIntentData();

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(MasukanKategori.this);
                myDB.addKategori(inputKategori.getText().toString().trim(), jenis);
                backToKategoriList(v);
            }
        });
    }

    void getAndSetIntentData(){
        if(getIntent().hasExtra("jenis")){
            jenis = getIntent().getStringExtra("jenis");
            pengguna_id = getIntent().getIntExtra("pengguna_id", 0);
            nominal = getIntent().getStringExtra("nominal");
            keterangan = getIntent().getStringExtra("keterangan");
            tanggal = getIntent().getStringExtra("tanggal");
        } else {
            Toast.makeText(this, "Tidak ada jenis kategori yang terkirim", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToKategoriList(View view) {
        Intent intent = new Intent(this, ListKategori.class);
        intent.putExtra("jenis", jenis);
        intent.putExtra("nominal", nominal);
        intent.putExtra("keterangan", keterangan);
        intent.putExtra("tanggal", tanggal);
        intent.putExtra("pengguna_id", pengguna_id);
        startActivity(intent);
    }
}