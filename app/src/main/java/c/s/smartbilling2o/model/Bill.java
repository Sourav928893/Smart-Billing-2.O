package c.s.smartbilling2o.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bills")
public class Bill {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String invoiceNumber;
    private String customerName;
    private String date;
    private double totalAmount;
    private String pdfPath;

    public Bill(String invoiceNumber, String customerName, String date, double totalAmount, String pdfPath) {
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.date = date;
        this.totalAmount = totalAmount;
        this.pdfPath = pdfPath;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public String getCustomerName() { return customerName; }
    public String getDate() { return date; }
    public double getTotalAmount() { return totalAmount; }
    public String getPdfPath() { return pdfPath; }
}
