package com.example.textil;

import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.*;


public class ProductService {
    private FirebaseFirestore db;
    private CollectionReference productsRef;

    public ProductService() {
        db = FirebaseFirestore.getInstance();
        productsRef = db.collection("products");
    }

    // CREATE
    public void addProduct(Product product) {
        productsRef.add(product)
                .addOnSuccessListener(documentReference -> {
                    Log.d("ProductService", "Product added with ID:");
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Error adding product", e);
                });
    }

    // READ
    public void getAllProducts(OnCompleteListener<QuerySnapshot> listener) {
        productsRef.get().addOnCompleteListener(listener);
    }

    // UPDATE
    public void updateProduct(Product product) {
        if (product.getId() == null) {
            Log.e("ProductService", "Product ID is null. Cannot update.");
            return;
        }

        productsRef.document(product.getId()).set(product)
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProductService", "Product updated");
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Error updating product", e);
                });
    }

    // DELETE
    public void deleteProduct(String productId) {
        productsRef.document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("ProductService", "Product deleted");
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductService", "Error deleting product", e);
                });
    }

    public void getProductsByPriceFiltered(double minPrice, int limit, OnCompleteListener<QuerySnapshot> listener) {
        productsRef.whereGreaterThan("price", minPrice)
                .orderBy("price", Query.Direction.ASCENDING)
                .limit(limit)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getProductsOrderedByNameAndPrice(OnCompleteListener<QuerySnapshot> listener) {
        productsRef.orderBy("name")
                .orderBy("price", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(listener);
    }

    public void getPaginatedProducts(DocumentSnapshot startAfterDoc, int limit, OnCompleteListener<QuerySnapshot> listener) {
        Query query = productsRef.orderBy("name").limit(limit);
        if (startAfterDoc != null) {
            query = query.startAfter(startAfterDoc);
        }
        query.get().addOnCompleteListener(listener);
    }
}
