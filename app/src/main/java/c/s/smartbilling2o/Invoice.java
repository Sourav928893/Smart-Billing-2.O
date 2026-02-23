package c.s.smartbilling2o;

public class Invoice {
    private String id;
    private String date;
    private String customerName;
    private double totalAmount;

    public Invoice(String id, String date, String customerName, double totalAmount) {
        this.id = id;
        this.date = date;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public String getId() { return id; }
    public String getDate() { return date; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
}
