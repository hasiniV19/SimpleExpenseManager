package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db.SQLiteConnector;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteTransactionDAO implements TransactionDAO {
    private SQLiteConnector sqLiteConnector;

    public SQLiteTransactionDAO(Context context) {
//        sqLiteConnector = new SQLiteConnector(context);
        sqLiteConnector = SQLiteConnector.getInstance(context);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase database = sqLiteConnector.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        ContentValues contentValues = new ContentValues();
        contentValues.put("date", dateFormat.format(date));
        contentValues.put("accountNo", accountNo);
        contentValues.put("expenseType", expenseType.toString());
        contentValues.put("amount", amount);

        database.insert("`transaction`", null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        SQLiteDatabase database = sqLiteConnector.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM `transaction`", null);

        return getTransactionLogsList(cursor);
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        SQLiteDatabase database = sqLiteConnector.getReadableDatabase();

        Cursor cursor = database.rawQuery("SELECT * FROM `transaction` LIMIT " + limit, null);

        return getTransactionLogsList(cursor);
    }

    private List<Transaction> getTransactionLogsList(Cursor cursor) {
        List<Transaction> transactions = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                // get details of a transaction

                // create date object
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = simpleDateFormat.parse(cursor.getString(cursor.getColumnIndexOrThrow("date")));

                // accountNo
                String accountNo = cursor.getString(cursor.getColumnIndexOrThrow("accountNo"));

                // get expense type
                String expenseTypeStr = cursor.getString(cursor.getColumnIndexOrThrow("expenseType"));
                ExpenseType expenseType = null;
                if (expenseTypeStr.equals(ExpenseType.EXPENSE.toString())) {
                    expenseType = ExpenseType.EXPENSE;
                } else if (expenseTypeStr.equals(ExpenseType.INCOME.toString())) {
                    expenseType = ExpenseType.INCOME;
                }

                // amount
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

                // create a transaction object
                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);

                // add transaction to transactions list
                transactions.add(transaction);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        return transactions;
    }
}
