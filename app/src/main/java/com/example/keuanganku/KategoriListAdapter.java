package com.example.keuanganku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class KategoriListAdapter extends RecyclerView.Adapter<KategoriListAdapter.KategoriViewHolder> {
    private Context context;
    Activity activity;
    private ArrayList<String> kategori_jenis;
    private ArrayList<String> kategori_nama;
    private ArrayList<Integer> kategori_id;

    private boolean isInput;

    public KategoriListAdapter(Activity activity, Context context, ArrayList<Integer> kategori_id,  ArrayList<String> kategori_nama, ArrayList<String> kategori_jenis, boolean isInput) {
        this.activity = activity;
        this.context = context;
        this.kategori_id = kategori_id;
        this.kategori_nama = kategori_nama;
        this.kategori_jenis = kategori_jenis;
        this.isInput = isInput;
    }

    @NonNull
    @Override
    public KategoriListAdapter.KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rowlayout_kategori, parent, false);
        return new KategoriViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KategoriListAdapter.KategoriViewHolder holder, int position) {
        holder.kategori_nama_txt.setText(String.valueOf(kategori_nama.get(position)));
        holder.editKategoriButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, UpdateKategori.class);
                    intent.putExtra("kategori_id", Integer.valueOf(kategori_id.get(currentPosition)));
                    intent.putExtra("kategori_nama", String.valueOf(kategori_nama.get(currentPosition)));
                    intent.putExtra("kategori_jenis", String.valueOf(kategori_jenis.get(currentPosition)));
                    activity.startActivityForResult(intent, 1);
                }
            }
        });
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    if (isInput){
                        Intent intent = new Intent(context, MasukanCatatan.class);
                        intent.putExtra("kategori_nama", String.valueOf(kategori_nama.get(currentPosition)));
                        intent.putExtra("kategori_id", Integer.valueOf(kategori_id.get(currentPosition)));
                        intent.putExtra("jenis", String.valueOf(kategori_jenis.get(currentPosition)));
                        activity.startActivityForResult(intent, 1);
                    } else {
                        Intent intent = new Intent(context, UpdateCatatan.class);
                        intent.putExtra("kategori_nama", String.valueOf(kategori_nama.get(currentPosition)));
                        intent.putExtra("kategori_id", Integer.valueOf(kategori_id.get(currentPosition)));
                        intent.putExtra("jenis", String.valueOf(kategori_jenis.get(currentPosition)));
                        activity.startActivityForResult(intent, 1);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategori_id.size();
    }

    public class KategoriViewHolder extends RecyclerView.ViewHolder{
        TextView kategori_nama_txt;
        LinearLayout mainLayout;
        ImageButton editKategoriButton;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            kategori_nama_txt = itemView.findViewById(R.id.NamaKategori);
            editKategoriButton = itemView.findViewById(R.id.editKategoriButton);
            mainLayout = itemView.findViewById(R.id.rowKategori);
        }
    }
}
