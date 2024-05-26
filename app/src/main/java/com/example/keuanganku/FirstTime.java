package com.example.keuanganku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class FirstTime extends AppCompatActivity {

    EditText inputNama, inputUsia;
    Button inputUserButton;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        inputNama = findViewById(R.id.inputNamaUser);
        inputUsia = findViewById(R.id.inputUsiaUser);
        inputUserButton = findViewById(R.id.tombolInputUser);
        context = getApplicationContext(); // Inisialisasi context

        inputUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = inputNama.getText().toString().trim();
                String usiaString = inputUsia.getText().toString().trim();

                if (!TextUtils.isEmpty(nama) && !TextUtils.isEmpty(usiaString)) {
                    int usia = Integer.parseInt(usiaString);
                    MyDatabaseHelper myDB = new MyDatabaseHelper(FirstTime.this);
                    long userId = myDB.addUser(nama, usia);

                    if (userId != -1) {
                        // Data pengguna berhasil ditambahkan, lanjut ke MainActivity
                        Toast.makeText(context, "Data pengguna berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                        toHomePage();
                    } else {
                        Toast.makeText(context, "Gagal menambahkan pengguna", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FirstTime.this, "Silakan isi semua data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void toHomePage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Selesaikan activity saat berpindah ke MainActivity
    }
}
