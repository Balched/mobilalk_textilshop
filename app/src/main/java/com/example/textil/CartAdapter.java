package com.example.textil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<Product> cartList;

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvPrice;

        public CartViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartProductName);
            tvPrice = itemView.findViewById(R.id.tvCartProductPrice);
        }
    }

    public CartAdapter(List<Product> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product p = cartList.get(position);
        holder.tvName.setText(p.getName());
        holder.tvPrice.setText(String.format("%.2f Ft", p.getPrice()));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
