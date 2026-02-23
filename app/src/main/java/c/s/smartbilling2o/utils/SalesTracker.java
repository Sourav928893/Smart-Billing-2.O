package c.s.smartbilling2o.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import c.s.smartbilling2o.DbClass;

public class SalesTracker {

    private static final SimpleDateFormat DB_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public static double getMonthlySales(Context context, int month, int year) {
        DbClass dbHelper = new DbClass(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        double total = 0;

        // Query all invoices and filter in Java to handle the date format stored as TEXT
        Cursor cursor = db.query("Invoices", new String[]{"totalAmount", "date"}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String dateStr = cursor.getString(cursor.getColumnIndexOrThrow("date"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("totalAmount"));

                try {
                    Date date = DB_DATE_FORMAT.parse(dateStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    if (cal.get(Calendar.MONTH) == month && cal.get(Calendar.YEAR) == year) {
                        total += amount;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return total;
    }

    public static String getMonthName(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month);
        return new SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.getTime());
    }
}
