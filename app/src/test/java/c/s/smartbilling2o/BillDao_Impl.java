package c.s.smartbilling2o;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Generated;

import c.s.smartbilling2o.model.Bill;
import c.s.smartbilling2o.database.BillDao;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BillDao_Impl implements BillDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Bill> __insertionAdapterOfBill;

  private final EntityDeletionOrUpdateAdapter<Bill> __deletionAdapterOfBill;

  public BillDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBill = new EntityInsertionAdapter<Bill>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `bills` (`id`,`invoiceNumber`,`customerName`,`date`,`totalAmount`,`pdfPath`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Bill entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getInvoiceNumber() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getInvoiceNumber());
        }
        if (entity.getCustomerName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getCustomerName());
        }
        if (entity.getDate() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDate());
        }
        statement.bindDouble(5, entity.getTotalAmount());
        if (entity.getPdfPath() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getPdfPath());
        }
      }
    };
    this.__deletionAdapterOfBill = new EntityDeletionOrUpdateAdapter<Bill>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `bills` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Bill entity) {
        statement.bindLong(1, entity.getId());
      }
    };
  }

  @Override
  public void insertBill(final Bill bill) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfBill.insert(bill);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void deleteBill(final Bill bill) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfBill.handle(bill);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public List<Bill> getAllBills() {
    final String _sql = "SELECT * FROM bills ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
      final int _cursorIndexOfInvoiceNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "invoiceNumber");
      final int _cursorIndexOfCustomerName = CursorUtil.getColumnIndexOrThrow(_cursor, "customerName");
      final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
      final int _cursorIndexOfTotalAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "totalAmount");
      final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
      final List<Bill> _result = new ArrayList<Bill>(_cursor.getCount());
      while (_cursor.moveToNext()) {
        final Bill _item;
        final String _tmpInvoiceNumber;
        if (_cursor.isNull(_cursorIndexOfInvoiceNumber)) {
          _tmpInvoiceNumber = null;
        } else {
          _tmpInvoiceNumber = _cursor.getString(_cursorIndexOfInvoiceNumber);
        }
        final String _tmpCustomerName;
        if (_cursor.isNull(_cursorIndexOfCustomerName)) {
          _tmpCustomerName = null;
        } else {
          _tmpCustomerName = _cursor.getString(_cursorIndexOfCustomerName);
        }
        final String _tmpDate;
        if (_cursor.isNull(_cursorIndexOfDate)) {
          _tmpDate = null;
        } else {
          _tmpDate = _cursor.getString(_cursorIndexOfDate);
        }
        final double _tmpTotalAmount;
        _tmpTotalAmount = _cursor.getDouble(_cursorIndexOfTotalAmount);
        final String _tmpPdfPath;
        if (_cursor.isNull(_cursorIndexOfPdfPath)) {
          _tmpPdfPath = null;
        } else {
          _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
        }
        _item = new Bill(_tmpInvoiceNumber,_tmpCustomerName,_tmpDate,_tmpTotalAmount,_tmpPdfPath);
        final int _tmpId;
        _tmpId = _cursor.getInt(_cursorIndexOfId);
        _item.setId(_tmpId);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
