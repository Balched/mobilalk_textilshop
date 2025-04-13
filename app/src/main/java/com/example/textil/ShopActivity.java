package com.example.textil;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Button buyButton1 = findViewById(R.id.btnBuy1);
        Button buyButton2 = findViewById(R.id.btnBuy2);
        Button buyButton3 = findViewById(R.id.btnBuy3);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeInAnimation = AnimationUtils.loadAnimation(ShopActivity.this, R.anim.fade_in);
                v.startAnimation(fadeInAnimation);
                Toast.makeText(ShopActivity.this, "Termék kosárba téve!", Toast.LENGTH_SHORT).show();
            }
        };

        buyButton1.setOnClickListener(listener);
        buyButton2.setOnClickListener(listener);
        buyButton3.setOnClickListener(listener);
    }
}
