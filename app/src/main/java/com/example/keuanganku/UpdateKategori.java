package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UpdateKategori extends AppCompatActivity {

    String kategori_nama, jenis;
    int kategori_id;

    EditText inputKategori;
    Button editButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_kategori);
        inputKategori = findViewById(R.id.inputNamaKategori2);
        editButton = findViewById(R.id.tombolInputKategori2);

        getAndSetIntentData();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // And only then we call this
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateKategori.this);
                myDB.updateKategori(kategori_id,
                        inputKategori.getText().toString().trim(),
                        jenis);
                backToKategoriList(v);
            }
        });
    }

    public void getAndSetIntentData() {
        if (getIntent().hasExtra("kategori_nama") &&
                getIntent().hasExtra("kategori_id") &&
                getIntent().hasExtra("kategori_jenis")) {
            jenis = getIntent().getStringExtra("kategori_jenis");
            kategori_nama = getIntent().getStringExtra("kategori_nama");
            kategori_id = getIntent().getIntExtra("kategori_id", 0);
            inputKategori.setText(kategori_nama);
        } else {
            Toast.makeText(this, "Tidak ada Data yang terkirim", Toast.LENGTH_SHORT).show();
        }
    }



    public void backToKategoriList(View view) {
        Intent intent = new Intent(this, ListKategori.class);
        intent.putExtra("jenis", jenis);
        startActivity(intent);
    }
}