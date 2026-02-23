package c.s.smartbilling2o.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "Invoices")
public class Invoice {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String date;
    private double totalAmount;
    private double paidAmount;
    private double remainingDue;
    private String paymentType;
    private String currency;

    public Invoice() {
    }

    @Ignore
    public Invoice(int id, String date, String customerName, double totalAmount) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    @Ignore
    public Invoice(String customerName, String customerAddress, String customerPhone, String date, double totalAmount, double paidAmount, double remainingDue, String paymentType, String currency) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.date = date;
        this.totalAmount = totalAmount;
        this.paidAmount = paidAmount;
        this.remainingDue = remainingDue;
        this.paymentType = paymentType;
        this.currency = currency;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) { this.paidAmount = paidAmount; }

    public double getRemainingDue() { return remainingDue; }
    public void setRemainingDue(double remainingDue) { this.remainingDue = remainingDue; }

    public String getPaymentType() { return paymentType; }
    public void setPaymentType(String paymentType) { this.paymentType = paymentType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
