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
        assertEquals(expense.getId(), actual.getId());
        assertEquals(expense.getServerId(), actual.getServerId());
        assertEquals(expense.getDate(), actual.getDate());
        assertEquals(expense.getAmount(), actual.getAmount());
        assertEquals(expense.getComment(), actual.getComment());
        assertEquals(expense.getDescription(), actual.getDescription());

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
        assertTrue(
            "Failed to update the expense with id " + id,
            0 < db.update(Expense.class,
            fields,
            "serverId = ?",
            current.getServerId())
        );

        cursor = db.query(Expense.class).getCursor();
        expenses = cupboard().withCursor(cursor).list(Expense.class);
        assertEquals(1, expenses.size());
        Expense actual = expenses.get(0);
        assertEquals(expense.getId(), actual.getId());
        assertEquals(expense.getServerId(), actual.getServerId());
        assertEquals(expense.getDate(), actual.getDate());
        assertEquals(expense.getAmount(), actual.getAmount());
        assertEquals(expense.getComment(), actual.getComment());
        assertEquals("a new description", actual.getDescription());
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
        expense.setLocalState(Expense.SYNCHED);
        expense.setServerId("545ea2cbf448feda2d788a7d");
        expense.setDate(new Date());
        expense.setAmount(10.3);
        expense.setComment("Lots of groceries");
        expense.setDescription("Groceries");

        return expense;
    }
}
