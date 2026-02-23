package c.s.smartbilling2o;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import c.s.smartbilling2o.model.Invoice;
import c.s.smartbilling2o.subscription.SubscriptionActivity;
import c.s.smartbilling2o.subscription.SubscriptionManager;
import c.s.smartbilling2o.utils.SalesTracker;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvRecentBills;
    private ExtendedFloatingActionButton btnAddBill;
    private ImageView btnSettings;
    private TextView tvMainMonthTotal, tvSubscriptionTimer;
    private MaterialCardView cardMonthlySales, cardSubscription;
    private MaterialButton btnViewAllSales, btnUpgradeNow;
    private DbClass dbHelper;
    private RecentBillsAdapter adapter;
    private List<Invoice> invoiceList;
    private String currencySymbol = "₹";
    private SubscriptionManager subscriptionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        subscriptionManager = new SubscriptionManager(this);
        checkSubscription();

        dbHelper = new DbClass(this);
        rvRecentBills = findViewById(R.id.rvRecentBills);
        btnAddBill = findViewById(R.id.btnAddBill);
        btnSettings = findViewById(R.id.btnSettings);
        tvMainMonthTotal = findViewById(R.id.tvMainMonthTotal);
        cardMonthlySales = findViewById(R.id.cardMonthlySales);
        btnViewAllSales = findViewById(R.id.btnViewAllSales);
        
        // Subscription Banner Views
        cardSubscription = findViewById(R.id.cardSubscription);
        tvSubscriptionTimer = findViewById(R.id.tvSubscriptionTimer);
        btnUpgradeNow = findViewById(R.id.btnUpgradeNow);

        invoiceList = new ArrayList<>();
        adapter = new RecentBillsAdapter(invoiceList);
        rvRecentBills.setLayoutManager(new LinearLayoutManager(this));
        rvRecentBills.setAdapter(adapter);

        btnAddBill.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NewBill.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        cardMonthlySales.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MonthlySalesActivity.class));
        });

        btnViewAllSales.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MonthlySalesActivity.class));
        });

        btnUpgradeNow.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubscriptionActivity.class));
        });
        
        loadCurrency();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkSubscription(); // Re-check on resume
        updateSubscriptionBanner();
        loadRecentBills();
        updateMonthlyTotal();
    }

    private void checkSubscription() {
        if (subscriptionManager.isTrialExpired() && !subscriptionManager.isPremium()) {
            Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }

    private void updateSubscriptionBanner() {
        if (subscriptionManager.isPremium()) {
            cardSubscription.setStrokeColor(Color.parseColor("#4CAF50")); // Green
            tvSubscriptionTimer.setText("Premium: " + subscriptionManager.getFormattedRemainingTime(subscriptionManager.getRemainingPremiumTime()));
            tvSubscriptionTimer.setTextColor(Color.parseColor("#4CAF50"));
            btnUpgradeNow.setVisibility(View.GONE);
        } else {
            long remainingTrial = subscriptionManager.getRemainingTrialTime();
            tvSubscriptionTimer.setText("Trial: " + subscriptionManager.getFormattedRemainingTime(remainingTrial));
            
            if (remainingTrial < (6L * 60 * 60 * 1000)) { // Less than 6 hours
                cardSubscription.setStrokeColor(Color.RED);
                tvSubscriptionTimer.setTextColor(Color.RED);
            }
        }
    }

    private void loadCurrency() {
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
            cursor.close();
        }
    }

    private void updateMonthlyTotal() {
        Calendar cal = Calendar.getInstance();
        double total = SalesTracker.getMonthlySales(this, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR));
        tvMainMonthTotal.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, total));
    }

    private void loadRecentBills() {
        invoiceList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("Invoices", null, null, null, null, null, "id DESC", "10");
        
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Invoice invoice = new Invoice();
                invoice.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                invoice.setCustomerName(cursor.getString(cursor.getColumnIndexOrThrow("customerName")));
                invoice.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                invoice.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount")));
                invoiceList.add(invoice);
            } while (cursor.moveToNext());
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }
}
