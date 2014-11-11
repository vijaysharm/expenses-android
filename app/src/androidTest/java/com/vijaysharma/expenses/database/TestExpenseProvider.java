package com.vijaysharma.expenses.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.vijaysharma.expenses.database.models.Expense;

import java.util.Date;
import java.util.List;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TestExpenseProvider extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        mContext.getContentResolver().delete(
            ExpenseProvider.EXPENSE_URI,
            null,
            null
        );

        Cursor cursor = mContext.getContentResolver().query(
            ExpenseProvider.EXPENSE_URI,
            null,
            null,
            null,
            null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void test_insert() {
        ContentValues expense = create();

        Uri inserted = mContext.getContentResolver().insert(ExpenseProvider.EXPENSE_URI, expense);
        long expenseRowId = ContentUris.parseId(inserted);
        assertTrue(expenseRowId != -1);
    }

    public void test_read() {
        ContentValues expense = create();
        mContext.getContentResolver().insert(ExpenseProvider.EXPENSE_URI, expense);

        Cursor cursor = mContext.getContentResolver().query(
            ExpenseProvider.EXPENSE_URI,
            new String[]{"_id", "amount", "serverId", "description", "comment", "date"},
            null,
            null,
            null
        );

        List<Expense> expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        cursor.close();
    }

    public void test_read_by_id() {
        ContentValues expense = create();
        mContext.getContentResolver().insert(ExpenseProvider.EXPENSE_URI, expense);

        Cursor cursor = mContext.getContentResolver().query(
            ExpenseProvider.EXPENSE_URI,
            new String[]{"_id", "amount", "serverId", "description", "comment", "date"},
            "serverId = ?",
            new String[]{"545ea2cbf448feda2d788a7d"},
            null
        );

        List<Expense> expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        cursor.close();
    }

    public void test_delete_expenses() {
        ContentValues expense = create();
        mContext.getContentResolver().insert(ExpenseProvider.EXPENSE_URI, expense);

        int deleted = mContext.getContentResolver().delete(
            ExpenseProvider.EXPENSE_URI,
            null,
            null
        );
        assertTrue(deleted == 1);
    }

    public void test_update_expense() {
        ContentValues expense = new ContentValues();
        expense.put("serverId", "545ea2cbf448feda2d788a7c");
        expense.put("description", "an expense");
        expense.put("comment", "so much money");
        expense.put("amount", 10.2);
        expense.put("date", new Date().getTime());
        mContext.getContentResolver().insert(ExpenseProvider.EXPENSE_URI, expense);

        ContentValues values = new ContentValues();
        values.put("amount", 94.0);

        int updated = mContext.getContentResolver().update(
            ExpenseProvider.EXPENSE_URI,
            values,
            "serverId = ?",
            new String[]{"545ea2cbf448feda2d788a7c"}
        );

        assertTrue(updated == 1);
    }

    private static ContentValues create() {
        ContentValues expense = new ContentValues();
        expense.put("serverId", "545ea2cbf448feda2d788a7d");
        expense.put("description", "an expense");
        expense.put("comment", "so much money");
        expense.put("amount", 10.2);
        expense.put("date", new Date().getTime());

        return expense;
    }
}
