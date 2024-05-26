package com.example.keuanganku;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Kalkulator extends AppCompatActivity {

    Integer angka1, angka2;
    EditText input1, input2;
    TextView operator, teksHasil;
    double hasil = 0;
    Button tambah, kurang, kali, bagi, sama_dengan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalkulator);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Praktikum 6");
            getSupportActionBar().setSubtitle("Kalkulator");
        }

        tambah = findViewById(R.id.tambah);
        kali = findViewById(R.id.kali);
        kurang = findViewById(R.id.kurang);
        bagi = findViewById(R.id.bagi);
        sama_dengan = findViewById(R.id.sama_dengan);
        operator = findViewById(R.id.operator);
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        teksHasil = findViewById(R.id.textHasil);

        tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performOperation("+");
            }
        });

        kurang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performOperation("-");
            }
        });

        kali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performOperation("*");
            }
        });

        bagi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performOperation("/");
            }
        });

        sama_dengan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                teksHasil.setText(String.format("%.1f", hasil));
            }
        });
    }

    private void performOperation(String op) {
        if (input1.getText().toString().equals("") || input2.getText().toString().equals("")) {
            Toast.makeText(Kalkulator.this, "Masukkan Angka Dulu", Toast.LENGTH_SHORT).show();
            return;
        }

        angka1 = Integer.parseInt(input1.getText().toString());
        angka2 = Integer.parseInt(input2.getText().toString());

        switch (op) {
            case "+":
                hasil = angka1 + angka2;
                break;
            case "-":
                hasil = angka1 - angka2;
                break;
            case "*":
                hasil = angka1 * angka2;
                break;
            case "/":
                if (angka2 == 0) {
                    Toast.makeText(Kalkulator.this, "Tidak bisa membagi dengan nol", Toast.LENGTH_SHORT).show();
                    hasil = 0;
                    return;
                }
                hasil = angka1 / (double) angka2;
                break;
        }
        operator.setText(op);
        teksHasil.setText(String.format("%.1f", hasil));
    }
}
