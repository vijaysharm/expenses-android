package com.vijaysharma.expenses.features.list;

import com.vijaysharma.expenses.database.models.Expense;
import com.vijaysharma.expenses.misc.ObserverAdapter;

import java.util.List;

import rx.subscriptions.CompositeSubscription;

public class ExpenseListController {
    public interface ExpenseListView {
        void showError();
        void add(Expense expense);
        void refresh(List<Expense> expenses);
    }

    private final CompositeSubscription subscriptions;
    private final ExpenseListOperations operations;
    private final ObserverAdapter<List<Expense>> fetchItems;

    private ExpenseListView view;

    public ExpenseListController(ExpenseListOperations operations) {
        this.subscriptions = new CompositeSubscription();
        this.operations = operations;

        fetchItems = new ObserverAdapter<List<Expense>>() {
            @Override
            public void onNext(List<Expense> expenses) {
                if (view != null)
                    view.refresh(expenses);
            }
        };
        subscriptions.add(operations.newItem().subscribe(new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                if ( view != null ) {
                    view.add(expense);
                }
            }
        }));
        subscriptions.add(operations.updatedItem().subscribe(new ObserverAdapter<Expense>() {
            @Override
            public void onNext(Expense expense) {
                subscriptions.add(ExpenseListController.this.operations.fetch().subscribe(fetchItems));
            }
        }));
        subscriptions.add(operations.exception().subscribe(new ObserverAdapter<Throwable>() {
            @Override
            public void onNext(Throwable throwable) {
                if ( view != null ) {
                    view.showError();
                }
            }
        }));
    }

    public void attach(ExpenseListView view) {
        this.view = view;
    }

    public void detach(ExpenseListView view) {
        this.view = null;
    }

    public void fetch() {
        subscriptions.add(operations.fetch().subscribe(fetchItems));
    }

    public void refresh() {
        operations.getToken()
            .subscribe(new ObserverAdapter<String>() {
                @Override
                public void onNext(String token) {
                    subscriptions.add(operations.refresh(token));
                }
            });
    }
}
