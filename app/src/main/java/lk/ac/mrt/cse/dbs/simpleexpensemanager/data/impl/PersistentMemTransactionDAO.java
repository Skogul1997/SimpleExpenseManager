package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentMemTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    public static final String DATABASE_NAME = "170307M.db";
    public static final String TABLE_NAME = "transactions";
    public static final String transid = "Transid";
    public static final String accnum = "accountno";
    public static final String date = "date";
    public static final String type = "type";
    public static final String amounts = "amount";

    private List<Transaction> Transactions;
    public PersistentMemTransactionDAO(Context context) {
        super(context,DATABASE_NAME, null,1);
        Transactions = new LinkedList<>();
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_NAME+
                "(Transid varchar primary key autoincrement, accountno varchar,date String,type text,amount double)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(database);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        String accountno = transaction.getAccountNo();
        Date dates = transaction.getDate();

        String Date = dates.toString();
        ExpenseType types = transaction.getExpenseType();
        String strType = types.toString();
        Double amounts = transaction.getAmount();

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues Contentval = new ContentValues();
        Contentval.put("accountno", accountno);
        Contentval.put("amount", amounts);
        Contentval.put("type",strType);
        Contentval.put("date", Date);

        database.insert(TABLE_NAME, null, Contentval);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        Transactions.clear();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result =  db.rawQuery( " select * from " + TABLE_NAME, null );

        result.moveToFirst();

        while(result.isAfterLast() == false){

            String accountNo = result.getString(result.getColumnIndex(accnum));
            Double amount = result.getDouble(result.getColumnIndex(amounts));
            String transType = result.getString(result.getColumnIndex(type));

            ExpenseType type = ExpenseType.valueOf(transType);
            String dates = result.getString(result.getColumnIndex(date));
            Date date1=new SimpleDateFormat("dd/MM/yyyy").parse(dates);

            Transactions.add(new Transaction(date1,accountNo,type,amount));
            result.moveToNext();
        }
        return Transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int lim) {
        int size = Transactions.size();
        if (size <= lim) {
            return Transactions;
        }
        return Transactions.subList(size - lim, size);
    }
}
