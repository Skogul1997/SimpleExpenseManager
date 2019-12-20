package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentMemAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private static final String DATABASE_NAME = "170307M.db";
    private static final String TABLE_NAME = "Account";
    private static final String accnum = "accountno";
    private static final String bankname = "bankname";
    private static final String holdername = "accountholdername";
    private static final String bal = "balance";

    public PersistentMemAccountDAO(Context context) {
        super(context, DATABASE_NAME , null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("create table " + TABLE_NAME+
                "(accountno varchar primary key , bankname text,accountholdername text,balance double)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i1) {
        database.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
        onCreate(database);
    }
    @Override
    public List<String> getAccountNumbersList() {
        ArrayList<String> accountNum = new ArrayList<>();
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME, null );
        result.moveToFirst();

        while(result.isAfterLast() == false){
            accountNum.add(result.getString(result.getColumnIndex(accnum)));
            result.moveToNext();
        }
        return accountNum;


    }

    @Override
    public List<Account> getAccountsList()
    {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME, null );
        ArrayList<Account> accounts = new ArrayList<>();

        result.moveToFirst();

        while(result.isAfterLast() == false){
            String accountNo = result.getString(result.getColumnIndex(accnum));
            String bankName = result.getString(result.getColumnIndex(bankname));
            String accountHolderName = result.getString(result.getColumnIndex(holdername));
            Double balance = result.getDouble(result.getColumnIndex(bal));

            accounts.add(new Account(accountNo,bankName,accountHolderName,balance));
            result.moveToNext();
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor result =  database.rawQuery( "select * from "+TABLE_NAME+" where accountno="+accountNo+"", null );

        String accountno = result.getString(result.getColumnIndex(accnum));
        String bankName = result.getString(result.getColumnIndex(bankname));
        String accountHolderName = result.getString(result.getColumnIndex(holdername));
        Double balance = result.getDouble(result.getColumnIndex(bal));

        return  new Account(accountno,bankName,accountHolderName,balance);


    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contVal = new ContentValues();

        String accountnum = account.getAccountNo();
        String bankname = account.getBankName();
        String holdername = account.getAccountHolderName();
        Double bal = account.getBalance();

        contVal.put("accountno", accountnum);
        contVal.put("bankname", bankname);
        contVal.put("accountholdername", holdername);
        contVal.put("balance", bal);

        database.insert(TABLE_NAME, null, contVal);

    }

    @Override
    public void removeAccount(String accountNumber) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME,
                "accountno = ? ",
                new String[] { accountNumber});
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor result =  database.rawQuery( "select balance from "+TABLE_NAME+" where accountno="+accountNo+"", null );

        Double balance = result.getDouble(result.getColumnIndex(bal));
        Double mbalance;

        ContentValues cV = new ContentValues();

        switch (expenseType) {
            case EXPENSE:
                mbalance = balance - amount;
                cV.put("balance", balance);
                break;
            case INCOME:
                mbalance = balance + amount;
                cV.put("balance", balance);
                break;
        }
    }
}
