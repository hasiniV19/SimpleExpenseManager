package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class SQLiteConnector extends SQLiteOpenHelper {
    public static SQLiteConnector sqLiteConnector;

    public static SQLiteConnector getInstance(Context context) {
        if (sqLiteConnector == null) {
            sqLiteConnector = new SQLiteConnector(context);
        }
        return sqLiteConnector;
    }

    private SQLiteConnector(@Nullable Context context) {
        super(context, "190647X", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // CREATE TABLE account
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `account` (" +
                "`accountNo` VARCHAR(255) NOT NULL," +
                " `bankName` VARCHAR(255) NOT NULL, " +
                "`accountHolderName` VARCHAR(255) NOT NULL, " +
                "`balance` DOUBLE NOT NULL," +
                " PRIMARY KEY (`accountNo`))");

        // CREATE TABLE transaction
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS `transaction` (" +
                "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                "`accountNo` VARCHAR(255) NOT NULL," +
                "`date` DATE NOT NULL," +
                "`expenseType` VARCHAR(255) NOT NULL," +
                "`amount` DOUBLE NOT NULL," +
                "CONSTRAINT fk_account FOREIGN KEY (`accountNo`) REFERENCES `account`(`accountNo`))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // drop table account
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS `account`");
        // drop table transaction
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS `transaction`");

        onCreate(sqLiteDatabase);
    }
}
