package com.vijaysharma.expenses.features.list;

import android.content.ContentValues;
import android.content.Context;

import com.vijaysharma.expenses.misc.ObserverAdapter;
import com.vijaysharma.expenses.database.ExpenseDatabase;
import com.vijaysharma.expenses.database.models.Expense;

import nl.qbusict.cupboard.QueryResultIterable;
import rx.Observable;
import rx.Observer;
import rx.subjects.PublishSubject;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class ExpenseListStorage {
    private final ExpenseDatabase db;
    private final PublishSubject<Expense> newItems;
    private final PublishSubject<Expense> updatedItems;

    public ExpenseListStorage(Context context) {
        db = new ExpenseDatabase(context);
        newItems = PublishSubject.create();
        updatedItems = PublishSubject.create();
    }

    public PublishSubject<Expense> newItems() {
        return newItems;
    }

    public PublishSubject<Expense> updatedItems() {
        return updatedItems;
    }

    public Observable<Expense> readAll() {
        return Observable.from(fetchFromDb());
    }

    public Observable<Expense> readByStatus(int... statuses) {
        return Observable.from(fetchFromDbWithLocalStatus(statuses));
    }

    public Observer<? super Expense> save() {
        return new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                try {
                    Expense stored = findInDbByServerId(expense.getServerId());
                    if ( stored == null ) {
                        storeExpenseInDb(expense);
                        newItems.onNext(findInDbByServerId(expense.getServerId()));
                    } else {
                        updateExpenseInDb(expense);
                        updatedItems.onNext(findInDbByServerId(expense.getServerId()));
                    }
                } catch (Exception e) {
                    // There was an exception with this item. Oh well..
                }
            }
        };
    }

    private long storeExpenseInDb(Expense expense) {
        return cupboard()
            .withDatabase(db.getWritableDatabase())
            .put(expense);
    }

    private void updateExpenseInDb(Expense expense) {
        ContentValues values = new ContentValues();
        values.put("description", expense.getDescription());
        values.put("comment", expense.getComment());
        values.put("date", expense.getDate().getTime());
        values.put("amount", expense.getAmount());

        cupboard()
            .withDatabase(db.getWritableDatabase())
            .update(Expense.class, values, "serverId = ?", expense.getServerId());
    }

    private Expense findInDbByServerId(String serverId) {
        return cupboard()
            .withDatabase(db.getReadableDatabase())
            .query(Expense.class)
            .withSelection("serverId = ?", serverId)
            .get();
    }

    private QueryResultIterable<Expense> fetchFromDbWithLocalStatus(int... statuses) {
        StringBuilder selection = new StringBuilder();
        String[] args = new String[statuses.length];
        for ( int index = 0; index < statuses.length; index++ ) {
            if ( index != 0 )
                selection.append(", ");

            selection.append("status = ?");
            args[index] = String.valueOf(statuses[index]);
        }

        return cupboard()
            .withDatabase(db.getReadableDatabase())
            .query(Expense.class)
            .withSelection(selection.toString(), args).query();
    }

    private QueryResultIterable<Expense> fetchFromDb() {
        return cupboard()
            .withDatabase(db.getReadableDatabase())
            .query(Expense.class)
            .query();
    }
}
