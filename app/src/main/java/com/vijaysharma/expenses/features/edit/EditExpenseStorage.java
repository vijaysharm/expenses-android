package com.vijaysharma.expenses.features.edit;

import android.content.ContentValues;

import com.vijaysharma.expenses.database.ExpenseDatabase;
import com.vijaysharma.expenses.database.models.Expense;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

import static nl.qbusict.cupboard.CupboardFactory.cupboard;

public class EditExpenseStorage {
    private final ExpenseDatabase database;
    private final PublishSubject<Expense> saved;

    public EditExpenseStorage(ExpenseDatabase database) {
        this.database = database;
        this.saved = PublishSubject.create();
    }

    public Observable<Expense> find(long expenseId) {
        return Observable.just(
            cupboard()
                .withDatabase(database.getReadableDatabase())
                .get(Expense.class, expenseId)
        );
    }

    public Observable<Expense> insert(final Expense expense) {
        return Observable.create(new Observable.OnSubscribe<Expense>() {
            @Override
            public void call(Subscriber<? super Expense> subscriber) {
                try {
                    long expenseId = cupboard()
                        .withDatabase(database.getWritableDatabase())
                        .put(expense);
                    subscriber.onNext(findInDbByLocalId(expenseId));
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        }).doOnNext(notifySaved());
    }

    public Observable<Expense> update(final long id, final Expense expense) {
        return Observable.create(new Observable.OnSubscribe<Expense>() {
            @Override
            public void call(Subscriber<? super Expense> subscriber) {
                try {
                    Expense stored = findInDbByLocalId(id);
                    if ( stored == null ) {
                        long expenseId = cupboard()
                            .withDatabase(database.getWritableDatabase())
                            .put(expense);
                        subscriber.onNext(findInDbByLocalId(expenseId));
                    } else {
                        ContentValues values = new ContentValues();
                        values.put("description", expense.getDescription());
                        values.put("comment", expense.getComment());
                        values.put("date", expense.getDate().getTime());
                        values.put("amount", expense.getAmount());
                        cupboard()
                            .withDatabase(database.getWritableDatabase())
                            .update(Expense.class, values, "_id = ?", String.valueOf(id));
                        subscriber.onNext(findInDbByLocalId(id));
                    }

                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        }).doOnNext(notifySaved());
    }

    public Observable<Expense> saved() {
        return saved;
    }

    private Action1<? super Expense> notifySaved() {
        return new Action1<Expense>() {
            @Override
            public void call(Expense expense) {
                saved.onNext(expense);
            }
        };
    }

    private Expense findInDbByLocalId(long serverId) {
        return cupboard()
            .withDatabase(database.getReadableDatabase())
            .query(Expense.class)
            .byId(serverId)
            .get();
    }
}
