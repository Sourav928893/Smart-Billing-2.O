package c.s.smartbilling2o;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbClass extends SQLiteOpenHelper {

    public static final String DATABASE = "SmartBilling.db";
    public static final int VERSION = 7;

    public DbClass(Context ctx) {
        super(ctx, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Shop Info Table
        db.execSQL("CREATE TABLE ShopInfo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "shopName TEXT, " +
                "ownerName TEXT, " +
                "shopAddress TEXT, " +
                "shopContact TEXT, " +
                "gstNumber TEXT, " +
                "logoPath TEXT, " +
                "currency TEXT, " +
                "themeColor TEXT)");

        // Invoices Table
        db.execSQL("CREATE TABLE Invoices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customerName TEXT, " +
                "customerAddress TEXT, " +
                "customerPhone TEXT, " +
                "date TEXT, " +
                "totalAmount REAL, " +
                "paidAmount REAL DEFAULT 0.0, " +
                "remainingDue REAL DEFAULT 0.0, " +
                "paymentType TEXT, " +
                "currency TEXT)");

        // Invoice Items Table
        db.execSQL("CREATE TABLE InvoiceItems (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "invoiceId INTEGER, " +
                "itemName TEXT, " +
                "quantity REAL, " +
                "price REAL, " +
                "total REAL, " +
                "FOREIGN KEY(invoiceId) REFERENCES Invoices(id))");

        // Initial default data
        ContentValues values = new ContentValues();
        values.put("shopName", "My Business");
        values.put("currency", "₹");
        values.put("themeColor", "#6200EE");
        db.insert("ShopInfo", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE Invoices ADD COLUMN paidAmount REAL DEFAULT 0.0");
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE Invoices ADD COLUMN remainingDue REAL DEFAULT 0.0");
            db.execSQL("ALTER TABLE InvoiceItems ADD COLUMN quantity REAL");
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE Invoices ADD COLUMN paymentType TEXT");
        }
    }

    public Cursor getShopInfo() {
        return getReadableDatabase().rawQuery("SELECT * FROM ShopInfo LIMIT 1", null);
    }

    public void updateShopInfo(String name, String owner, String address, String contact, String gst, String logo, String currency, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("shopName", name);
        values.put("ownerName", owner);
        values.put("shopAddress", address);
        values.put("shopContact", contact);
        values.put("gstNumber", gst);
        values.put("logoPath", logo);
        values.put("currency", currency);
        values.put("themeColor", color);
        db.update("ShopInfo", values, "id = 1", null);
    }

    public Cursor getCustomerSummary(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT customerName, " +
                "COUNT(id) as totalBills, " +
                "SUM(totalAmount) as totalPurchase, " +
                "SUM(paidAmount) as totalPaid, " +
                "SUM(remainingDue) as totalDue, " +
                "MAX(date) as lastDate " +
                "FROM Invoices WHERE customerPhone = ? " +
                "GROUP BY customerPhone";
        return db.rawQuery(query, new String[]{phone});
    }

    public Cursor getCustomerInvoices(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query("Invoices", null, "customerPhone = ?", new String[]{phone}, null, null, "id DESC");
    }
}
