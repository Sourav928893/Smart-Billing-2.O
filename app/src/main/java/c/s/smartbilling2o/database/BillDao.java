package c.s.smartbilling2o.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import c.s.smartbilling2o.model.Bill;

@Dao
public interface BillDao {
    @Query("SELECT * FROM bills ORDER BY id DESC")
    List<Bill> getAllBills();

    @Insert
    void insertBill(Bill bill);

    @Delete
    void deleteBill(Bill bill);
}
