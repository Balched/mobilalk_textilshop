package com.example.textil;


import android.os.Bundle;
import android.util.Log;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductCrudActivity extends AppCompatActivity {
    private EditText nameEditText, priceEditText;
    private Button addButton, updateButton, deleteButton;
    private ListView productList;
    private ArrayAdapter<String> adapter;
    private List<Product> products = new ArrayList<>();
    private Product selectedProduct = null;

    private ProductService productService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_crud);

        nameEditText = findViewById(R.id.nameEditText);
        priceEditText = findViewById(R.id.priceEditText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        productList = findViewById(R.id.productListView);

        Button btnBackToShop = findViewById(R.id.btnBackToShop);
        btnBackToShop.setOnClickListener(v -> finish());

        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        btnBackToShop.startAnimation(zoomIn);

        productService = new ProductService();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        productList.setAdapter(adapter);

        loadProducts();

        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Töltsd ki a mezőket!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference productsRef = db.collection("products");

            DocumentReference newDocRef = productsRef.document();

            String generatedId = newDocRef.getId();

            Product p = new Product(generatedId, name, price);
            productService.addProduct(p);
            clearFields();
            loadProducts();
        });

        productList.setOnItemClickListener((parent, view, position, id) -> {
            selectedProduct = products.get(position);
            nameEditText.setText(selectedProduct.getName());
            priceEditText.setText(String.valueOf(selectedProduct.getPrice()));
        });

        updateButton.setOnClickListener(v -> {
            if (selectedProduct == null) {
                Toast.makeText(this, "Válassz egy terméket!", Toast.LENGTH_SHORT).show();
                return;
            }

            selectedProduct.setName(nameEditText.getText().toString());
            selectedProduct.setPrice(Double.parseDouble(priceEditText.getText().toString()));
            productService.updateProduct(selectedProduct);
            clearFields();
            loadProducts();
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedProduct == null) {
                Toast.makeText(this, "Válassz egy terméket!", Toast.LENGTH_SHORT).show();
                return;
            }

            productService.deleteProduct(selectedProduct.getId());
            clearFields();
            loadProducts();
        });
    }

    private void loadProducts() {
        productService.getAllProducts(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshot = task.getResult();
                products.clear();
                adapter.clear();
                for (QueryDocumentSnapshot doc : snapshot) {
                    Product p = doc.toObject(Product.class);
                    p.setId(doc.getId());
                    products.add(p);
                    adapter.add(p.getName() + " - " + p.getPrice() + " Ft");
                }
            } else {
                Log.e("ProductCrud", "Hiba a lekérdezés során", task.getException());
            }
        });
    }

    private void clearFields() {
        nameEditText.setText("");
        priceEditText.setText("");
        selectedProduct = null;
    }
}
