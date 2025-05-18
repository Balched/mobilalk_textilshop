package com.example.textil;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private CartAdapter cartAdapter;
    private ArrayList<Product> cartItems;

    private static final String CHANNEL_ID = "order_channel";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCart = findViewById(R.id.rvCart);
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        cartItems = getIntent().getParcelableArrayListExtra("cartItems");
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        cartAdapter = new CartAdapter(cartItems);
        rvCart.setAdapter(cartAdapter);

        Button btnBackToShop = findViewById(R.id.btnBackToShop);
        btnBackToShop.setOnClickListener(v -> finish());

        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        btnBackToShop.startAnimation(zoomIn);

        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        createNotificationChannel();

        btnPlaceOrder.startAnimation(zoomIn);

        btnPlaceOrder.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "A kosár már üres.", Toast.LENGTH_SHORT).show();
                return;
            }

            cartItems.clear();
            cartAdapter.notifyDataSetChanged();


            Toast.makeText(this, "Köszönjük a rendelésed!", Toast.LENGTH_SHORT).show();
            sendOrderNotification();
            setResult(RESULT_OK);
            finish();
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        }

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Rendelés";
            String description = "Rendelés leadás értesítése";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendOrderNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_shopping_cart)
                .setContentTitle("Rendelés sikeres")
                .setContentText("Köszönjük a vásárlást!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        notificationManager.notify(1001, builder.build());
    }

}
