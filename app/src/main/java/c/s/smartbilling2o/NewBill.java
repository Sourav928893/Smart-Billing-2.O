package c.s.smartbilling2o;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewBill extends AppCompatActivity {

    private TextInputEditText etCustomerName, etCustomerPhone, etCustomerAddress, etDiscountPercent, etPaidAmount, etInvoiceDate;
    private AutoCompleteTextView spinnerPaymentType;
    private LinearLayout layoutItems, layoutCustomerSummary;
    private TextView tvTotalAmount, tvPrevPurchase, tvOutstanding, tvLastVisit;
    private MaterialButton btnAddItem, btnGeneratePdf;
    private DbClass dbHelper;
    private double subtotal = 0.0;
    private double finalTotal = 0.0;
    private double discountValue = 0.0;
    private String appliedDiscountLabel = "";
    private String currencySymbol = "₹";
    private String themeColor = "#6200EE";
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);

        dbHelper = new DbClass(this);
        etCustomerName = findViewById(R.id.etCustomerName);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etDiscountPercent = findViewById(R.id.etDiscountPercent);
        etPaidAmount = findViewById(R.id.etPaidAmount);
        etInvoiceDate = findViewById(R.id.etInvoiceDate);
        spinnerPaymentType = findViewById(R.id.spinnerPaymentType);
        layoutItems = findViewById(R.id.layoutItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        
        layoutCustomerSummary = findViewById(R.id.layoutCustomerSummary);
        tvPrevPurchase = findViewById(R.id.tvPrevPurchase);
        tvOutstanding = findViewById(R.id.tvOutstanding);
        tvLastVisit = findViewById(R.id.tvLastVisit);

        setupPaymentTypeSpinner();
        loadShopSettings();
        applyTheme();

        addNewItemRow();

        btnAddItem.setOnClickListener(v -> addNewItemRow());
        
        etInvoiceDate.setOnClickListener(v -> showDatePicker());

        etCustomerPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String normalizedPhone = s.toString().replaceAll("[\\s\\-()]", "");
                if (normalizedPhone.length() >= 10) {
                    checkExistingCustomer(normalizedPhone);
                } else {
                    layoutCustomerSummary.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        TextWatcher calculationWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        etDiscountPercent.addTextChangedListener(calculationWatcher);

        btnGeneratePdf.setOnClickListener(v -> {
            if (validateInputs()) {
                saveBillAndGeneratePdf();
            }
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void setupPaymentTypeSpinner() {
        String[] options = {"Cash", "UPI", "Card", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, options);
        spinnerPaymentType.setAdapter(adapter);
    }

    private void checkExistingCustomer(String phone) {
        Cursor cursor = dbHelper.getCustomerSummary(phone);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("customerName"));
            double totalPurchase = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPurchase"));
            double totalPaid = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPaid"));
            String lastDate = cursor.getString(cursor.getColumnIndexOrThrow("lastDate"));

            etCustomerName.setText(name);
            layoutCustomerSummary.setVisibility(View.VISIBLE);
            tvPrevPurchase.setText(String.format(Locale.getDefault(), "Total Purchase: %s %.2f", currencySymbol, totalPurchase));
            tvOutstanding.setText(String.format(Locale.getDefault(), "Outstanding: %s %.2f", currencySymbol, (totalPurchase - totalPaid)));
            tvLastVisit.setText("Last Purchase: " + lastDate);
            cursor.close();
        } else {
            layoutCustomerSummary.setVisibility(View.GONE);
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            String format = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
            etInvoiceDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadShopSettings() {
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
            themeColor = cursor.getString(cursor.getColumnIndexOrThrow("themeColor"));
            cursor.close();
        }
    }

    private void applyTheme() {
        int color = Color.parseColor(themeColor);
        btnGeneratePdf.setBackgroundColor(color);
        btnAddItem.setTextColor(color);
        tvTotalAmount.setTextColor(color);
    }

    private void addNewItemRow() {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_product_input, layoutItems, false);
        EditText etQuantity = itemView.findViewById(R.id.etQuantity);
        EditText etPrice = itemView.findViewById(R.id.etPrice);
        ImageButton btnRemove = itemView.findViewById(R.id.btnRemoveItem);

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateTotal();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etQuantity.addTextChangedListener(watcher);
        etPrice.addTextChangedListener(watcher);

        btnRemove.setOnClickListener(v -> {
            if (layoutItems.getChildCount() > 1) {
                layoutItems.removeView(itemView);
                calculateTotal();
            }
        });

        layoutItems.addView(itemView);
    }

    private void calculateTotal() {
        subtotal = 0.0;
        for (int i = 0; i < layoutItems.getChildCount(); i++) {
            View v = layoutItems.getChildAt(i);
            EditText etQty = v.findViewById(R.id.etQuantity);
            EditText etPrice = v.findViewById(R.id.etPrice);

            String qtyStr = etQty.getText().toString();
            String priceStr = etPrice.getText().toString();

            if (!qtyStr.isEmpty() && !priceStr.isEmpty()) {
                subtotal += Double.parseDouble(qtyStr) * Double.parseDouble(priceStr);
            }
        }

        String percStr = etDiscountPercent.getText().toString();
        discountValue = 0.0;
        appliedDiscountLabel = "";

        if (!percStr.isEmpty() && Double.parseDouble(percStr) > 0) {
            double percent = Double.parseDouble(percStr);
            discountValue = (subtotal * percent) / 100.0;
            appliedDiscountLabel = "Discount (" + percStr + "%)";
        }

        if (discountValue > subtotal) {
            discountValue = subtotal;
        }

        finalTotal = subtotal - discountValue;
        tvTotalAmount.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, finalTotal));
    }

    private boolean validateInputs() {
        if (etCustomerPhone.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Customer Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etCustomerName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter Customer Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (subtotal <= 0) {
            Toast.makeText(this, "Add at least one item with price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void saveBillAndGeneratePdf() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String customerName = etCustomerName.getText().toString().trim();
        String customerPhoneRaw = etCustomerPhone.getText().toString().trim();
        String customerPhone = customerPhoneRaw.replaceAll("[\\s\\-()]", "");
        String customerAddress = etCustomerAddress.getText().toString().trim();
        String paidAmtStr = etPaidAmount.getText().toString().trim();
        double paidAmount = paidAmtStr.isEmpty() ? 0.0 : Double.parseDouble(paidAmtStr);
        double remainingDue = finalTotal - paidAmount;
        String paymentType = spinnerPaymentType.getText().toString().trim();
        
        String customDate = etInvoiceDate.getText().toString().trim();
        String date = customDate.isEmpty() ? new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()) : customDate;

        ContentValues invoiceValues = new ContentValues();
        invoiceValues.put("customerName", customerName);
        invoiceValues.put("customerPhone", customerPhone);
        invoiceValues.put("customerAddress", customerAddress);
        invoiceValues.put("date", date);
        invoiceValues.put("totalAmount", finalTotal);
        invoiceValues.put("paidAmount", paidAmount);
        invoiceValues.put("remainingDue", remainingDue);
        invoiceValues.put("paymentType", paymentType);
        invoiceValues.put("currency", currencySymbol);

        long invoiceId = db.insert("Invoices", null, invoiceValues);

        List<BillItem> items = new ArrayList<>();
        for (int i = 0; i < layoutItems.getChildCount(); i++) {
            View v = layoutItems.getChildAt(i);
            String name = ((EditText) v.findViewById(R.id.etItemName)).getText().toString().trim();
            String qty = ((EditText) v.findViewById(R.id.etQuantity)).getText().toString().trim();
            String price = ((EditText) v.findViewById(R.id.etPrice)).getText().toString().trim();

            if (!name.isEmpty() && !qty.isEmpty() && !price.isEmpty()) {
                double total = Double.parseDouble(qty) * Double.parseDouble(price);
                ContentValues itemValues = new ContentValues();
                itemValues.put("invoiceId", invoiceId);
                itemValues.put("itemName", name);
                itemValues.put("quantity", Double.parseDouble(qty));
                itemValues.put("price", Double.parseDouble(price));
                itemValues.put("total", total);
                db.insert("InvoiceItems", null, itemValues);
                items.add(new BillItem(name, Double.parseDouble(qty), Double.parseDouble(price), total));
            }
        }

        generatePdf(invoiceId, customerName, customerPhone, customerAddress, date, paymentType, items);
    }

    private void generatePdf(long invoiceId, String customerName, String customerPhone, String customerAddress, String date, String paymentType, List<BillItem> items) {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        String shopName = "My Business", ownerName = "", shopAddress = "", shopContact = "", gstNumber = "", logoPath = "";
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            shopName = cursor.getString(cursor.getColumnIndexOrThrow("shopName"));
            ownerName = cursor.getString(cursor.getColumnIndexOrThrow("ownerName"));
            shopAddress = cursor.getString(cursor.getColumnIndexOrThrow("shopAddress"));
            shopContact = cursor.getString(cursor.getColumnIndexOrThrow("shopContact"));
            gstNumber = cursor.getString(cursor.getColumnIndexOrThrow("gstNumber"));
            logoPath = cursor.getString(cursor.getColumnIndexOrThrow("logoPath"));
            cursor.close();
        }

        int startX = 45;
        int y = 50;

        // Requirement: Make Shop Logo Optional. If missing, skip it.
        if (logoPath != null && !logoPath.isEmpty()) {
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                try {
                    Bitmap logo = BitmapFactory.decodeFile(logoPath);
                    if (logo != null) {
                        float logoSize = 70f;
                        float ratio = Math.min(logoSize / logo.getWidth(), logoSize / logo.getHeight());
                        float finalWidth = logo.getWidth() * ratio;
                        float finalHeight = logo.getHeight() * ratio;
                        RectF targetRect = new RectF(595 - startX - finalWidth, y, 595 - startX, y + finalHeight);
                        canvas.drawBitmap(logo, null, targetRect, paint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.LEFT);
        
        paint.setTextSize(22);
        paint.setFakeBoldText(true);
        canvas.drawText(shopName, startX, y + 20, paint);
        y += 40;

        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        if (ownerName != null && !ownerName.isEmpty()) {
            canvas.drawText(ownerName, startX, y, paint);
            y += 15;
        }

        if (shopAddress != null && !shopAddress.isEmpty()) {
            canvas.drawText(shopAddress, startX, y, paint);
            y += 15;
        }

        if (shopContact != null && !shopContact.isEmpty()) {
            canvas.drawText("Ph: " + shopContact, startX, y, paint);
            y += 15;
        }

        if (gstNumber != null && !gstNumber.isEmpty()) {
            canvas.drawText("GSTIN: " + gstNumber, startX, y, paint);
            y += 15;
        }

        y += 10;
        paint.setStrokeWidth(1.2f);
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(startX, y, 550, y, paint);
        y += 30;

        paint.setColor(Color.BLACK);
        paint.setTextSize(18);
        paint.setFakeBoldText(true);
        canvas.drawText("INVOICE", startX, y, paint);
        y += 25;

        paint.setTextSize(12);
        paint.setFakeBoldText(false);
        canvas.drawText("Invoice No: #" + invoiceId, startX, y, paint);
        
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Date: " + date, 550, y, paint);
        
        y += 20;
        paint.setColor(Color.LTGRAY);
        canvas.drawLine(startX, y, 550, y, paint);
        y += 30;

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(true);
        canvas.drawText("Bill To:", startX, y, paint);
        y += 18;
        paint.setFakeBoldText(false);
        canvas.drawText(customerName, startX, y, paint);
        y += 15;
        if (customerPhone != null && !customerPhone.isEmpty()) {
            canvas.drawText("Contact: " + customerPhone, startX, y, paint);
            y += 15;
        }
        if (customerAddress != null && !customerAddress.isEmpty()) {
            canvas.drawText("Address: " + customerAddress, startX, y, paint);
            y += 15;
        }

        y += 25;

        paint.setFakeBoldText(true);
        paint.setColor(Color.parseColor(themeColor));
        canvas.drawRect(startX, y, 550, y + 25, paint);
        
        paint.setColor(Color.WHITE);
        canvas.drawText("Description", startX + 10, y + 17, paint);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Qty", 350, y + 17, paint);
        canvas.drawText("Rate", 430, y + 17, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Amount", 540, y + 17, paint);

        paint.setColor(Color.BLACK);
        paint.setFakeBoldText(false);
        y += 45;

        for (BillItem item : items) {
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(item.name, startX + 10, y, paint);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(item.qty), 350, y, paint);
            canvas.drawText(String.format("%.2f", item.price), 430, y, paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(String.format("%.2f", item.total), 540, y, paint);
            y += 20;
            paint.setColor(Color.LTGRAY);
            canvas.drawLine(startX, y - 5, 550, y - 5, paint);
            paint.setColor(Color.BLACK);
            y += 15;
        }

        y += 10;
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(12);
        canvas.drawText("Subtotal: " + currencySymbol + " " + String.format("%.2f", subtotal), 540, y, paint);
        y += 20;
        
        if (discountValue > 0) {
            paint.setColor(Color.parseColor("#D32F2F"));
            canvas.drawText(appliedDiscountLabel + ": - " + currencySymbol + " " + String.format("%.2f", discountValue), 540, y, paint);
            y += 20;
            paint.setColor(Color.BLACK);
        }
        
        paint.setTextSize(16);
        paint.setFakeBoldText(true);
        canvas.drawText("Grand Total: " + currencySymbol + " " + String.format("%.2f", finalTotal), 540, y, paint);
        y += 20;
        
        if (paymentType != null && !paymentType.isEmpty()) {
            paint.setTextSize(12);
            paint.setFakeBoldText(false);
            canvas.drawText("Payment: " + paymentType, 540, y, paint);
        }

        y = 810;
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(10);
        paint.setFakeBoldText(false);
        paint.setColor(Color.GRAY);
        canvas.drawText("This is a computer generated invoice.", 595/2f, y, paint);

        document.finishPage(page);

        File pdfFile = new File(getExternalFilesDir(null), "Invoice_" + invoiceId + ".pdf");
        try {
            document.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF Generated Successfully", Toast.LENGTH_SHORT).show();
            sharePdf(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
        document.close();
    }

    private void sharePdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Invoice"));
        finish();
    }

    private static class BillItem {
        String name;
        double qty;
        double price, total;
        BillItem(String name, double qty, double price, double total) {
            this.name = name; this.qty = qty; this.price = price; this.total = total;
        }
    }
}
