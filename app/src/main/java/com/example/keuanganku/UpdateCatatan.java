package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class UpdateCatatan extends AppCompatActivity {

    EditText inputTanggal, inputNominal, inputKategori, inputKeterangan;
    Button pemasukanButton, pengeluaranButton, updateButton, deleteButton;

    int kategori_id, pengguna_id, transaksi_id;
    boolean isInput = false;

    String jenis = "pengeluaran", kategori_nama, nominal, keterangan, tanggal;
    SharedPreferences sp;
    MyDatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_catatan);

        db = new MyDatabaseHelper(this);
        sp = getApplicationContext().getSharedPreferences("UpdateCatatanPrefs", Context.MODE_PRIVATE);

        inputKeterangan = findViewById(R.id.inputKeterangan2);
        inputTanggal = findViewById(R.id.editTextTanggal2);
        inputKategori = findViewById(R.id.inputKategori2);
        inputNominal = findViewById(R.id.inputNominal2);
        inputTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        // Terapkan TextWatcher untuk format nominal
        MasukanCatatan.CurrencyTextWatcher textWatcher = new MasukanCatatan.CurrencyTextWatcher(inputNominal);
        inputNominal.addTextChangedListener(textWatcher);

        // Menemukan tombol pemasukan menggunakan ID-nya
        pemasukanButton = findViewById(R.id.pemasukanButton2);

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
                    Toast.makeText(UpdateCatatan.this, "Tidak ada data pemasukan tersedia", Toast.LENGTH_SHORT).show();
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        // Menemukan tombol pengeluaran menggunakan ID-nya
        pengeluaranButton = findViewById(R.id.pengeluaranButton2);

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
                    Toast.makeText(UpdateCatatan.this, "Tidak ada data pengeluaran tersedia", Toast.LENGTH_SHORT).show();
                }

                if (cursor != null) {
                    cursor.close();
                }
            }
        });

        getSharedPreferences();
        getAndSetIntentData();

        updateButton = findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateCatatan.this);
                String nominalString = inputNominal.getText().toString().replaceAll("[^0-9]", ""); // Menghapus simbol mata uang dan mengambil hanya angka
                int nominal = Integer.parseInt(nominalString); // Mengonversi teks menjadi bilangan bulat
                myDB.updateTransaksi(transaksi_id, pengguna_id,
                        inputKeterangan.getText().toString().trim(),
                        inputTanggal.getText().toString().trim(),
                        kategori_id,
                        nominal,
                        jenis);
                resetSharedPreferences();
                backToMain(v);
            }
        });

        deleteButton = findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hapus Catatan ?");
        builder.setMessage("Apakah kamu ingin menghapus Catatan ?");
        builder.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateCatatan.this);
                myDB.deleteTransaksi(transaksi_id);
                finish();
            }
        });
        builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
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
        if (getIntent().hasExtra("transaksi_id") &&
                getIntent().hasExtra("pengguna_id") &&
                getIntent().hasExtra("keterangan") &&
                getIntent().hasExtra("tanggal") &&
                getIntent().hasExtra("kategori_id") &&
                getIntent().hasExtra("nominal") &&
                getIntent().hasExtra("jenis")){
            kategori_id = getIntent().getIntExtra("kategori_id", 0);
            pengguna_id = getIntent().getIntExtra("pengguna_id", 0); // Mengubah ini menjadi getIntExtra()
            transaksi_id = getIntent().getIntExtra("transaksi_id", 0);
            jenis = getIntent().getStringExtra("jenis");
            nominal = getIntent().getStringExtra("nominal");
            keterangan = getIntent().getStringExtra("keterangan");
            tanggal = getIntent().getStringExtra("tanggal");
            if ("pemasukan".equals(jenis)){
                changeColorPemasukan();
                resetColorPengeluaran();
            } else {
                changeColorPengeluaran();
                resetColorPemasukan();
            }
            MyDatabaseHelper db = new MyDatabaseHelper(this);
            kategori_nama = db.getKategoriNameFromId(kategori_id);
            inputKategori.setText(kategori_nama);
            inputNominal.setText(nominal);
            inputKeterangan.setText(keterangan);
            inputTanggal.setText(tanggal);
        } else if (getIntent().hasExtra("jenis") &&
                   getIntent().hasExtra("kategori_nama")&&
                   getIntent().hasExtra("kategori_id")){
            kategori_id = getIntent().getIntExtra("kategori_id", 0);
            pengguna_id = getIntent().getIntExtra("pengguna_id", 0);
            jenis = getIntent().getStringExtra("jenis");
            kategori_nama = getIntent().getStringExtra("kategori_nama");
            inputKategori.setText(kategori_nama);
        } else{
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void getSharedPreferences(){
        nominal = sp.getString("nominal", "");
        keterangan = sp.getString("keterangan", "");
        tanggal = sp.getString("tanggal", "");
        transaksi_id = sp.getInt("transaksi_id", 0);
        pengguna_id = sp.getInt("pengguna_id",0);

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

        sp = getSharedPreferences("UpdateCatatanPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("transaksi_id", transaksi_id);
        editor.putInt("pengguna_id", pengguna_id);
        editor.putString("nominal", nominal);
        editor.putString("keterangan", keterangan);
        editor.putString("tanggal", tanggal);
        editor.commit();

        Intent intent = new Intent(this, ListKategori.class);
        intent.putExtra("jenis", jenis);
        intent.putExtra("nominal", nominal);
        intent.putExtra("keterangan", keterangan);
        intent.putExtra("tanggal", tanggal);
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