package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Grafik extends AppCompatActivity {

    Spinner spinnerWaktu, spinnerJenis;
    PieChart pieChart;
    RecyclerView listGrafik;
    GrafikListAdapter adapter;
    MyDatabaseHelper db;
    List<Integer> colors;

    String input = "11";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafik);
        db = new MyDatabaseHelper(this);
        spinnerWaktu = findViewById(R.id.spinnerWaktu);
        spinnerJenis = findViewById(R.id.spinnerJenis);
        pieChart = findViewById(R.id.grafikChart);
        listGrafik = findViewById(R.id.grafikList);

        // Definisi array status
        String[] optionWaktu = {"Semua", "Bulan Ini", "Bulan Lalu", "Hari Ini", "Custom"};
        ArrayAdapter<String> adapterWaktu = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, optionWaktu);
        spinnerWaktu.setAdapter(adapterWaktu);

        String[] optionJenis = {"Ringkasan", "Pengeluaran", "Pemasukan"};
        ArrayAdapter<String> adapterJenis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, optionJenis);
        spinnerJenis.setAdapter(adapterJenis);

        listGrafik.setLayoutManager(new LinearLayoutManager(Grafik.this));

        // Listener for spinnerJenis
        spinnerJenis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedJenis = (String) parent.getItemAtPosition(position);
                handleSpinnerSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Listener for spinnerWaktu
        spinnerWaktu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                handleSpinnerSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void handleSpinnerSelection() {
        Calendar calendar = Calendar.getInstance();
        Cursor cursor;
        String selectedJenis = (String) spinnerJenis.getSelectedItem();
        String selectedWaktu = (String) spinnerWaktu.getSelectedItem();

        if (selectedJenis.equals("Ringkasan")) {
            switch (selectedWaktu) {
                case "Semua":
                    cursor = db.getTotalAmountByType();
                    pieChartByJenis(cursor);
                    break;
                case "Bulan Ini":
                    int currentMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                    String currentMonthString = String.format(Locale.getDefault(), "%02d", currentMonth); // Format ke "MM"
                    String currentYearMonth = calendar.get(Calendar.YEAR) + "-" + currentMonthString;

                    cursor = db.getTotalAmountByTypeByMonth(currentYearMonth);
                    pieChartByJenis(cursor);
                    break;
                case "Bulan Lalu":
                    calendar.add(Calendar.MONTH, -1); // Mendapatkan bulan lalu
                    int previousMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                    String previousMonthString = String.format(Locale.getDefault(), "%02d", previousMonth); // Format ke "MM"
                    String previousYearMonth = calendar.get(Calendar.YEAR) + "-" + previousMonthString;

                    cursor = db.getTotalAmountByTypeByMonth(previousYearMonth);
                    pieChartByJenis(cursor);
                    break;
                case "Hari Ini":
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String todayDate = dateFormat.format(new Date());

                    cursor = db.getTotalAmountByTypeByDate(todayDate);
                    pieChartByJenis(cursor);
                    break;
                case "Custom":
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
                                cursor = db.getTotalAmountByTypeBetweenDate(tanggal1, tanggal2);
                                pieChartByJenis(cursor);
                            }
                        }
                    });
                    materialDatePicker.show(getSupportFragmentManager(), "tag");
                    break;
            }
        } else {
            String jenis = selectedJenis.equals("Pengeluaran") ? "pengeluaran" : "pemasukan";
            switch (selectedWaktu) {
                case "Semua":
                    cursor = db.getTotalAmountByCategory(jenis);
                    pieChartByKategori(cursor);
                    break;
                case "Bulan Ini":
                    int currentMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                    String currentMonthString = String.format(Locale.getDefault(), "%02d", currentMonth); // Format ke "MM"
                    String currentYearMonth = calendar.get(Calendar.YEAR) + "-" + currentMonthString;

                    cursor = db.getTotalAmountByCategoryByMonth(jenis, currentYearMonth);
                    pieChartByKategori(cursor);
                    break;
                case "Bulan Lalu":
                    calendar.add(Calendar.MONTH, -1); // Mendapatkan bulan lalu
                    int previousMonth = calendar.get(Calendar.MONTH) + 1; // Bulan dimulai dari 0, sehingga ditambah 1
                    String previousMonthString = String.format(Locale.getDefault(), "%02d", previousMonth); // Format ke "MM"
                    String previousYearMonth = calendar.get(Calendar.YEAR) + "-" + previousMonthString;

                    cursor = db.getTotalAmountByCategoryByMonth(jenis, previousYearMonth);
                    pieChartByKategori(cursor);
                    break;
                case "Hari Ini":
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String todayDate = dateFormat.format(new Date());

                    cursor = db.getTotalAmountByCategoryByDate(jenis, todayDate);
                    pieChartByKategori(cursor);
                    break;
                case "Custom":
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
                                cursor = db.getTotalAmountByCategoryBetweenDate(jenis, tanggal1, tanggal2);
                                pieChartByKategori(cursor);
                            }
                        }
                    });
                    materialDatePicker.show(getSupportFragmentManager(), "tag");
                    break;
            }
        }
    }

    public void pieChartByJenis(Cursor cursor){
        if (cursor != null) {
            Map<String, Integer> typeData = new HashMap<>();
            int totalAmount = 0;
            while (cursor.moveToNext()) {
                String type = cursor.getString(cursor.getColumnIndexOrThrow("jenis"));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("total_amount"));
                typeData.put(type, amount);
                totalAmount += amount;
            }
            cursor.close();

            // Urutkan data berdasarkan total_amount dari terbesar ke terkecil
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(typeData.entrySet());
            Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                    return entry2.getValue().compareTo(entry1.getValue());
                }
            });

            // Konversi kembali ke Map setelah pengurutan
            Map<String, Integer> sortedTypeData = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                sortedTypeData.put(entry.getKey(), entry.getValue());
            }

            createPieChart(sortedTypeData, "Jenis Transaksi");

            adapter = new GrafikListAdapter(this, this, sortedTypeData, colors);
            listGrafik.setAdapter(adapter);
        }
    }


    public void pieChartByKategori(Cursor cursor){
        if (cursor != null) {
            Map<String, Integer> categoryData = new HashMap<>();
            int totalAmount = 0;
            while (cursor.moveToNext()) {
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow("idKategori"));
                int amount = cursor.getInt(cursor.getColumnIndexOrThrow("total_amount"));

                // Dapatkan nama kategori dari ID kategori
                String categoryName = db.getKategoriNameFromId(categoryId);

                categoryData.put(categoryName, amount);
                totalAmount += amount;
            }
            cursor.close();

            // Urutkan data berdasarkan total_amount dari terbesar ke terkecil
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(categoryData.entrySet());
            Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Integer>>() {
                @Override
                public int compare(Map.Entry<String, Integer> entry1, Map.Entry<String, Integer> entry2) {
                    return entry2.getValue().compareTo(entry1.getValue());
                }
            });

            // Konversi kembali ke Map setelah pengurutan
            Map<String, Integer> sortedCategoryData = new LinkedHashMap<>();
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                sortedCategoryData.put(entry.getKey(), entry.getValue());
            }

            createPieChart(sortedCategoryData, "Kategori");

            adapter = new GrafikListAdapter(this, this, sortedCategoryData, colors);
            listGrafik.setAdapter(adapter);
        }
    }

    private void createPieChart(Map<String, Integer> data, String chartLabel) {
        List<PieEntry> pieEntries = new ArrayList<>();
        colors = new ArrayList<>();  // Initialize colors list
        int total = 0;
        for (int amount : data.values()) {
            total += amount;
        }

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            float percentage = (float) entry.getValue() / total * 100;
            pieEntries.add(new PieEntry(percentage, entry.getKey()));
        }

        PieDataSet pieDataSet = new PieDataSet(pieEntries, chartLabel);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        colors.addAll(pieDataSet.getColors());

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieData.setValueTextSize(14f);
        pieData.setValueTextColor(Color.BLACK);

        pieChart.setData(pieData);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(12f);

        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.invalidate();
    }

    public void backToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
