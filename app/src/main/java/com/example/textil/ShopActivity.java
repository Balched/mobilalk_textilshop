package com.example.textil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ShopActivity extends AppCompatActivity implements ProductAdapter.OnAddToCartClickListener {

    private ProductService productService;
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    private List<Product> cart = new ArrayList<>();

    private Button btnGoToCart;
    private Button btnFilter;

    private TextView tvShopTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        tvShopTitle = findViewById(R.id.tvShopTitle);

        productService = new ProductService();

        rvProducts = findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(productList, this);
        rvProducts.setAdapter(productAdapter);

        btnGoToCart = findViewById(R.id.btnGoToCart);
        btnGoToCart.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CartActivity.class);
            intent.putParcelableArrayListExtra("cartItems", new ArrayList<>(cart));
            startActivityForResult(intent, 1);
        });

        Button goToCrudButton = findViewById(R.id.goToCrudButton);

        goToCrudButton.setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, ProductCrudActivity.class);
            startActivity(intent);
        });

        btnFilter = findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(v -> runComplexQueries());

        loadProductsFromFirestore();
    }

    private void loadProductsFromFirestore() {
        productService.getAllProducts(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                QuerySnapshot result = task.getResult();
                for (QueryDocumentSnapshot doc : result) {
                    Product product = doc.toObject(Product.class);
                    product.setId(doc.getId());
                    productList.add(product);
                }
                productAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Hiba a termékek betöltésekor", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAddToCartClick(Product product) {
        cart.add(product);
        Toast.makeText(this, product.getName() + " hozzáadva a kosárhoz", Toast.LENGTH_SHORT).show();

        View v = findViewById(R.id.btnAddToCart);

        Animation bounceAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        tvShopTitle.startAnimation(bounceAnim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            cart.clear();
            Toast.makeText(this, "A kosár ürítve lett.", Toast.LENGTH_SHORT).show();
        }
    }


    private void runComplexQueries() {
        productList.clear();

        productService.getProductsByPriceFiltered(500, 10, task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Product p = doc.toObject(Product.class);
                    p.setId(doc.getId());
                    productList.add(p);
                }
                productAdapter.notifyDataSetChanged();
                Toast.makeText(this, "1. lekérdezés eredménye betöltve", Toast.LENGTH_SHORT).show();
            }
        });

        productService.getProductsOrderedByNameAndPrice(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Product p = doc.toObject(Product.class);
                    p.setId(doc.getId());
                    productList.add(p);
                }
                productAdapter.notifyDataSetChanged();
                Toast.makeText(this, "2. lekérdezés eredménye betöltve", Toast.LENGTH_SHORT).show();
            }
        });

        AtomicReference<DocumentSnapshot> lastVisible = new AtomicReference<>();

        if (lastVisible.get() == null) {
            productService.getPaginatedProducts(null, 5, task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Product p = doc.toObject(Product.class);
                        p.setId(doc.getId());
                        productList.add(p);
                    }
                    if (!task.getResult().isEmpty())
                        lastVisible.set(task.getResult().getDocuments()
                                .get(task.getResult().size() - 1));
                    productAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "3. lekérdezés eredménye betöltve", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            productService.getPaginatedProducts(lastVisible.get(), 5, task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Product p = doc.toObject(Product.class);
                        p.setId(doc.getId());
                        productList.add(p);
                    }
                    if (!task.getResult().isEmpty())
                        lastVisible.set(task.getResult().getDocuments()
                                .get(task.getResult().size() - 1));
                    productAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "3. lekérdezés újabb oldal betöltve", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
