package c.s.smartbilling2o.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import c.s.smartbilling2o.model.Invoice;
import c.s.smartbilling2o.model.InvoiceItem;
import c.s.smartbilling2o.model.ShopInfo;

@Dao
public interface AppDao {
    @Query("SELECT * FROM ShopInfo LIMIT 1")
    ShopInfo getShopInfo();

    @Insert
    void insertShopInfo(ShopInfo shopInfo);

    @Update
    void updateShopInfo(ShopInfo shopInfo);

    @Insert
    long insertInvoice(Invoice invoice);

    @Query("SELECT * FROM Invoices ORDER BY id DESC")
    List<Invoice> getAllInvoices();

    @Query("SELECT * FROM Invoices ORDER BY id DESC LIMIT :limit")
    List<Invoice> getRecentInvoices(int limit);

    @Query("SELECT * FROM Invoices WHERE customerPhone = :phone ORDER BY id DESC")
    List<Invoice> getCustomerInvoices(String phone);

    @Query("SELECT customerName, COUNT(id) as totalBills, SUM(totalAmount) as totalPurchase, SUM(paidAmount) as totalPaid, SUM(remainingDue) as totalDue, MAX(date) as lastDate FROM Invoices WHERE customerPhone = :phone GROUP BY customerPhone")
    CustomerSummary getCustomerSummary(String phone);

    @Insert
    void insertInvoiceItems(List<InvoiceItem> items);

    @Query("SELECT * FROM InvoiceItems WHERE invoiceId = :invoiceId")
    List<InvoiceItem> getInvoiceItems(int invoiceId);

    static class CustomerSummary {
        public String customerName;
        public int totalBills;
        public double totalPurchase;
        public double totalPaid;
        public double totalDue;
        public String lastDate;
    }
}
