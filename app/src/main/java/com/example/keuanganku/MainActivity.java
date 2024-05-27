package com.example.keuanganku;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import android.widget.PopupMenu;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

public class MainActivity extends AppCompatActivity {

    TextView headerNamaUser, totalPemasukanView, totalPengeluaranView, totalBersihView, emptyTextView;
    ImageView tombolRahasia, rangeDateButton, emptyImageView;
    Button rangeBulanIni, rangeBulanLalu, rangeSemua, rangeHariIni;
    ImageButton tombolMenu;

    SearchView searchView;

    String pengguna_nama;

    private List<String> transaksi_nama, transaksi_tanggal, transaksi_kategori, transaksi_tipe;
    private List<Integer> transaksi_id, user_id, kategori_id, transaksi_nominal;
    MyDatabaseHelper myDB;
    CatatanListAdapter adapter;
    int pengguna_id;

    RecyclerView listCatatan;

    int totalPemasukan, totalPengeluaran, totalBersih;
    DecimalFormat formatter = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getUserData();
        headerNamaUser = findViewById(R.id.headerNamaUser);
        totalPemasukanView = findViewById(R.id.TotalPemasukan);
        totalPengeluaranView = findViewById(R.id.TotalPengeluaran);
        totalBersihView = findViewById(R.id.TotalBersih);
        tombolRahasia = findViewById(R.id.tombolRahasia);
        tombolMenu = findViewById(R.id.ImageMenu);
        rangeDateButton = findViewById(R.id.rangeDateButton);
        rangeBulanIni = findViewById(R.id.rangeBulanIni);
        rangeBulanLalu = findViewById(R.id.rangeBulanLalu);
        rangeSemua = findViewById(R.id.rangeSemua);
        rangeHariIni = findViewById(R.id.rangeHariIni);
        searchView = findViewById(R.id.searchView);
        headerNamaUser.setText(pengguna_nama);
        emptyImageView= findViewById(R.id.empty_imageview);
        emptyTextView = findViewById(R.id.empty_textview);

        myDB = new MyDatabaseHelper(this);
        listCatatan = findViewById(R.id.list_catatan);
        transaksi_nama = new ArrayList<>();
        transaksi_id = new ArrayList<>();
        transaksi_nominal = new ArrayList<>();
        transaksi_kategori = new ArrayList<>();
        transaksi_tanggal = new ArrayList<>();
        transaksi_tipe = new ArrayList<>();
        user_id = new ArrayList<>();
        kategori_id = new ArrayList<>();

        // Atur adapter ke RecyclerView
        adapter = new CatatanListAdapter(MainActivity.this, this, transaksi_nama, transaksi_kategori,
                                            transaksi_tanggal, transaksi_tipe, transaksi_nominal, transaksi_id,
                                            user_id, kategori_id);
        listCatatan.setAdapter(adapter);
        listCatatan.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        Cursor cursor = myDB.readAllTransaksi();
        storeDataInArray(cursor);

        rangeDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Pair<Long, Long>> materialDatePicker = com.google.android.material.datepicker.MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(new Pair<>(
                                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                                MaterialDatePicker.todayInUtcMilliseconds()
                        )).build();

                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
                    @Override
                    public void onPositiveButtonClick(Pair<Long, Long> selection) {
                        if (selection != null) {
                            String tanggal1 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.first));
                            String tanggal2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(selection.second));
                            Cursor cursor;
                            cursor = myDB.readTransaksiBetweenDate(tanggal1, tanggal2);
                            storeDataInArray(cursor);
                        }
                    }
                });

                materialDatePicker.show(getSupportFragmentManager(), "tag");
            }
        });

        rangeBulanIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                String currentMonthString = String.format(Locale.getDefault(), "%02d", currentMonth); // Format ke "MM"
                String currentYearMonth = calendar.get(Calendar.YEAR) + "-" + currentMonthString;

                // Sekarang Anda bisa memanggil method untuk membaca transaksi berdasarkan bulan
                Cursor cursor = myDB.searchTransaksibyMonth(currentYearMonth);
                storeDataInArray(cursor);
            }
        });

        rangeBulanLalu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -1); // Mendapatkan bulan lalu
                int previousMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                String previousMonthString = String.format(Locale.getDefault(), "%02d", previousMonth); // Format ke "MM"
                String previousYearMonth = calendar.get(Calendar.YEAR) + "-" + previousMonthString;

                // Sekarang Anda bisa memanggil method untuk membaca transaksi berdasarkan bulan
                Cursor cursor = myDB.searchTransaksibyMonth(previousYearMonth);
                storeDataInArray(cursor);
            }
        });

        rangeSemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = myDB.readAllTransaksi();
                storeDataInArray(cursor);
            }
        });

        rangeHariIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String todayDate = dateFormat.format(new Date());

                Cursor cursor = myDB.getTransaksibyDate(todayDate);
                storeDataInArray(cursor);
            }
        });


        tombolMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initializing the popup menu and giving the reference as current context
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, tombolMenu);

                // Inflating popup menu from popup_menu.xml file
                popupMenu.getMenuInflater().inflate(R.menu.drawer_menu, popupMenu.getMenu());

                // Showing the popup menu
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle item selection.
//                        if (item.getItemId() == R.id.navigation_kalkulator) {
//                            Intent kalkulatorIntent = new Intent(MainActivity.this, Kalkulator.class);
//                            startActivity(kalkulatorIntent);
//                            return true;
//                        } else
                          if(item.getItemId() == R.id.navigtation_grafik){
                            Intent grafikIntent = new Intent(MainActivity.this, Grafik.class);
                            startActivity(grafikIntent);
                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Cursor cursor;
                cursor = myDB.getTransaksiByName(newText);
                storeDataInArray(cursor);
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            // Memuat ulang data dari database
            Cursor cursor = myDB.readAllTransaksi();
            storeDataInArray(cursor);
            // Memberitahu adapter bahwa data telah berubah
            adapter.notifyDataSetChanged();
        }
    }

    void storeDataInArray(Cursor cursor){
        transaksi_nama.clear();
        transaksi_id.clear();
        transaksi_nominal.clear();
        transaksi_kategori.clear();
        transaksi_tanggal.clear();
        transaksi_tipe.clear();
        user_id.clear();
        kategori_id.clear();

        if(cursor.getCount() == 0) {
            emptyImageView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()){
                transaksi_id.add(cursor.getInt(0));
                user_id.add(cursor.getInt(1));
                transaksi_nama.add(cursor.getString(2));
                transaksi_tanggal.add(cursor.getString(3));
                kategori_id.add(cursor.getInt(4));
                transaksi_nominal.add(cursor.getInt(5));
                transaksi_tipe.add(cursor.getString(6));
            }

            emptyImageView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.GONE);
        }
        cursor.close();
        adapter.notifyDataSetChanged();
        isiTotal();
    }

    public void getUserData() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(MainActivity.this);
        Cursor cursor = dbHelper.getFirstUserData();
        if (cursor != null) {
            // Memeriksa apakah indeks kolom valid
            int namaIndex = cursor.getColumnIndex(MyDatabaseHelper.getColumnNameUser());
            int idIndex = cursor.getColumnIndex(MyDatabaseHelper.getColumnIdUser());

            if (namaIndex != -1 && idIndex != -1 && cursor.moveToFirst()) {
                // Indeks kolom valid, dapatkan data
                pengguna_nama = cursor.getString(namaIndex);
                pengguna_id = cursor.getInt(idIndex);
                cursor.close();
            } else {
                Intent intent = new Intent(this, FirstTime.class);
                startActivity(intent);
            }
        }
    }

    public void isiTotal(){
        totalPemasukan = 0;
        totalPengeluaran = 0;
        totalBersih = 0;
        for(int i = 0; i < transaksi_nominal.size(); i++){
            if ("pemasukan".equals(transaksi_tipe.get(i))){
                totalPemasukan += transaksi_nominal.get(i);
            } else {
                totalPengeluaran += transaksi_nominal.get(i);
            }
        }

        if (totalPemasukan >= totalPengeluaran){
            totalBersih = totalPemasukan - totalPengeluaran;
            totalBersihView.setTextColor(ContextCompat.getColor(this, R.color.Pemasukan));
            totalBersihView.setText("Rp " + formatter.format(totalBersih));
        } else if(totalPemasukan < totalPengeluaran){
            totalBersih = totalPengeluaran - totalPemasukan;
            totalBersihView.setTextColor(ContextCompat.getColor(this, R.color.Pengeluaran));
            totalBersihView.setText("- Rp " + formatter.format(totalBersih));
        }
        totalPemasukanView.setText("Rp " + formatter.format(totalPemasukan));
        totalPengeluaranView.setText("Rp " + formatter.format(totalPengeluaran));
    }

    public void toAddCatatan(View view) {
        Intent intent = new Intent(this, MasukanCatatan.class);
        intent.putExtra("pengguna_id", pengguna_id);
        startActivity(intent);
    }
}