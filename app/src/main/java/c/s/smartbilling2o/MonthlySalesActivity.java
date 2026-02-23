package c.s.smartbilling2o;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import c.s.smartbilling2o.databinding.ActivityMonthlySalesBinding;
import c.s.smartbilling2o.utils.SalesTracker;

public class MonthlySalesActivity extends AppCompatActivity {

    private ActivityMonthlySalesBinding binding;
    private DbClass dbHelper;
    private String currencySymbol = "₹";
    private int selectedMonth = -1;
    private int selectedYear = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMonthlySalesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DbClass(this);
        loadCurrency();

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        loadSalesData();

        binding.btnSelectMonth.setOnClickListener(v -> showMonthPicker());
        
        binding.btnViewHistory.setOnClickListener(v -> {
            if (selectedMonth != -1 && selectedYear != -1) {
                Intent intent = new Intent(MonthlySalesActivity.this, MonthlyHistoryActivity.class);
                intent.putExtra("selected_month", selectedMonth);
                intent.putExtra("selected_year", selectedYear);
                startActivity(intent);
            } else {
                // Default to current month if none selected
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(MonthlySalesActivity.this, MonthlyHistoryActivity.class);
                intent.putExtra("selected_month", cal.get(Calendar.MONTH));
                intent.putExtra("selected_year", cal.get(Calendar.YEAR));
                startActivity(intent);
            }
        });
    }

    private void loadCurrency() {
        Cursor cursor = dbHelper.getShopInfo();
        if (cursor != null && cursor.moveToFirst()) {
            currencySymbol = cursor.getString(cursor.getColumnIndexOrThrow("currency"));
            cursor.close();
        }
    }

    private void loadSalesData() {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);
        
        selectedMonth = currentMonth;
        selectedYear = currentYear;

        // Current Month
        double currentTotal = SalesTracker.getMonthlySales(this, currentMonth, currentYear);
        binding.tvCurrentMonthName.setText(SalesTracker.getMonthName(currentMonth) + " " + currentYear);
        binding.tvCurrentMonthTotal.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, currentTotal));

        // Previous Month
        Calendar prevCal = (Calendar) cal.clone();
        prevCal.add(Calendar.MONTH, -1);
        int prevMonth = prevCal.get(Calendar.MONTH);
        int prevYear = prevCal.get(Calendar.YEAR);
        double prevTotal = SalesTracker.getMonthlySales(this, prevMonth, prevYear);
        binding.tvPrevMonthName.setText(SalesTracker.getMonthName(prevMonth) + " " + prevYear);
        binding.tvPrevMonthTotal.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, prevTotal));
    }

    private void showMonthPicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedMonth = month;
            selectedYear = year;
            double selectedTotal = SalesTracker.getMonthlySales(this, month, year);
            binding.layoutSelectedMonth.setVisibility(View.VISIBLE);
            binding.tvSelectedMonthName.setText(SalesTracker.getMonthName(month) + " " + year);
            binding.tvSelectedMonthTotal.setText(String.format(Locale.getDefault(), "%s %.2f", currencySymbol, selectedTotal));
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        
        // Hide day picker (approximate for simple month selection)
        int dayId = getResources().getIdentifier("day", "id", "android");
        if (dayId != 0) {
            View dayPicker = datePickerDialog.getDatePicker().findViewById(dayId);
            if (dayPicker != null) dayPicker.setVisibility(View.GONE);
        }
        datePickerDialog.show();
    }
}
