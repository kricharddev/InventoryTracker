package com.example.android.inventorytracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventorytracker.data.ProductContract.ProductEntry;

/**
 * Created by kyle on 5/1/17.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c,  0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_products, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.product);
        Button saleButton = (Button) view.findViewById(R.id.list_sale_button);
        final TextView qtyTextView = (TextView) view.findViewById(R.id.qty);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int qtyColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QTY_AVAILABLE);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);

        String productName = cursor.getString(nameColumnIndex);
        final String productQty = cursor.getString(qtyColumnIndex);
        String productPrice = "$" + cursor.getString(priceColumnIndex);

        nameTextView.setText(productName);
        qtyTextView.setText(productQty);
        priceTextView.setText(productPrice);

        saleButton.setOnClickListener(new View.OnClickListener() {

            int quantity = (Integer.valueOf(productQty));

            @Override
            public void onClick(View v) {
                quantity = quantity - 1;
                quantity = quantity < 0? 0 : quantity;
                qtyTextView.setText(String.valueOf(quantity));
            }
        });
    }
}