package com.example.keuanganku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_DELAY = 2000; // 2 detik

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Cek keberadaan pengguna dalam database
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(SplashScreen.this);
                Cursor cursor = dbHelper.getUserData();

                if (cursor != null && cursor.getCount() > 0) {
                    // Pengguna ditemukan, arahkan ke MainActivity
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    // Pengguna tidak ditemukan, arahkan ke LoginActivity
                    Intent intent = new Intent(SplashScreen.this, FirstTime.class);
                    startActivity(intent);
                }

                // Tutup cursor dan activity
                if (cursor != null) {
                    cursor.close();
                }
                finish();
            }
        }, SPLASH_DELAY);
    }
}
