package com.example.keuanganku;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class GrafikListAdapter extends RecyclerView.Adapter<GrafikListAdapter.GrafikViewHolder> {
    Context context;
    Activity activity;
    private List<String> grafik_kategori, grafik_persen;
    private List<Integer> grafik_total;
    private List<Integer> grafik_warna;

    public GrafikListAdapter(Context context, Activity activity, Map<String, Integer> data, List<Integer> colors) {
        this.context = context;
        this.activity = activity;
        this.grafik_kategori = new ArrayList<>(data.keySet());
        this.grafik_persen = new ArrayList<>();
        this.grafik_total = new ArrayList<>(data.values());
        this.grafik_warna = colors;

//        // Urutkan data berdasarkan totalnya dari terbesar ke terkecil
//        Collections.sort(this.grafik_kategori, new Comparator<String>() {
//            @Override
//            public int compare(String o1, String o2) {
//                return data.get(o2).compareTo(data.get(o1));
//            }
//        });

        // Persentase
        int totalAmount = 0;
        for (int amount : data.values()) {
            totalAmount += amount;
        }
        for (String category : this.grafik_kategori) {
            float percentage = (float) data.get(category) / totalAmount * 100;
            grafik_persen.add(String.format("%.2f%%", percentage));
        }
    }

    @NonNull
    @Override
    public GrafikListAdapter.GrafikViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rowlayout_grafik, parent, false);
        return new GrafikListAdapter.GrafikViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GrafikListAdapter.GrafikViewHolder holder, int position) {
        // Pastikan indeks valid sebelum mengakses elemen daftar
        if (position < grafik_kategori.size() && position < grafik_persen.size() && position < grafik_total.size() && position < grafik_warna.size()) {
            holder.grafik_kategori_txt.setText("(" + String.valueOf(grafik_persen.get(position)) + ") " + String.valueOf(grafik_kategori.get(position)));
            DecimalFormat formatter = new DecimalFormat("###,###");
            String formattedTotal = "Rp " + formatter.format(grafik_total.get(position));
            holder.grafik_total_txt.setText(formattedTotal);
            holder.lingkaranWarna.setColorFilter(grafik_warna.get(position));  // Set the color of the circle
        }
    }

    @Override
    public int getItemCount() {
        return grafik_kategori.size();
    }

    public class GrafikViewHolder extends RecyclerView.ViewHolder {
        TextView grafik_kategori_txt, grafik_total_txt;
        ImageView lingkaranWarna;
        LinearLayout mainLayout;

        public GrafikViewHolder(@NonNull View itemView) {
            super(itemView);
            grafik_kategori_txt = itemView.findViewById(R.id.grafikNama);
            grafik_total_txt = itemView.findViewById(R.id.grafikTotal);
            lingkaranWarna = itemView.findViewById(R.id.lingkaranWarna);
        }
    }
}
