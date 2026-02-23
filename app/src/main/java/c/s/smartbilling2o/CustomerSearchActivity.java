package c.s.smartbilling2o;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import c.s.smartbilling2o.model.Invoice;

public class CustomerSearchActivity extends AppCompatActivity {

    private TextInputEditText etSearchPhone;
    private MaterialCardView cardCustomerInfo;
    private TextView tvSearchName, tvTotalBills, tvTotalPurchase, tvTotalPaid, tvTotalDue, tvHistoryTitle;
    private RecyclerView rvCustomerHistory;
    private MaterialButton btnViewFullHistory;
    private DbClass dbHelper;
    private RecentBillsAdapter adapter;
    private List<Invoice> invoiceList;
    private String currencySymbol = "₹";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_search);

        dbHelper = new DbClass(this);
        etSearchPhone = findViewById(R.id.etSearchPhone);
        cardCustomerInfo = findViewById(R.id.cardCustomerInfo);
        tvSearchName = findViewById(R.id.tvSearchName);
        tvTotalBills = findViewById(R.id.tvTotalBills);
        tvTotalPurchase = findViewById(R.id.tvTotalPurchase);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);
        tvTotalDue = findViewById(R.id.tvTotalDue);
        tvHistoryTitle = findViewById(R.id.tvHistoryTitle);
        rvCustomerHistory = findViewById(R.id.rvCustomerHistory);
        btnViewFullHistory = findViewById(R.id.btnViewFullHistory);

        loadCurrency();

        invoiceList = new ArrayList<>();
        adapter = new RecentBillsAdapter(invoiceList);
        rvCustomerHistory.setLayoutManager(new LinearLayoutManager(this));
        rvCustomerHistory.setAdapter(adapter);

        etSearchPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String normalizedPhone = s.toString().replaceAll("[\\s\\-()]", "");
                if (normalizedPhone.length() >= 10) {
                    performSearch(normalizedPhone);
                } else {
                    cardCustomerInfo.setVisibility(View.GONE);
                    tvHistoryTitle.setVisibility(View.GONE);
                    invoiceList.clear();
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnViewFullHistory.setOnClickListener(v -> {
            // Can be extended for a dedicated full history view if needed
            Toast.makeText(this, "Showing detailed history below", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
    }

    private void loadCurrency() {
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
            cursor.close();
        }
    }

    private void performSearch(String phone) {
        Cursor summary = dbHelper.getCustomerSummary(phone);
        if (summary != null && summary.moveToFirst()) {
            cardCustomerInfo.setVisibility(View.VISIBLE);
            tvHistoryTitle.setVisibility(View.VISIBLE);

            String name = summary.getString(summary.getColumnIndexOrThrow("customerName"));
            int totalBills = summary.getInt(summary.getColumnIndexOrThrow("totalBills"));
            double totalPurchase = summary.getDouble(summary.getColumnIndexOrThrow("totalPurchase"));
            double totalPaid = summary.getDouble(summary.getColumnIndexOrThrow("totalPaid"));
            double due = totalPurchase - totalPaid;

            tvSearchName.setText(name);
            tvTotalBills.setText("Total Bills: " + totalBills);
            tvTotalPurchase.setText(String.format(Locale.getDefault(), "Total Purchase: %s %.2f", currencySymbol, totalPurchase));
            tvTotalPaid.setText(String.format(Locale.getDefault(), "Paid: %s %.2f", currencySymbol, totalPaid));
            tvTotalDue.setText(String.format(Locale.getDefault(), "Due: %s %.2f", currencySymbol, due));
            summary.close();

            loadHistory(phone);
        } else {
            cardCustomerInfo.setVisibility(View.GONE);
            tvHistoryTitle.setVisibility(View.GONE);
            invoiceList.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "No customer found with this number", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadHistory(String phone) {
        invoiceList.clear();
        Cursor cursor = dbHelper.getCustomerInvoices(phone);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("customerName"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"));
                
                invoiceList.add(new Invoice(id, date, name, total));
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
