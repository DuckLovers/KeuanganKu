package com.example.keuanganku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.text.DecimalFormat;



public class CatatanListAdapter extends RecyclerView.Adapter<CatatanListAdapter.CatatanViewHolder> {

    private Context context;
    Activity activity;
    private List<String> transaksi_nama, transaksi_tanggal, transaksi_kategori, transaksi_tipe;
    private List<Integer> transaksi_id, user_id, kategori_id, transaksi_nominal;

    public CatatanListAdapter(Activity activity, Context context,
                              List<String> transaksi_nama, List<String> transaksi_kategori,
                              List<String> transaksi_tanggal, List<String> transaksi_tipe,
                              List<Integer> transaksi_nominal, List<Integer> transaksi_id,
                              List<Integer> user_id, List<Integer> kategori_id) {

        this.activity = activity;
        this.context = context;
        this.transaksi_id = transaksi_id;
        this.transaksi_nama = transaksi_nama;
        this.transaksi_kategori = transaksi_kategori;
        this.transaksi_tanggal = transaksi_tanggal;
        this.transaksi_nominal = transaksi_nominal;
        this.transaksi_tipe = transaksi_tipe;
        this.kategori_id = kategori_id;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public CatatanListAdapter.CatatanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rowlayout_catatan, parent, false);
        return new CatatanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatatanViewHolder holder, int position) {
        MyDatabaseHelper db = new MyDatabaseHelper(context);
        String kategori_nama = db.getKategoriNameFromId(kategori_id.get(position));
        holder.transaksi_nama_txt.setText(String.valueOf(transaksi_nama.get(position)));
        holder.transaksi_tanggal_txt.setText(String.valueOf(transaksi_tanggal.get(position)));
        holder.transaksi_kategori_txt.setText(String.valueOf(kategori_nama));

        // Format angka nominal menjadi format mata uang (Rp) tanpa desimal dan angka nol di akhir
        int nominal = transaksi_nominal.get(position);
        DecimalFormat formatter = new DecimalFormat("###,###");
        String formattedNominal = "Rp " + formatter.format(nominal);
        holder.transaksi_nominal_txt.setText(formattedNominal);

        if ("pemasukan".equals(transaksi_tipe.get(position))) {
            holder.transaksi_nominal_txt.setTextColor(ContextCompat.getColor(context, R.color.Pemasukan));
        } else {
            holder.transaksi_nominal_txt.setTextColor(ContextCompat.getColor(context, R.color.Pengeluaran));
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, UpdateCatatan.class);
                    intent.putExtra("transaksi_id", transaksi_id.get(currentPosition));
                    intent.putExtra("kategori_id", kategori_id.get(currentPosition));
                    intent.putExtra("pengguna_id", String.valueOf(user_id.get(currentPosition)));
                    intent.putExtra("keterangan", String.valueOf(transaksi_nama.get(currentPosition)));
                    intent.putExtra("tanggal", String.valueOf(transaksi_tanggal.get(currentPosition)));
                    intent.putExtra("nominal", String.valueOf(transaksi_nominal.get(currentPosition)));
                    intent.putExtra("jenis", String.valueOf(transaksi_tipe.get(currentPosition)));
                    // Tambahkan pernyataan log untuk mencatat nilai-nilai ekstra yang diterima
                    Log.d("Intent", "kategori_id: " + kategori_id.get(currentPosition));
                    Log.d("Intent", "pengguna_id: " + String.valueOf(user_id.get(currentPosition)));
                    Log.d("Intent", "transaksi_id: " + transaksi_id);
                    Log.d("Intent", "jenis: " + transaksi_tipe);
                    Log.d("Intent", "nominal: " + nominal);
                    Log.d("Intent", "keterangan: " + transaksi_nama);
                    Log.d("Intent", "tanggal: " + transaksi_tanggal);
                    activity.startActivityForResult(intent, 1);
                }
            }
        });
    }




    @Override
    public int getItemCount() {
        return transaksi_id.size();
    }

    public class CatatanViewHolder extends RecyclerView.ViewHolder{
        TextView transaksi_nama_txt, transaksi_nominal_txt, transaksi_tanggal_txt, transaksi_kategori_txt;
        LinearLayout mainLayout;
        public CatatanViewHolder(@NonNull View itemView) {

            super(itemView);
            transaksi_nama_txt = itemView.findViewById(R.id.NamaTransaksi);
            transaksi_nominal_txt = itemView.findViewById(R.id.NominalTransaksi);
            transaksi_tanggal_txt = itemView.findViewById(R.id.TanggalTransaksi);
            transaksi_kategori_txt = itemView.findViewById(R.id.KategoriTransaksi);
            mainLayout = itemView.findViewById(R.id.rowCatatan);
        }
    }
}
