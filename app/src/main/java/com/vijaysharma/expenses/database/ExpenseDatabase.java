package com.vijaysharma.expenses.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vijaysharma.expenses.database.models.Expense;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ExpenseDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "expense.db";

    static {
        cupboard().register(Expense.class);
    }

    public ExpenseDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        cupboard().withDatabase(db).createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        cupboard().withDatabase(db).upgradeTables();
    }
}
