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
    String jenis;

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
        } else {
            Toast.makeText(this, "Tidak ada jenis kategori yang terkirim", Toast.LENGTH_SHORT).show();
        }
    }

    public void backToKategoriList(View view) {
        Intent intent = new Intent(this, ListKategori.class);
        intent.putExtra("jenis", jenis);
        startActivity(intent);
    }
}