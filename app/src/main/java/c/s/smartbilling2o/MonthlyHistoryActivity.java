package c.s.smartbilling2o;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import c.s.smartbilling2o.databinding.ActivityMonthlyHistoryBinding;
import c.s.smartbilling2o.model.Invoice;
import c.s.smartbilling2o.utils.SalesTracker;

public class MonthlyHistoryActivity extends AppCompatActivity {

    private ActivityMonthlyHistoryBinding binding;
    private DbClass dbHelper;
    private RecentBillsAdapter adapter;
    private List<Invoice> invoiceList;
    private static final String TAG = "MonthlyHistory";
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonthlyHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DbClass(this);
        invoiceList = new ArrayList<>();
        adapter = new RecentBillsAdapter(invoiceList);

        binding.rvMonthlyHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMonthlyHistory.setAdapter(adapter);

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        int month = getIntent().getIntExtra("selected_month", -1);
        int year = getIntent().getIntExtra("selected_year", -1);

        Log.d(TAG, "Month: " + month + " Year: " + year);

        if (month == -1 || year == -1) {
            Toast.makeText(this, "Error: Invalid Month or Year selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(SalesTracker.getMonthName(month) + " " + year + " Sales");
        loadMonthlyInvoices(month, year);
    }

    private void loadMonthlyInvoices(int targetMonth, int targetYear) {
        invoiceList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        // Since dates are stored as dd/MM/yyyy, we fetch all and filter in Java 
        // to maintain compatibility with existing data as per SalesTracker logic.
        Cursor cursor = db.query("Invoices", null, null, null, null, null, "id DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                try {
                    Date date = dbDateFormat.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    if (cal.get(Calendar.MONTH) == targetMonth && cal.get(Calendar.YEAR) == targetYear) {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String name = cursor.getString(cursor.getColumnIndexOrThrow("customerName"));
                        double total = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"));
                        invoiceList.add(new Invoice(id, dateStr, name, total));
                    }
                } catch (ParseException e) {
                    Log.e(TAG, "Date parse error: " + dateStr);
                }
            }
            cursor.close();
        }

        Log.d(TAG, "Invoices Count: " + invoiceList.size());

        if (invoiceList.isEmpty()) {
            binding.tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            binding.tvEmptyHistory.setVisibility(View.GONE);
        }
        
        adapter.notifyDataSetChanged();
    }
}
