package c.s.smartbilling2o;

public class POJOInvoices {
    private String id;
    private String date;
    private String customerName;
    private double totalAmount;

    public POJOInvoices(String id, String date, String customerName, double totalAmount) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public POJOInvoices(String id, String date, String customerName) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
    }

    public String getId() { return id; }
    public String getInvoiceNo() { return id; }
    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public String getUsername() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
}
