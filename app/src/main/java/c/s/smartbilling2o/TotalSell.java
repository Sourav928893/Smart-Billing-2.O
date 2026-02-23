package c.s.smartbilling2o;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class TotalSell extends AppCompatActivity {

    ArrayList<POJOInvoices> dataModels;
    ListView listView;
    private static ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_sell);

        listView=(ListView)findViewById(R.id.listInvoices);
        dataModels= new ArrayList<>();
        showRecords();


    }

    public void showRecords() {
        DbClass helper = new DbClass(getApplicationContext());
        SQLiteDatabase database = helper.getReadableDatabase();

        try {
            // Using actual column names from Invoices table: id, date, customerName
            String query = "SELECT id, date, customerName FROM Invoices ORDER BY id DESC";

            Cursor cursor = database.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex("id"));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                    @SuppressLint("Range") String customerName = cursor.getString(cursor.getColumnIndex("customerName"));
                    dataModels.add(new POJOInvoices(id, date, customerName));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            Log.d(TAG, "showRecords: " + e.getMessage());
        }

        adapter = new ListAdapter(dataModels, getApplicationContext());
        listView.setAdapter(adapter);
    }
}
