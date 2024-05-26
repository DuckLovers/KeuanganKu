package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

public class MasukanCatatan extends AppCompatActivity {
    EditText inputTanggal, inputNominal, inputKategori, inputKeterangan;
    Button pemasukanButton, pengeluaranButton, inputButton;
    int kategori_id, pengguna_id;
    boolean isInput = true;
    String jenis = "pengeluaran", kategori_nama, nominal, keterangan, tanggal;

    MyDatabaseHelper db;

    SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masukan_catatan);
        db = new MyDatabaseHelper(MasukanCatatan.this);

        sp = getApplicationContext().getSharedPreferences("InpurCatatanPrefs", Context.MODE_PRIVATE);

        inputKeterangan = findViewById(R.id.inputKeterangan);
        inputTanggal = findViewById(R.id.editTextTanggal);
        inputKategori = findViewById(R.id.inputKategori);
        inputNominal = findViewById(R.id.inputNominal);

        inputTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        // Terapkan TextWatcher untuk format nominal
        CurrencyTextWatcher textWatcher = new CurrencyTextWatcher(inputNominal);
        inputNominal.addTextChangedListener(textWatcher);

        // Menemukan tombol pemasukan menggunakan ID-nya
        pemasukanButton = findViewById(R.id.pemasukanButton);

        // Menetapkan onClickListener ke tombol pemasukan
        pemasukanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil fungsi untuk mengubah warna saat tombol ditekan
                changeColorPemasukan();
                resetColorPengeluaran();
                Cursor cursor = db.getFirstKategoriPemasukan();

                if (cursor != null && cursor.moveToFirst()) {
                    kategori_id = cursor.getInt(0);
                    inputKategori.setText(cursor.getString(1));
                    jenis = cursor.getString(2);
                } else {
                    // Handle the case where the cursor is null or empty
                    Toast.makeText(MasukanCatatan.this, "Tidak ada data pemasukan tersedia", Toast.LENGTH_SHORT).show();
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        // Menemukan tombol pengeluaran menggunakan ID-nya
        pengeluaranButton = findViewById(R.id.pengeluaranButton);

        // Menetapkan onClickListener ke tombol pengeluaran
        pengeluaranButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Panggil fungsi untuk mengubah warna saat tombol ditekan
                changeColorPengeluaran();
                resetColorPemasukan();
                Cursor cursor = db.getFirstKategoriPengeluaran();

                if (cursor != null && cursor.moveToFirst()) {
                    kategori_id = cursor.getInt(0);
                    inputKategori.setText(cursor.getString(1));
                    jenis = cursor.getString(2);
                } else {
                    // Handle the case where the cursor is null or empty
                    Toast.makeText(MasukanCatatan.this, "Tidak ada data pengeluaran tersedia", Toast.LENGTH_SHORT).show();
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        getAndSetIntentData();

        inputButton = findViewById(R.id.inputButton);

        inputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nominalString = inputNominal.getText().toString().replaceAll("[^0-9]", ""); // Menghapus simbol mata uang dan mengambil hanya angka
                int nominal = Integer.parseInt(nominalString); // Mengonversi teks menjadi bilangan bulat
                db.addTransaksi(pengguna_id,
                        inputKeterangan.getText().toString().trim(),
                        inputTanggal.getText().toString().trim(),
                        kategori_id,
                        nominal,
                        jenis);
                backToMain(v);
            }
        });

        getSharedPreferences();
    }

    public void changeColorPemasukan() {
        pemasukanButton.setBackgroundColor(getResources().getColor(R.color.Pemasukan));
        jenis = "pemasukan";
    }

    private void changeColorPengeluaran() {
        // Ubah warna tombol menjadi warna yang diinginkan
        pengeluaranButton.setBackgroundColor(getResources().getColor(R.color.Pengeluaran)); // Ganti newColor dengan warna yang diinginkan
        jenis = "pengeluaran";
    }

    // Fungsi untuk mengembalikan warna tombol pemasukan ke warna semula
    private void resetColorPemasukan() {
        pemasukanButton.setBackgroundColor(getResources().getColor(R.color.ButtonColor)); // Ganti ButtonColor dengan warna semula tombol
    }

    // Fungsi untuk mengembalikan warna tombol pengeluaran ke warna semula
    private void resetColorPengeluaran() {
        pengeluaranButton.setBackgroundColor(getResources().getColor(R.color.ButtonColor)); // Ganti ButtonColor dengan warna semula tombol
    }

    // Kelas CurrencyTextWatcher untuk format angka
    public static class CurrencyTextWatcher implements TextWatcher {
        private final EditText editText;

        public CurrencyTextWatcher(EditText editText) {
            this.editText = editText;
        }

        private String current = "";

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                editText.removeTextChangedListener(this);

                String cleanString = s.toString().replaceAll("[^0-9]", "");
                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < cleanString.length(); i++) {
                    formatted.append(cleanString.charAt(i));
                    if ((cleanString.length() - i) % 3 == 1 && i != cleanString.length() - 1) {
                        formatted.append(".");
                    }
                }

                current = formatted.toString();
                editText.setText(current);
                editText.setSelection(current.length());

                editText.addTextChangedListener(this);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Tindakan yang akan diambil saat pengguna memilih tanggal
                        String formattedMonth = String.format("%02d", month + 1); // tambahkan +1 karena bulan dimulai dari 0
                        String formattedDayOfMonth = String.format("%02d", dayOfMonth);
                        String selectedDate = year + "-" + formattedMonth + "-" + formattedDayOfMonth; // Format sesuai dengan SQLite DATE
                        inputTanggal.setText(selectedDate);
                    }
                },
                year, month, dayOfMonth);
        datePickerDialog.show();
    }

    void getAndSetIntentData(){
            kategori_nama = getIntent().getStringExtra("kategori_nama");
            kategori_id = getIntent().getIntExtra("kategori_id", 0);
            pengguna_id = getIntent().getIntExtra("pengguna_id", 0);
            jenis = getIntent().getStringExtra("jenis");
            if ("pemasukan".equals(jenis)){
                changeColorPemasukan();
                resetColorPengeluaran();
            } else {
                changeColorPengeluaran();
                resetColorPemasukan();
            }
            inputKategori.setText(kategori_nama);
    }

    private void getSharedPreferences(){
        nominal = sp.getString("nominal", "");
        keterangan = sp.getString("keterangan", "");
        tanggal = sp.getString("tanggal", "");
        pengguna_id = sp.getInt("pengguna_id", 0);

        inputNominal.setText(nominal);
        inputKeterangan.setText(keterangan);
        inputTanggal.setText(tanggal);
    }

    private void resetSharedPreferences() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply(); // or editor.commit();
    }

    public void toListKategori(View view) {
        // Simpan data yang diinput ke dalam variabel sebelum memulai ListKategori
        nominal = inputNominal.getText().toString();
        keterangan = inputKeterangan.getText().toString();
        tanggal = inputTanggal.getText().toString();

        sp = getSharedPreferences("InpurCatatanPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("nominal", nominal);
        editor.putString("keterangan", keterangan);
        editor.putString("tanggal", tanggal);
        editor.putInt("pengguna_id", pengguna_id);
        editor.commit();

        Intent intent = new Intent(this, ListKategori.class);
        intent.putExtra("jenis", jenis);
        intent.putExtra("pengguna_id", pengguna_id);
        intent.putExtra("isInput", isInput);
        startActivity(intent);
    }

    public void backToMain(View view) {
        resetSharedPreferences();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}