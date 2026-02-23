package c.s.smartbilling2o.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

import c.s.smartbilling2o.model.Invoice;
import c.s.smartbilling2o.model.InvoiceItem;
import c.s.smartbilling2o.model.ShopInfo;

@Database(entities = {ShopInfo.class, Invoice.class, InvoiceItem.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract AppDao appDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "smart_billing_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            // Initial default data
                            Executors.newSingleThreadExecutor().execute(() -> {
                                getInstance(context).appDao().insertShopInfo(new ShopInfo(
                                        "My Business", "", "", "", "", "", "₹", "#6200EE"
                                ));
                            });
                        }
                    })
                    .build();
        }
        return instance;
    }
}
