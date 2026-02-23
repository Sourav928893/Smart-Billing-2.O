package c.s.smartbilling2o.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ShopInfo")
public class ShopInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String shopName;
    private String ownerName;
    private String shopAddress;
    private String shopContact;
    private String gstNumber;
    private String logoPath;
    private String currency;
    private String themeColor;

    public ShopInfo(String shopName, String ownerName, String shopAddress, String shopContact, String gstNumber, String logoPath, String currency, String themeColor) {
        this.shopName = shopName;
        this.ownerName = ownerName;
        this.shopAddress = shopAddress;
        this.shopContact = shopContact;
        this.gstNumber = gstNumber;
        this.logoPath = logoPath;
        this.currency = currency;
        this.themeColor = themeColor;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getShopName() { return shopName; }
    public String getOwnerName() { return ownerName; }
    public String getShopAddress() { return shopAddress; }
    public String getShopContact() { return shopContact; }
    public String getGstNumber() { return gstNumber; }
    public String getLogoPath() { return logoPath; }
    public String getCurrency() { return currency; }
    public String getThemeColor() { return themeColor; }
}
