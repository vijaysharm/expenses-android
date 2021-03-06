package com.vijaysharma.expenses.features.list;

import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.service.ExpenseService;

import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class ExpenseListOperations {
    private final ExpenseListStorage storage;
    private final ExpenseListService service;
    private final Scheduler dbThread;
    private final Scheduler mainThread;
    private final PublishSubject<Throwable> refreshError;

    public ExpenseListOperations(
        ExpenseListStorage storage,
        ExpenseListService service,
        Scheduler mainThread
    ) {
        this.storage = storage;
        this.service = service;
        this.dbThread = Schedulers.io();
        this.mainThread = mainThread;
        this.refreshError = PublishSubject.create();
    }

    public Observable<List<Expense>> fetch() {
        return storage
            .readAll()
            .subscribeOn(dbThread)
            .observeOn(mainThread)
            .toList();
    }

    public Subscription refresh(String token) {
        return service.fetch(token)
            .observeOn(dbThread)
            .flatMap(flatten())
            .map(dtoToDomain())
            .doOnError(publishExceptions())
            .doOnEach(storage.save())
            .subscribe(Observers.empty());
    }

    /*
    private void doAll(String token) {
        storage.readByStatus(Expense.NEW)
            .flatMap(new Func1<Expense, Observable<?>>() {
                @Override
                public Observable<?> call(Expense expense) {
                    return ;
                }
            });
    }*/

    private Action1<Throwable> publishExceptions() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // TODO: I shouldn't let the observer have to cast the exception
                // TODO: and know that we're using retrofit
                refreshError.onNext(throwable);
            }
        };
    }

    public Observable<Throwable> exception() {
        // TODO: Merge with exceptions from the DB
        return refreshError
            .observeOn(mainThread);
    }

    public Observable<Expense> newItem() {
        return storage
            .newItems()
            .observeOn(mainThread);
    }

    public Observable<Expense> updatedItem() {
        return storage
            .updatedItems()
            .observeOn(mainThread);
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
                model.setServerId(dto.getId());
                model.setDescription(dto.getDescription());
                model.setComment(dto.getComment());
                model.setAmount(dto.getAmount());
                model.setDate(dto.getDate());
                model.setLocalState(Expense.SYNCHED);

                return model;
            }
        };
    }

    public Observable<String> getToken() {
        return storage.getToken();
    }
}
