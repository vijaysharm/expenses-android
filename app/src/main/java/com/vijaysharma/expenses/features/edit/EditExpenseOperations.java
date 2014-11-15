package com.vijaysharma.expenses.features.edit;

import android.content.Context;

import com.vijaysharma.expenses.database.models.Expense;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.Subscribers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class EditExpenseOperations {
    private final EditExpenseStorage storage;
    private final Scheduler dbThread;
    private final Scheduler mainThread;
    private final PublishSubject<Throwable> errors;

    public EditExpenseOperations(Context context) {
        storage = new EditExpenseStorage(context);
        dbThread = Schedulers.io();
        mainThread = AndroidSchedulers.mainThread();
        errors = PublishSubject.create();
    }

    public Observable<Expense> fetch(long expenseId) {
        return storage
            .find(expenseId)
            .subscribeOn(dbThread)
            .observeOn(mainThread);
    }

    public Observable<Throwable> errors() {
        return errors.observeOn(mainThread);
    }

    public Observable<Expense> saved() {
        return storage.saved().observeOn(mainThread);
    }

    public Subscription save(Expense expense) {
        return storage
            .insert(expense)
            .subscribeOn(dbThread)
            .subscribe(Subscribers.empty());
    }

    public Subscription update(long expenseId, Expense expense) {
        return storage
            .update(expenseId, expense)
            .subscribeOn(dbThread)
            .subscribe(Subscribers.empty());
    }
}
