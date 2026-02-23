package c.s.smartbilling2o;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class InvoiceDetails extends AppCompatActivity {

    private static final String TAG = "InvoiceDetails";
    TextView txtInvoiceNo, txtDate, txtPaymentType, txtProductType, txtCustomerName, txtPhoneNumber, txtQuantity, txtAmount, txtTotalAmount, txtAddress, txtStoreName;
    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invoice_details);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        txtInvoiceNo = findViewById(R.id.IDInvoiceNo);
        txtDate = findViewById(R.id.IDDate);
        txtPaymentType = findViewById(R.id.IDPaymentType);
        txtProductType = findViewById(R.id.IDProductType);
        txtCustomerName = findViewById(R.id.IDCustomerName);
        txtPhoneNumber = findViewById(R.id.IDPhoneNumber);
        txtQuantity = findViewById(R.id.IDQuantity);
        txtAmount = findViewById(R.id.IDAmount);
        txtTotalAmount = findViewById(R.id.IDTotalAmount);
        txtAddress = findViewById(R.id.IDAddress);
        txtStoreName = findViewById(R.id.IDStoreName);

        String invoiceId = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            invoiceId = bundle.getString("InvoiceNo");
        }

        if (invoiceId != null) {
            fetchInvoiceDetails(invoiceId);
        } else {
            Log.e(TAG, "Invoice ID is null");
        }
    }

    @SuppressLint("Range")
    private void fetchInvoiceDetails(String invoiceId) {
        DbClass helper = new DbClass(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        
        // Fetch Shop Info
        Cursor shopCursor = helper.getShopInfo();
        if (shopCursor != null && shopCursor.moveToFirst()) {
            txtStoreName.setText(shopCursor.getString(shopCursor.getColumnIndex("shopName")));
            shopCursor.close();
        }

        // Fetch Invoice Info
        Cursor invoiceCursor = db.query("Invoices", null, "id = ?", new String[]{invoiceId}, null, null, null);
        if (invoiceCursor != null && invoiceCursor.moveToFirst()) {
            txtInvoiceNo.setText("#" + invoiceId);
            txtDate.setText(invoiceCursor.getString(invoiceCursor.getColumnIndex("date")));
            txtCustomerName.setText(invoiceCursor.getString(invoiceCursor.getColumnIndex("customerName")));
            txtPhoneNumber.setText(invoiceCursor.getString(invoiceCursor.getColumnIndex("customerPhone")));
            txtAddress.setText(invoiceCursor.getString(invoiceCursor.getColumnIndex("customerAddress")));
            
            double totalAmount = invoiceCursor.getDouble(invoiceCursor.getColumnIndex("totalAmount"));
            String currency = invoiceCursor.getString(invoiceCursor.getColumnIndex("currency"));
            txtTotalAmount.setText(String.format(Locale.getDefault(), "%s %.2f", currency, totalAmount));
            
            // Payment Type
            String paymentType = invoiceCursor.getString(invoiceCursor.getColumnIndex("paymentType"));
            if (paymentType != null && !paymentType.isEmpty()) {
                txtPaymentType.setText(paymentType);
            } else {
                txtPaymentType.setText("Not Specified");
            }
            
            fetchItems(invoiceId, currency);
            
            invoiceCursor.close();
        }
    }

    @SuppressLint("Range")
    private void fetchItems(String invoiceId, String currency) {
        DbClass helper = new DbClass(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        
        Cursor itemCursor = db.query("InvoiceItems", null, "invoiceId = ?", new String[]{invoiceId}, null, null, null);
        
        StringBuilder productNames = new StringBuilder();
        double totalQty = 0;
        double subtotal = 0;

        if (itemCursor != null && itemCursor.moveToFirst()) {
            do {
                String name = itemCursor.getString(itemCursor.getColumnIndex("itemName"));
                double qty = itemCursor.getDouble(itemCursor.getColumnIndex("quantity"));
                double price = itemCursor.getDouble(itemCursor.getColumnIndex("price"));
                double total = itemCursor.getDouble(itemCursor.getColumnIndex("total"));

                if (productNames.length() > 0) productNames.append(", ");
                productNames.append(name);
                totalQty += qty;
                subtotal += total;
            } while (itemCursor.moveToNext());
            itemCursor.close();
        }

        txtProductType.setText(productNames.toString());
        txtQuantity.setText(String.valueOf(totalQty));
        txtAmount.setText(String.format(Locale.getDefault(), "%s %.2f", currency, subtotal));
    }
}
