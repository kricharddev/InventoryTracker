package com.example.android.inventorytracker;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventorytracker.data.ProductContract.ProductEntry;

/**
 * Created by kyle on 4/26/17.
 */

public class CrudProductActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "CrudProductActivity";

    private static final int EXISTING_PRODUCT_LOADER = 0;

    private Uri mCurrentProductUri;

    private EditText mName;

    private EditText mQty;

    private EditText mPrice;

    private EditText mInfo;

    private EditText mEmail;

    private EditText mImage;

    private String emailBody;

    private String emailSubject;

    private String emailAddress;

    Button deleteButton;

    Button orderButton;

    Button shipButton;

    Button saleButton;

    private boolean mProductHasChanged = false;

    Button saveProductButton;

    private int quantity = 0;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crud_product);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();

        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.add_product_button));

        } else {
            setTitle(getString(R.string.product_details));

            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

            Log.v(TAG, mCurrentProductUri.toString());

        }

        mName = (EditText) findViewById(R.id.edit_text_add_product);
        mQty = (EditText) findViewById(R.id.edit_text_qty);
        mPrice = (EditText) findViewById(R.id.edit_text_add_price);
        mInfo = (EditText) findViewById(R.id.edit_text_add_product_info);
        mEmail = (EditText) findViewById(R.id.edit_text_add_product_email);
        mImage = (EditText) findViewById(R.id.image_file_location);

        mName.setOnTouchListener(mTouchListener);
        mQty.setOnTouchListener(mTouchListener);
        mPrice.setOnTouchListener(mTouchListener);
        mInfo.setOnTouchListener(mTouchListener);
        mEmail.setOnTouchListener(mTouchListener);
        mImage.setOnTouchListener(mTouchListener);

        // Save button saves product and finishes activity
        saveProductButton = (Button) findViewById(R.id.save_button);
        saveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
                finish();
            }
        });

        deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        orderButton = (Button) findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            // Launch email app to order more product
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
                intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
                intent.putExtra(Intent.EXTRA_TEXT, emailBody);
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        shipButton = (Button) findViewById(R.id.shipment_button);
        shipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity + 1;
                displayQuantity(quantity);

            }
        });

        saleButton = (Button) findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity = quantity - 1;
                quantity = quantity < 0? 0 : quantity;
                displayQuantity(quantity);

            }
        });

        hideViews();
    }

    public void displayQuantity(int quantity) {
        mQty.setText(String.valueOf(quantity));
    }

    private void saveProduct() {

        String nameString = mName.getText().toString().trim();
        String qtyString = mQty.getText().toString().trim();
        String priceString = mPrice.getText().toString().trim();
        String infoString = mInfo.getText().toString().trim();
        String emailString = mEmail.getText().toString().trim();
        String imageString = mImage.getText().toString().trim();

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(qtyString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(infoString)) {
            return;
        }

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(qtyString) ||
                TextUtils.isEmpty(infoString)
                || TextUtils.isEmpty(emailString)) {
            Toast.makeText(this, (R.string.missing_data), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_QTY_AVAILABLE, qtyString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_INFO, infoString);
        values.put(ProductEntry.COLUMN_PRODUCT_EMAIL, emailString);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageString);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
                NavUtils.navigateUpFromSameTask(CrudProductActivity.this);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void hideViews() {
        if (mCurrentProductUri == null) {
            orderButton.setVisibility(View.GONE);
            saleButton.setVisibility(View.GONE);
            shipButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(CrudProductActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(CrudProductActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_QTY_AVAILABLE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_INFO,
                ProductEntry.COLUMN_PRODUCT_EMAIL,
                ProductEntry.COLUMN_PRODUCT_IMAGE };

        Log.v(TAG, "onCreateLoader1");
        Log.v(TAG, mCurrentProductUri.toString());

        return new CursorLoader(this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current product
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int qtyColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_QTY_AVAILABLE);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int infoColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_INFO);
            int emailColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_EMAIL);
            int imageColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String qty = cursor.getString(qtyColumnIndex);
            String price = cursor.getString(priceColumnIndex);
            String info = cursor.getString(infoColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String image = cursor.getString(imageColumnIndex);

            // Getting Email info for "Order More" Button
            emailAddress = cursor.getString(emailColumnIndex);
            emailBody = name + "\n" + info + "\n" + "Request to order X additional product";
            emailSubject = "Order request for: " + name;

            // Get quantity for updating with sale/receive buttons
            quantity = cursor.getInt(qtyColumnIndex);

            mName.setText(name);
            mQty.setText(qty);
            mPrice.setText(price);
            mInfo.setText(info);
            mEmail.setText(email);
            mImage.setText(image);

            Log.v(TAG, "LoaderFinished");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mName.setText("");
        mQty.setText("");
        mInfo.setText("");

        Log.v(TAG, "LoaderReset");
    }
}