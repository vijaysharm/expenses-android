package com.vijaysharma.expenses.features.list;

import android.content.Context;

import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.service.ExpenseService;
import com.vijaysharma.expenses.service.Service;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class ExpenseListOperations {
    private final ExpenseListStorage storage;
    public ExpenseListOperations(Context context) {
        storage = new ExpenseListStorage(context);
    }

    public Observable<List<Expense>> fetch() {
        return storage
            .read()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toList();
    }

    public Subscription refresh(String token) {
        return Service.fetch(token)
            .flatMap(flatten())
            .map(dtoToDomain())
            .observeOn(Schedulers.io())
            .subscribe(storage.save());
    }

    public Observable<Expense> newItem() {
        return storage
            .newItems()
            .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Expense> updatedItem() {
        return storage
            .updatedItems()
            .observeOn(AndroidSchedulers.mainThread());
    }

    private Func1<List<ExpenseService.Expense>, Observable<ExpenseService.Expense>> flatten() {
        return new Func1<List<ExpenseService.Expense>, Observable<ExpenseService.Expense>>() {
            @Override
            public Observable<ExpenseService.Expense> call(List<ExpenseService.Expense> expenses) {
                return Observable.from(expenses);
            }
        };
    }

    private Func1<ExpenseService.Expense, Expense> dtoToDomain() {
        return new Func1<ExpenseService.Expense, Expense>() {
            @Override
            public Expense call(ExpenseService.Expense dto) {
                Expense model = new Expense();
                model.serverId = dto.getId();
                model.description = dto.getDescription();
                model.comment = dto.getComment();
                model.amount = dto.getAmount();
                model.date = dto.getDate();
                return model;
            }
        };
    }

}
