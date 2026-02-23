package c.s.smartbilling2o.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "InvoiceItems",
        foreignKeys = @ForeignKey(entity = Invoice.class,
                parentColumns = "id",
                childColumns = "invoiceId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("invoiceId")})
public class InvoiceItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int invoiceId;
    private String itemName;
    private double quantity;
    private double price;
    private double total;

    public InvoiceItem(int invoiceId, String itemName, double quantity, double price, double total) {
        this.invoiceId = invoiceId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInvoiceId() { return invoiceId; }
    public String getItemName() { return itemName; }
    public double getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
}
