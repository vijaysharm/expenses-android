package com.vijaysharma.expenses.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.vijaysharma.expenses.database.models.Expense;

import java.util.Date;
import java.util.List;

import nl.qbusict.cupboard.DatabaseCompartment;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class TestExpenseDatabase extends AndroidTestCase {
    private ExpenseDatabase database;

    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase("expense.db");
        database = new ExpenseDatabase(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        database.close();
    }

    public void test_insert_Expense() {
        Expense expense = create();
        assertTrue("Failed to insert row", cupboard().withDatabase(database.getWritableDatabase()).put(expense) > 0);
    }

    public void test_query_Expense() {
        Expense expense = create();
        DatabaseCompartment db = cupboard().withDatabase(database.getWritableDatabase());
        long id = db.put(expense);
        assertTrue("Failed to insert row", id > 0);

        Cursor cursor = db.query(Expense.class).getCursor();
        List<Expense> expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        Expense actual = expenses.get(0);
        assertEquals(expense._id, actual._id);
        assertEquals(expense.serverId, actual.serverId);
        assertEquals(expense.date, actual.date);
        assertEquals(expense.amount, actual.amount);
        assertEquals(expense.comment, actual.comment);
        assertEquals(expense.description, actual.description);

        cursor.close();
    }

    public void test_update_Expense() {
        Expense expense = create();
        DatabaseCompartment db = cupboard().withDatabase(database.getWritableDatabase());
        long id = db.put(expense);
        assertTrue("Failed to insert row", id > 0);

        Cursor cursor = db.query(Expense.class).getCursor();
        List<Expense> expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        Expense current = expenses.get(0);

        ContentValues fields = new ContentValues();
        fields.put("description", "a new description");
        assertTrue("Failed to update the expense with id " + id, 0 < db.update(Expense.class, fields, "serverId = ?", current.serverId));

        cursor = db.query(Expense.class).getCursor();
        expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        Expense actual = expenses.get(0);
        assertEquals(expense._id, actual._id);
        assertEquals(expense.serverId, actual.serverId);
        assertEquals(expense.date, actual.date);
        assertEquals(expense.amount, actual.amount);
        assertEquals(expense.comment, actual.comment);
        assertEquals("a new description", actual.description);
    }

    public void test_delete_Expense() {
        Expense expense = create();
        DatabaseCompartment db = cupboard().withDatabase(database.getWritableDatabase());
        long id = db.put(expense);
        assertTrue("Failed to insert row", id > 0);
        assertTrue("Failed to delete " + id , db.delete(Expense.class, id));
    }

    private Expense create() {
        Expense expense = new Expense();
        expense.serverId = "545ea2cbf448feda2d788a7d";
        expense.date = new Date();
        expense.amount = 10.3f;
        expense.comment = "Lots of groceries";
        expense.description = "Groceries";
        return expense;
    }
}
