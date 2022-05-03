package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.SQLiteConnector;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class SQLiteAccountDAO implements AccountDAO {
    private SQLiteConnector sqLiteConnector;

    public SQLiteAccountDAO(Context context) {
//        sqLiteConnector = new SQLiteConnector(context);
    sqLiteConnector = SQLiteConnector.getInstance(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = sqLiteConnector.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT `accountNo` FROM `account`", null);

        List<String> accountNumbers = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
                accountNumbers.add(accountNo);
            }
        } finally {
            cursor.close();
        }
        return accountNumbers;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase database = sqLiteConnector.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM `account`", null);

        List<Account> accounts = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                // get details of account
                String accountNo = cursor.getString(cursor.getColumnIndex("accountNo"));
                String bankName = cursor.getString(cursor.getColumnIndex("bankName"));
                String accountHolderName = cursor.getString(cursor.getColumnIndex("accountHolderName"));
                double balance = cursor.getDouble(cursor.getColumnIndex("balance"));

                // create an account object
                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accounts.add(account);
            }
        } finally {
            cursor.close();
        }
        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = sqLiteConnector.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM `account` WHERE `accountNo` = ?", new String[] {accountNo});
        Account account = null;

        try {
            if (cursor.getCount() == 0) {
                throw new InvalidAccountException("Account " + accountNo + " is invalid.");
            }
            if (cursor.moveToFirst()) {
                // get details of account
                String bankName = cursor.getString(cursor.getColumnIndex("bankName"));
                String accountHolderName = cursor.getString(cursor.getColumnIndex("accountHolderName"));
                double balance = cursor.getDouble(cursor.getColumnIndex("balance"));

                // create an account object
                account = new Account(accountNo, bankName, accountHolderName, balance);
            }
        } finally {
            cursor.close();
        }
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase database = sqLiteConnector.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo", account.getAccountNo());
        contentValues.put("bankName", account.getBankName());
        contentValues.put("accountHolderName", account.getAccountHolderName());
        contentValues.put("balance", account.getBalance());

        database.insert("`account`", null, contentValues);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = sqLiteConnector.getWritableDatabase();
        getAccount(accountNo);

        database.delete("`account`", "`accountNo`=?", new String[] {accountNo});

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = sqLiteConnector.getWritableDatabase();

        Account account = getAccount(accountNo);
        ContentValues contentValues = new ContentValues();

        if (expenseType == ExpenseType.EXPENSE) {
            contentValues.put("balance", account.getBalance() - amount);
        } else if (expenseType == ExpenseType.INCOME){
            contentValues.put("balance", account.getBalance() + amount);
        }

        database.update("`account`", contentValues, "`accountNo` = ?", new String[] {accountNo});
    }
}
